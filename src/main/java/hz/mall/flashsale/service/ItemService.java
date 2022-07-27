package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.error.BusinessException;

import java.util.List;

public interface ItemService {

    Item createItem(Item item) throws BusinessException;

    List<Item> listItem();

    Item getItemById(Integer id);
}
