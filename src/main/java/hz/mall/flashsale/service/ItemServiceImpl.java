package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.ItemConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.ItemDo;
import hz.mall.flashsale.domain.ItemStockDo;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.ItemDoMapper;
import hz.mall.flashsale.mapper.ItemStockDoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemConverter itemConverter;
    private final ItemDoMapper itemDoMapper;
    private final ItemStockDoMapper itemStockDoMapper;

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
        return null;
    }

    @Override
    public Item getItemById(Integer id) {
        ItemDo itemDo = itemDoMapper.selectByPrimaryKey(id);
        if (itemDo == null) return null;
        ItemStockDo itemStockDo = itemStockDoMapper.selectByItemId(itemDo.getId());
        Item item = itemConverter.DoToItem(itemDo, itemStockDo);
        return null;
    }
}
