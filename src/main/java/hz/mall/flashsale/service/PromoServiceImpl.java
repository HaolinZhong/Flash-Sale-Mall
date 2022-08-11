package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.PromoConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.domain.Promo;
import hz.mall.flashsale.domain.PromoDo;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.mapper.PromoDoMapper;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoConverter promoConverter;
    @Autowired
    private PromoDoMapper promoDoMapper;

    @Override
    public Promo getPromoByItemId(Integer itemId) {

        // get promo information related to a certain item
        PromoDo promoDo = promoDoMapper.selectByItemId(itemId);

        if (promoDo == null) return null;

        Promo promo = promoConverter.promoDoToPromo(promoDo);

        // determine whether the promo is outdated or about to begin or ongoing
        promo = setPromoStatus(promo);

        return promo;
    }

    public Promo setPromoStatus(Promo promo) {
        if (promo.getStartDate().isAfterNow()) {
            promo.setStatus(1);
        } else if (promo.getEndDate().isBeforeNow()) {
            promo.setStatus(3);
        } else {
            promo.setStatus(2);
        }

        return promo;
    }
}
