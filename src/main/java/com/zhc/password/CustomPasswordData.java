package com.zhc.password;

import lombok.Data;
import org.passay.PasswordData;

/**
 * 自定义密码参数
 * @author zhouhengchao
 * @since 2023-07-12 11:58
 */
@Data
public class CustomPasswordData extends PasswordData {

    public String mobile;

    public CustomPasswordData() {}

    public CustomPasswordData(String u, String p, String mobile) {
        super(u, p);
        this.mobile = mobile;
    }

}
