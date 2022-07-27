package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.ItemDo;
import hz.mall.flashsale.domain.ItemStockDo;
import hz.mall.flashsale.web.model.ItemVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ItemConverter {

    @Mapping(source = "itemDo.id", target = "id")
    Item DoToItem(ItemDo itemDo, ItemStockDo itemStockDo);

    ItemDo itemToItemDo(Item item);

    @Mapping(source = "id", target = "itemId")
    ItemStockDo itemToItemStockDo(Item item);

    ItemVo itemToItemVo(Item item);
}
