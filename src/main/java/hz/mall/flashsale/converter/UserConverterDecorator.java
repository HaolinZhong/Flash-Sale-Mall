package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.domain.UserDo;
import hz.mall.flashsale.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserConverterDecorator implements UserConverter {

    private UserService userService;
    private UserConverter userConverter;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserDoConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @Override
    public User userDoToUser(UserDo userDo) {
        User user = userConverter.userDoToUser(userDo);
        user.setEncryptPassword(userService.getEncryptPassword(userDo.getId()));
        return user;
    }
}
