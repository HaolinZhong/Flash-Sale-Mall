package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.UserVoConverter;
import hz.mall.flashsale.domain.UserDo;
import hz.mall.flashsale.domain.UserPasswordDo;
import hz.mall.flashsale.mapper.UserDoMapper;
import hz.mall.flashsale.mapper.UserPasswordDoMapper;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDoMapper userDoMapper;

    @Autowired
    private UserPasswordDoMapper userPasswordDoMapper;

    @Autowired
    private UserVoConverter userVoConverter;

    @Override
    public String getEncryptPassword(Integer userId) {
        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userId);
        if (userPasswordDo == null) return null;
        return userPasswordDo.getEncryptPassword();
    }

    @Override
    public UserVo getUserById(Integer userId) {
        UserDo userDo = userDoMapper.selectByPrimaryKey(userId);
        if (userDo == null) {
            return null;
        }
        return userVoConverter.UserDoToVo(userDo);
    }
}
