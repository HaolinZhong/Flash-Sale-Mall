package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.Promo;

public interface PromoService {

    // get ongoing or incoming flash sale event
    Promo getPromoByItemId(Integer itemId);

}
