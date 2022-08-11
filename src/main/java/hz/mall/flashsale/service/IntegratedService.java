package hz.mall.flashsale.service;

public interface IntegratedService {
    String generateFlashSaleToken(Integer promoId, Integer itemId, Integer userId);
}
