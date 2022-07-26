package hz.mall.flashsale.web.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin
public class UserController extends BaseController {

    private final UserService userService;
    private final HttpServletRequest httpServletRequest;
    private Map<String, String> telOtpCodeMap = new HashMap<>();

    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getUserById(@RequestParam(name = "id") Integer id) throws BusinessException {
        UserVo userVo = userService.getUserById(id);

        if (userVo == null) throw new BusinessException(BusinessErrEnum.USER_NOT_EXIST);

        return CommonReturnType.builder().data(userVo).status("success").build();
    }

    @PostMapping(path = "/getotp", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "tel") String tel) {
        int randomInt = new Random().nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // todo: use redis for caching user telephone number and otp code
        // for now, use hash map
        telOtpCodeMap.put(tel, otpCode);

        // todo:send otp code to user telephone for user registration
        // (or may never do it because I have no money XD)
        // for now just log it
        // never do this in real world application for privacy issue!
        log.info("Message sent to tel " + tel + ", otp: " + otpCode);



        return CommonReturnType.builder().status("success").data(null).build();
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType register(
            @NotNull @NotBlank @RequestParam(name = "tel") String tel,
            @NotNull @NotBlank @RequestParam(name = "otpCode") String otpCode,
            @NotNull @NotBlank @RequestParam(name = "name") String name,
            @NotNull @NotBlank @RequestParam(name = "password") String password,
            @NotNull @RequestParam(name = "gender") Byte gender,
            @NotNull @RequestParam(name = "age") Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        String otpCodeStored = telOtpCodeMap.get(tel);

        if (otpCodeStored == null || !otpCodeStored.equals(otpCode)) {
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

    private String encryptByMd5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64en = Base64.getEncoder();
        String encryptPassword = base64en.encodeToString(md5.digest(password.getBytes("utf-8")));
        return encryptPassword;
    }

}
