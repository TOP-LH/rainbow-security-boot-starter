package com.rainbow.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rainbow-security 支持的jwt模式
 *
 * @author lihao3
 * @Date 2020/12/29 10:39
 */
@Getter
@AllArgsConstructor
public enum  RainbowSecurityJwtEnum {

    UUID("传统的UUID（）");


    /**
     * 对messageCode 异常信息进行补充说明
     */
    private String detailMessage;
}
