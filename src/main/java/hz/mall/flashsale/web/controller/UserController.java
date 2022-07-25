package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.UserService;
import hz.mall.flashsale.web.model.CommonReturnType;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController extends BaseController {

    private final UserService userService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getUserById(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserVo userVo = userService.getUserById(id);

        if (userVo == null) throw new BusinessException(BusinessErrEnum.USER_NOT_EXIST);

        return CommonReturnType.builder().data(userVo).status("success").build();
    }

    @GetMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "tel") String tel) {
        int randomInt = new Random().nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // todo: use redis for caching user telephone number and otp code
        // for now, use http session
        httpServletRequest.setAttribute(tel, otpCode);

        // todo:send otp code to user telephone for user registration
        // (or may never do it because I have no money XD)
        // for now just log it
        // never do this in real world application for privacy issue!
        log.info("Message sent to tel " + tel + ", otp: " + otpCode);

        return CommonReturnType.builder().build();
    }

}
