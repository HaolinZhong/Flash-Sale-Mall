package hz.mall.flashsale.service;

import hz.mall.flashsale.web.model.UserVo;

public interface UserService {

    String getEncryptPassword(Integer userId);

    UserVo getUserById(Integer userId);
}
