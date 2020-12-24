package com.rainbow.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lihao3
 * @Date 2020/12/23 16:19
 */
@Getter
@AllArgsConstructor
public enum SecurityCacheTypeEnum {
    SPRING_REDIS_TEMPLATE("基于redis认证"),
    SESSION("基于session认证");

    /**
     * 对messageCode 异常信息进行补充说明
     */
    private String detailMessage;
}
