package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.ItemDo;
import hz.mall.flashsale.domain.ItemStockDo;
import hz.mall.flashsale.web.model.ItemVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(uses={DateConverter.class})
public interface ItemConverter {

    @Mapping(source = "itemDo.id", target = "id")
    Item DoToItem(ItemDo itemDo, ItemStockDo itemStockDo);

    ItemDo itemToItemDo(Item item);

    @Mapping(source = "id", target = "itemId")
    ItemStockDo itemToItemStockDo(Item item);

    @Mapping(source = "promo.id", target = "promoId")
    @Mapping(source = "promo.status", target = "promoStatus", defaultValue = "0")
    @Mapping(source = "promo.promoItemPrice", target = "promoPrice")
    @Mapping(source = "promo.startDate", target = "startDate")
    ItemVo itemToItemVo(Item item);
}
