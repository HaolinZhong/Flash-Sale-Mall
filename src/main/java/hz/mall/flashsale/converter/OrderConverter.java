package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.Order;
import hz.mall.flashsale.domain.OrderDo;
import org.mapstruct.Mapper;

@Mapper
public interface OrderConverter {
    OrderDo orderToDo(Order order);
}
