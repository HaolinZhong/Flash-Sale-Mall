package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.OrderConverter;
import hz.mall.flashsale.domain.*;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.OrderDoMapper;
import hz.mall.flashsale.mapper.SequenceDoMapper;
import hz.mall.flashsale.mapper.StockLogDoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderDoMapper orderDoMapper;
    @Autowired
    private SequenceDoMapper sequenceDoMapper;
    @Autowired
    private StockLogDoMapper stockLogDoMapper;

    @Override
    @Transactional
    public Order createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId, String stockLogId) throws BusinessException {

        /**
         * The function will be used when producer send transaction message of synchronizing db & cache item stock.
         * It has 4 steps:
         *  1. validate args
         *  2. decrease stock in cache
         *  3. create an order and persists it into db
         *  4. set stock log status to success
         *
         */
        // 1. validation
        Item item = itemService.getItemByIdInCache(itemId);
        if (item == null) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "invalid item information");
        }

        // 2. decrease stock (in cache) when an order was placed (instead of decreasing when paid)
        boolean assigned = itemService.decreaseStock(itemId, amount);

        if (!assigned) throw new BusinessException(BusinessErrEnum.STOCK_NOT_ENOUGH);

        // 3. persist order, ensure consistency of stock in cache & stock in db

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

        // 4. set stock log status to 2 (success)
        StockLogDo stockLogDo = stockLogDoMapper.selectByPrimaryKey(stockLogId);
        if (stockLogDo == null) throw new BusinessException(BusinessErrEnum.UNKNOW_ERROR);
        stockLogDo.setStatus(2);
        stockLogDoMapper.updateByPrimaryKeySelective(stockLogDo);

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
        sb.append(now.format(DateTimeFormatter.ISO_DATE).replace("-", ""));

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
