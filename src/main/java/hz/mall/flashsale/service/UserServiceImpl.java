package hz.mall.flashsale.service;

import hz.mall.flashsale.converter.UserConverter;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.domain.UserDo;
import hz.mall.flashsale.domain.UserPasswordDo;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.UserDoMapper;
import hz.mall.flashsale.mapper.UserPasswordDoMapper;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDoMapper userDoMapper;
    private final UserPasswordDoMapper userPasswordDoMapper;
    private final UserConverter userConverter;
    private final RedisTemplate redisTemplate;

    @Override
    public User getUserById(Integer userId) {
        UserDo userDo = userDoMapper.selectByPrimaryKey(userId);
        if (userDo == null) {
            return null;
        }

        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDo.getId());

        return userConverter.DoToUser(userDo, userPasswordDo);
    }

    @Override
    public User getUserByIdInCache(Integer userId) {
        User user = (User) redisTemplate.opsForValue().get("user_validate_" + userId);
        if (user == null) {
            user = getUserById(userId);
            redisTemplate.opsForValue().set("user_validate_" + userId, user);
            redisTemplate.expire("user_validate_" + userId, 10, TimeUnit.MINUTES);
        }
        return user;
    }

    @Transactional
    @Override
    public void register(User user) throws BusinessException {
        if (user == null) throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR);

        UserDo userDo = userConverter.userToUserDo(user);

        try {
            userDoMapper.insertSelective(userDo);
        } catch(DuplicateKeyException e) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "Phone number already registered");
        }


        user.setId(userDo.getId());
        UserPasswordDo userPasswordDo = userConverter.userToUserPasswordDo(user);
        userPasswordDoMapper.insertSelective(userPasswordDo);
    }

    @Override
    public User validateLogin(String tel, String encryptPassword) throws BusinessException {
        UserDo userDo = userDoMapper.selectByTel(tel);
        if (userDo == null) throw new BusinessException(BusinessErrEnum.USER_LOGIN_FAIL);
        UserPasswordDo userPasswordDo = userPasswordDoMapper.selectByUserId(userDo.getId());

        if (!StringUtils.equals(encryptPassword, userPasswordDo.getEncryptPassword())) {
            throw new BusinessException(BusinessErrEnum.USER_LOGIN_FAIL);
        }

        return userConverter.DoToUser(userDo, userPasswordDo);
    }
}
