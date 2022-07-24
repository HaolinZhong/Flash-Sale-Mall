package hz.mall.flashsale.converter;

import hz.mall.flashsale.domain.UserDo;
import hz.mall.flashsale.web.model.UserVo;
import org.mapstruct.Mapper;

@Mapper
public interface UserVoConverter {
    UserVo UserDoToVo(UserDo userDo);
}
