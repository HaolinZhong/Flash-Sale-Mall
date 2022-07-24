package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.domain.UserDo;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(UserConverterDecorator.class)
public interface UserConverter {
    User userDoToUser(UserDo userDo);
}
