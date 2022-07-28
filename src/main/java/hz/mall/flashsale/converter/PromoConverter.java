package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.Promo;
import hz.mall.flashsale.domain.PromoDo;
import org.mapstruct.Mapper;

@Mapper
public interface PromoConverter {
    Promo promoDoToPromo(PromoDo promoDo);
}
