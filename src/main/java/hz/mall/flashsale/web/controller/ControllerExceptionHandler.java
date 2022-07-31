package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.web.model.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonReturnType AllExceptionHandler(HttpServletRequest request, Exception e) {

        Map<String, Object> responseData = new HashMap<>();

        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            responseData.put("errCode", be.getErrCode());
            responseData.put("errMsg", be.getErrMsg());
        } else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e;
            responseData.put("errCode", BusinessErrEnum.PARAMETER_VALIDATION_ERROR.getErrCode());
            responseData.put("errMsg", cve.getMessage());
        } else if (e instanceof ServletRequestBindingException) {
            responseData.put("errCode", BusinessErrEnum.UNKNOW_ERROR.getErrCode());
            responseData.put("errMsg", "Url binding error");
        } else if (e instanceof NoHandlerFoundException) {
            responseData.put("errCode", BusinessErrEnum.UNKNOW_ERROR.getErrCode());
            responseData.put("errMsg", "Path not found");
        } else {
            responseData.put("errCode", BusinessErrEnum.UNKNOW_ERROR.getErrCode());
            responseData.put("errMsg", BusinessErrEnum.UNKNOW_ERROR.getErrMsg());
        }

        return CommonReturnType.builder().status("fail").data(responseData).build();
    }
}
