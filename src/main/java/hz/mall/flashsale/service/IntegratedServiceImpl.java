package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.PromoConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.Promo;
import hz.mall.flashsale.domain.PromoDo;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.PromoDoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IntegratedServiceImpl implements IntegratedService {

    @Autowired
    private PromoDoMapper promoDoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private PromoConverter promoConverter;

    @Override
    public void publishPromo(Integer promoId) {
        PromoDo promoDo = promoDoMapper.selectByPrimaryKey(promoId);
        if (promoDo.getItemId() == null || promoDo.getItemId().intValue() == 0) {
            return;
        }
        Item item = itemService.getItemById(promoDo.getItemId());

        // set initial item stock in redis
        redisTemplate.opsForValue().set("promo_item_stock_" + item.getId(), item.getStock());

        // set access limit in redis
        // e.g. for 100 stock, only 500 user can access
        redisTemplate.opsForValue().set("promo_access_limit_" + promoId, item.getStock().intValue() * 5);
    }


    @Override
    public String generateFlashSaleToken(Integer promoId, Integer itemId, Integer userId) throws BusinessException {

        // determine whether stock has ran out by ran out key
        // if ran out, no token will be assigned to the incoming user
        if (redisTemplate.hasKey("promo_item_stock_invalid_" + itemId)) {
            return null;
        }

        PromoDo promoDo = promoDoMapper.selectByPrimaryKey(promoId);
        if (promoDo == null) return null;

        Promo promo = promoConverter.promoDoToPromo(promoDo);
        if (promo.getStartDate().isAfterNow() || promo.getEndDate().isBeforeNow()) return null;

        Item item = itemService.getItemByIdInCache(itemId);
        if (item == null) return null;

        User user = userService.getUserByIdInCache(userId);
        if (user == null) return null;

        // decrease access limit
        // if limit deducted to less than 0, then no token will be assigned to the incoming user, making the user unable to purchase
        long result = redisTemplate.opsForValue().increment("promo_access_limit_" + promoId, -1);
        if (result < 0) return null;

        String token = UUID.randomUUID().toString().replace("-","");

        redisTemplate.opsForValue().set("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, token);
        redisTemplate.expire("promo_token_" + promoId, 5, TimeUnit.MINUTES);

        return token;
    }
}
