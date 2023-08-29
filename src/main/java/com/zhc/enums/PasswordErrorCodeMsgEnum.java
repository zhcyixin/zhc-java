package com.zhc.enums;

import java.util.Optional;

public enum PasswordErrorCodeMsgEnum {

    /**
     * 错误码提示
     */
    ILLEGAL_USERNAME_CONTAINS("ILLEGAL_USERNAME.CONTAINS", "密码中不可包含全部登录名信息，请重设密码"),
    ILLEGAL_MOBILE("ILLEGAL_MOBILE", "密码中不可包含账号手机号信息，请重设密码"),
    INSUFFICIENT_UPPERCASE("ILLEGAL_INSUFFICIENT_UPPERCASE", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符"),
    INSUFFICIENT_DIGIT("ILLEGAL_INSUFFICIENT_DIGIT", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符"),
    INSUFFICIENT_ALPHABETICAL("INSUFFICIENT_ALPHABETICAL", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符"),
    INSUFFICIENT_SPECIAL("INSUFFICIENT_ALPHABETICAL", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符"),
    ILLEGAL_NUMERICAL_SEQUENCE("ILLEGAL_NUMERICAL_SEQUENCE", "密码不可包含四位连续数字，请重设密码"),
    ILLEGAL_MATCH("ILLEGAL_MATCH", "密码不可包含四位相同数字，请重设密码"),
    TOO_SHORT("ILLEGAL_TOO_SHORT", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符"),
    TOO_LONG("ILLEGAL_TOO_LONG", "密码格式不正确，请填写8-20位数字/字母/特殊字符(如:!@#$)组合，且至少包含两种类型字符")
    ;

    private String code;

    private String msg;

    PasswordErrorCodeMsgEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Optional<String> getMsg(String code) {
        String msg = null;
        for (PasswordErrorCodeMsgEnum value : PasswordErrorCodeMsgEnum.values()) {
            if (value.getCode().contains(code)) {
                msg = value.getMsg();
            }
        }
        return Optional.ofNullable(msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
