package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.converter.UserConverter;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.UserService;
import hz.mall.flashsale.web.model.CommonReturnType;
import hz.mall.flashsale.web.model.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(allowCredentials="true", allowedHeaders = "*", originPatterns = "*")
@Validated
public class UserController {

    private final UserConverter userConverter;
    private final UserService userService;
    private final HttpServletRequest httpServletRequest;
    private final RedisTemplate redisTemplate;

    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getUserById(@RequestParam(name = "id") Integer id) throws BusinessException {
        User user = userService.getUserById(id);

        if (user == null) throw new BusinessException(BusinessErrEnum.USER_NOT_EXIST);

        UserVo userVo = userConverter.userToUserVo(user);
        return CommonReturnType.builder().data(userVo).status("success").build();
    }

    @PostMapping(path = "/getotp", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "tel") String tel) {
        int randomInt = new Random().nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // todo: use redis for caching user telephone number and otp code
        // for now, use session
        httpServletRequest.getSession().setAttribute(tel, otpCode);

        // todo:send otp code to user telephone for user registration
        // (or may never do it because I have no money XD)
        // for now just log it
        // never do this in real world application for privacy issue!
        log.info("Message sent to tel " + tel + ", otp: " + otpCode);

        return CommonReturnType.builder().status("success").data(otpCode).build();
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType register(
            @NotBlank(message = "please enter your phone number")
            @RequestParam(name = "tel") String tel,

            @NotBlank(message = "please enter your verification code")
            @RequestParam(name = "otpCode") String otpCode,

            @NotBlank(message = "please enter your name")
            @RequestParam(name = "name") String name,

            @NotBlank(message = "please enter your password")
            @RequestParam(name = "password") String password,

            @NotNull(message = "please enter your gender")
            @Min(value = 0, message = "please use 0 (female) or 1 (male) to suggest your gender")
            @Max(value = 1, message = "please use 0 (female) or 1 (male) to suggest your gender")
            @RequestParam(name = "gender") Byte gender,

            @NotNull(message = "please enter your age")
            @Min(value = 0, message = "age cannot be smaller than 0")
            @Max(value = 150, message = "age cannot be larger than 150")
            @RequestParam(name = "age") Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        String otpCodeInSession = (String) httpServletRequest.getSession().getAttribute(tel);

        if (otpCodeInSession == null || !otpCodeInSession.equals(otpCode)) {
            throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "Invalid Verification Code");
        }

        // user registration
        User user = User.builder()
                .name(name)
                .gender(gender)
                .age(age)
                .tel(tel)
                .registerMode("byPhone")
                .encryptPassword(this.encryptByMd5(password))
                .build();

        userService.register(user);

        return CommonReturnType.builder().status("success").data("Register Success").build();
    }


    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType login(@NotBlank @RequestParam(name = "tel") String tel,
                                  @NotBlank @RequestParam(name = "password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {

        User user = userService.validateLogin(tel, encryptByMd5(password));

        // update: use redis to store login information & token
        //        httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        //        httpServletRequest.getSession().setAttribute("LOGIN_USER", user);

        // generate token (an UUID)
        String token = UUID.randomUUID().toString().replace("-", "");

        // associate token and user login status
        redisTemplate.opsForValue().set(token, user);
        redisTemplate.expire(token, 1, TimeUnit.HOURS);

        return CommonReturnType.builder().status("success").data(token).build();
    }

    private String encryptByMd5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64en = Base64.getEncoder();
        String encryptPassword = base64en.encodeToString(md5.digest(password.getBytes("utf-8")));
        return encryptPassword;
    }

}
