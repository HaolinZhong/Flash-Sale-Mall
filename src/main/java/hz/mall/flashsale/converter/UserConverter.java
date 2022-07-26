package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.domain.UserDo;
import hz.mall.flashsale.domain.UserPasswordDo;
import hz.mall.flashsale.web.model.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserConverter {
    @Mapping(source = "userDo.id", target = "id")
    User DoToUser(UserDo userDo, UserPasswordDo userPasswordDo);
    UserDo userToUserDo(User user);

    @Mapping(source = "id", target = "userId")
    UserPasswordDo userToUserPasswordDo(User user);

    UserVo UserDoToVo(UserDo userDo);
}
