package com.zhc.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }


    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取当前环境
     */
    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    public static boolean isDev() {
        return Objects.equals(getActiveProfile(), "dev");
    }

    public static boolean isQa() {
        return Objects.equals(getActiveProfile(), "qa");
    }

    public static boolean isStg() {
        return Objects.equals(getActiveProfile(), "stg");
    }

    public static boolean isPrd() {
        return Objects.equals(getActiveProfile(), "prd");
    }
}
