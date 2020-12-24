package com.rainbow.security.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个接口，代表此接口必须有此权限标识才能请求
 *
 * @author lihao3
 * @Date 2020/12/18 16:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RainbowCheckPerms {

    /**
     * 权限码数组 ，String类型
     * @return .
     */
    String [] value() default {};

    /**
     * 是否属于and型验证 ，true=必须全部具有，false=只要具有一个就可以通过
     * @return .
     */
    boolean isAnd() default true;


}
