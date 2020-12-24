package com.rainbow.security.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口，代表此接口必须登录之后才能访问
 *
 * @author lihao3
 * @Date 2020/12/18 16:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RainbowCheckLogin {

}
