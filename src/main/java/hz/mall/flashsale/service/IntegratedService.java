package hz.mall.flashsale.service;

import hz.mall.flashsale.error.BusinessException;

public interface IntegratedService {
    String generateFlashSaleToken(Integer promoId, Integer itemId, Integer userId) throws BusinessException;

    void publishPromo(Integer promoId);
}
