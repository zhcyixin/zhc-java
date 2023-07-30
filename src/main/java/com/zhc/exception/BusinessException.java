package com.zhc.exception;


/**
 * @author zhouhengchao
 * @since  2023-07-29
 **/
public class BusinessException extends BaseException {

    private static final long serialVersionUID = -923082945191620993L;

    public BusinessException() {
        super(ExceptionModule.USER);
    }

    public BusinessException(Integer code) {
        super(ExceptionModule.USER, code);
    }

    public BusinessException(Integer code, String message) {
        super(ExceptionModule.BUSINESS, code, message);
    }

    public BusinessException(String message) {
        super(ExceptionModule.BUSINESS, 200, message);
    }

    public BusinessException(Integer code, String message, Throwable e) {
        super(ExceptionModule.BUSINESS, code, message, e);
    }

    public static class ExceptionModule {

        public static final String USER = "user";

        public static final String BUSINESS = "business";

        public static final String SYSTEM = "system";

        public static final String RECRUITMENT = "recruitment";
    }
}
