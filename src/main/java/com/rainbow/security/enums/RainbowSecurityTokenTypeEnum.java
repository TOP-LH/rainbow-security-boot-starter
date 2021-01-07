package com.rainbow.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rainbow-security 支持的token模式
 * token越短存储越快
 *
 * @author lihao3
 * @Date 2020/12/29 10:39
 */
@Getter
@AllArgsConstructor
public enum RainbowSecurityTokenTypeEnum {

    UUID("传统的UUID（36位+5位随机数）"),
    SIMPLE_UUID("不带中横线的UUID（32位+5位随机数）"),
    JWT("JWT模式（）");


    /**
     * 对jwt模式的说明
     */
    private String detailMessage;
}
