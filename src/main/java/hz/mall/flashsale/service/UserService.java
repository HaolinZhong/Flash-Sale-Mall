package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.web.model.UserVo;

public interface UserService {

    User getUserById(Integer userId);

    User getUserByIdInCache(Integer userId);

    void register(User user) throws BusinessException;

    User validateLogin(String tel, String encryptPassword) throws BusinessException;
}
