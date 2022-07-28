package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.OrderConverter;
import hz.mall.flashsale.domain.*;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.OrderDoMapper;
import hz.mall.flashsale.mapper.SequenceDoMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderConverter orderConverter;
    private final ItemService itemService;
    private final UserService userService;
    private final OrderDoMapper orderDoMapper;
    private final SequenceDoMapper sequenceDoMapper;

    @Override
    @Transactional
    public Order createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId) throws BusinessException {

        // 1. validation
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "invalid item information");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "invalid user information");
        }

        if (amount <= 0 || amount > 99) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "invalid amount");
        }

        if (promoId != null) {
            if (promoId.intValue() != item.getPromo().getId()) {
                throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "invalid promo information");
            }

            if (item.getPromo().getStatus().intValue() != 2) {
                throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "promo has not started");
            }
        }

        // 2. decrease stock when an order was placed (instead of decreasing when paid)
        boolean assigned = itemService.decreaseStock(itemId, amount);

        if (!assigned) throw new BusinessException(BusinessErrEnum.STOCK_NOT_ENOUGH);

        // 3. persist order
        Order.OrderBuilder orderBuilder = Order.builder()
                .userId(userId)
                .itemId(itemId)
                .amount(amount)
                .id(generateOrderId());      // generate order id string

        if (promoId != null) {
            orderBuilder.itemPrice(item.getPromo().getPromoItemPrice());
        } else {
            orderBuilder.itemPrice(item.getPrice());
        }

        Order order = orderBuilder
                .promoId(promoId)
                .totalPrice(item.getPrice().multiply(new BigDecimal(amount)))
                .build();

        OrderDo orderDo = orderConverter.orderToDo(order);
        orderDoMapper.insertSelective(orderDo);

        itemService.increaseSales(itemId, amount); // increase sales of the item

        // 4. return to frontend
        return order;
    }


    // use requires_new to achieve that even if outer transaction (create order) failed,
    // the increment of sequence will not roll back, (which is in this inner transaction)
    // to ensure the uniqueness of sequence
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderId() {
        // an order id should have 16 digits

        StringBuilder sb = new StringBuilder();

        // first 8 digits should be date
        LocalDateTime now = LocalDateTime.now();
        sb.append(now.format(DateTimeFormatter.ISO_DATE).replace("-",""));

        // in the middle, 6 digits should be auto increment sequence
        int sequence = 0;
        SequenceDo sequenceDo = sequenceDoMapper.getSequenceByName("order_info");
        sequence = sequenceDo.getCurrentValue();
        sequenceDo.setCurrentValue(sequenceDo.getCurrentValue() + sequenceDo.getStep());
        sequenceDoMapper.updateByPrimaryKeySelective(sequenceDo);

        String seqStr = String.valueOf(sequence);
        for (int i = 0; i < 6 - seqStr.length(); i++) {
            sb.append(0);
        }
        sb.append(seqStr);

        // the final 2 digits will be used in database sharding
        sb.append("00");

        return sb.toString();
    }
}
