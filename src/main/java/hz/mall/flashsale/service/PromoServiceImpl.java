package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.PromoConverter;
import hz.mall.flashsale.domain.Promo;
import hz.mall.flashsale.domain.PromoDo;
import hz.mall.flashsale.mapper.PromoDoMapper;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PromoServiceImpl implements PromoService {

    private final PromoConverter promoConverter;
    private final PromoDoMapper promoDoMapper;


    @Override
    public Promo getPromoByItemId(Integer itemId) {

        // get promo information related to a certain item
        PromoDo promoDo = promoDoMapper.selectByItemId(itemId);

        if (promoDo == null) return null;

        Promo promo = promoConverter.promoDoToPromo(promoDo);

        // determine whether the promo is outdated or about to begin or ongoing

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
