package com.zhc.controller.spring.param;

import com.zhc.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

@Component
public class ParamConstraintValidated implements ConstraintValidator<CheckParam, Object> {

    private List<String> annotationValue;

    private String errorMessage;

    @Override
    public void initialize(CheckParam checkParam){
        // 参数校验器上获取注解上的值
        annotationValue = Arrays.asList(checkParam.paramAllowValues());
        errorMessage = checkParam.message();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext){
        if(annotationValue.contains(o)){
            return true;
        }
        // 传入参数不在规定参数列表中
        throw new BusinessException(errorMessage);
    }

}
