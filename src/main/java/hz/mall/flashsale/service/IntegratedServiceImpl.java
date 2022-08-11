package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.PromoConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.Promo;
import hz.mall.flashsale.domain.PromoDo;
import hz.mall.flashsale.domain.User;
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
    public String generateFlashSaleToken(Integer promoId, Integer itemId, Integer userId) {

        PromoDo promoDo = promoDoMapper.selectByPrimaryKey(promoId);
        if (promoDo == null) return null;

        Promo promo = promoConverter.promoDoToPromo(promoDo);
        if (promo.getStartDate().isAfterNow() || promo.getEndDate().isBeforeNow()) return null;

        Item item = itemService.getItemByIdInCache(itemId);
        if (item == null) return null;

        User user = userService.getUserByIdInCache(userId);
        if (user == null) return null;

        String token = UUID.randomUUID().toString().replace("-","");

        redisTemplate.opsForValue().set("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, token);
        redisTemplate.expire("promo_token_" + promoId, 5, TimeUnit.MINUTES);

        return token;
    }
}
