package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.Order;
import hz.mall.flashsale.error.BusinessException;

public interface OrderService {

    // get promo id from frontend, then validate whether promo maps to item & promo has started
    Order createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId, String stockLogId) throws BusinessException;

    // another way: determine whether there is a promo event for the item; if the promo exists,
    // create the order with promo price
    // However, an item can be in multiple promo events simultaneous, making this method unfavorable

}
