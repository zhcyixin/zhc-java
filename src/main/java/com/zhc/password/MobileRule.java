package com.zhc.password;

import org.apache.commons.lang3.StringUtils;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义规则
 * @author zhouhengchao
 * @since  2023-07-12 11:55
 */
public class MobileRule implements Rule {

    /**
     * Error code for matching mobile.
     */
    public static final String ERROR_CODE = "ILLEGAL_MOBILE";

    /**
     * Error code for matching reversed dictionary word.
     */
    public static final String ERROR_CODE_REVERSED = "ILLEGAL_MOBILE_REVERSED";

    @Override
    public RuleResult validate(PasswordData passwordData) {
        CustomPasswordData auacPasswordData = (CustomPasswordData) passwordData;
        RuleResult result = new RuleResult();
        String mobile = auacPasswordData.getMobile();
        if (!StringUtils.isBlank(mobile)) {
            if (passwordData.getPassword().contains(mobile)) {
                result.addError(ERROR_CODE, createRuleResultDetailParameters(mobile));
            }
        }
        return result;
    }


    /**
     * Creates the parameter data for the rule result detail.
     *
     * @return map of parameter name to value
     */
    protected Map<String, Object> createRuleResultDetailParameters(final String mobile) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("mobile", mobile);
        return m;
    }


    @Override
    public String toString() {
        return
                String.format(
                        "%s@%h",
                        getClass().getName(),
                        hashCode());
    }
}
