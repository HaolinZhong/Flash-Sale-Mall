package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.web.model.UserVo;

public interface UserService {

    UserVo getUserById(Integer userId);

    void register(User user) throws BusinessException;

    User validateLogin(String tel, String encryptPassword) throws BusinessException;
}
