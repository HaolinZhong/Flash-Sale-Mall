package hz.mall.flashsale.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BusinessErrEnum implements CommonError{
    // 10001: parameter error
    PARAMETER_VALIDATION_ERROR(10001, "invalid parameter"),

    // 10002: unknown error
    UNKNOW_ERROR(10002, "unknown error"),

    // 20000: user info related error
    USER_NOT_EXIST(20001, "user not exist"),
    USER_LOGIN_FAIL(20002, "incorrect phone number or password")
    ;

    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
