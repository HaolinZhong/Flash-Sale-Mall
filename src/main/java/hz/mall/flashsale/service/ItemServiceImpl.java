package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.ItemConverter;
import hz.mall.flashsale.domain.*;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.ItemDoMapper;
import hz.mall.flashsale.mapper.ItemStockDoMapper;
import hz.mall.flashsale.mapper.PromoDoMapper;
import hz.mall.flashsale.mapper.StockLogDoMapper;
import hz.mall.flashsale.mq.DecreaseStockProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemConverter itemConverter;
    @Autowired
    private ItemDoMapper itemDoMapper;
    @Autowired
    private ItemStockDoMapper itemStockDoMapper;
    @Autowired
    private PromoService promoService;
    @Autowired
    private PromoDoMapper promoDoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StockLogDoMapper stockLogDoMapper;

    @Override
    @Transactional
    public Item createItem(Item item) throws BusinessException {
        if (item == null) throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR);

        ItemDo itemDo = itemConverter.itemToItemDo(item);
        itemDoMapper.insertSelective(itemDo);

        item.setId(itemDo.getId());

        ItemStockDo itemStockDo = itemConverter.itemToItemStockDo(item);
        itemStockDoMapper.insertSelective(itemStockDo);

        return getItemById(item.getId());
    }

    @Override
    public List<Item> listItem() {
        List<ItemDo> itemDoList = itemDoMapper.listAllItems();
        List<Item> itemList = itemDoList.stream().map(itemDo -> {
            ItemStockDo itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
            Item item = itemConverter.DoToItem(itemDo, itemStockDo);
            return item;
        }).collect(Collectors.toList());
        return itemList;
    }

    @Override
    public Item getItemById(Integer id) {
        ItemDo itemDo = itemDoMapper.selectByPrimaryKey(id);
        if (itemDo == null) return null;
        ItemStockDo itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
        Item item = itemConverter.DoToItem(itemDo, itemStockDo);

        // get promo information related to the item
        Promo promo = promoService.getPromoByItemId(item.getId());
        if (promo != null && promo.getStatus() != 3) {
            item.setPromo(promo);
        }

        return item;
    }


    @Override
    public Item getItemByIdInCache(Integer id) {
        Item item = (Item) redisTemplate.opsForValue().get("item_validate_" + id);
        if (item == null) {
            item = getItemById(id);
            redisTemplate.opsForValue().set("item_validate_" + id, item);
            redisTemplate.expire("item_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return item;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * -1);

        if (result > 0) return true;

        // item sold out
        if (result == 0) {
            // mark the item stock as ran out
            redisTemplate.opsForValue().set("promo_item_stock_invalid_" + itemId, "true");
            return true;
        }

        // not enough stock, add the amount back
        increaseStock(itemId, amount);
        return false;
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) throws BusinessException {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        return true;
    }

//    @Override
//    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
//        return producer.asyncReduceStock(itemId, amount);
//    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDoMapper.increaseSales(itemId, amount);
    }

    @Override
    @Transactional
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDo stockLogDO = new StockLogDo();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setStatus(1);

        stockLogDoMapper.insertSelective(stockLogDO);

        return stockLogDO.getStockLogId();
    }

}
