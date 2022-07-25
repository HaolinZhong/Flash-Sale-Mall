package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.UserService;
import hz.mall.flashsale.web.model.CommonReturnType;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService userService;

    @GetMapping("/get")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserVo userVo = userService.getUserById(id);

        if (userVo == null) throw new BusinessException(BusinessErrEnum.USER_NOT_EXIST);

        return CommonReturnType.builder().data(userVo).status("success").build();
    }

}
