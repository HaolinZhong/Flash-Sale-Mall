package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.Order;
import hz.mall.flashsale.error.BusinessException;

public interface OrderService {
    Order createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
