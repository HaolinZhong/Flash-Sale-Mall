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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserDoMapper userDoMapper;
    private final UserPasswordDoMapper userPasswordDoMapper;
    private final UserConverter userConverter;

    @Override
    public UserVo getUserById(Integer userId) {
        UserDo userDo = userDoMapper.selectByPrimaryKey(userId);
        if (userDo == null) {
            return null;
        }
        return userConverter.UserDoToVo(userDo);
    }

    @Transactional
    @Override
    public void register(User user) throws BusinessException {
        if (user == null) throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR);
        if (StringUtils.isEmpty(user.getName())
                || user.getGender() == null
                || user.getAge() == null
                || StringUtils.isEmpty(user.getTel())) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR);
        }

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
}
