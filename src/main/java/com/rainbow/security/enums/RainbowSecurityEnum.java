package com.rainbow.security.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lihao3
 * @Date 2020/12/23 16:19
 */
@Getter
@AllArgsConstructor
public enum RainbowSecurityEnum {

    NO_TOKEN("-1", "缺少token！"),
    TOKEN_TIME_OUT("-2", "token已过期！"),
    MANDATORY_LOGOUT("-3", "该账号已在别处登陆，您被迫下线！"),
    ADMIN_MANDATORY_LOGOUT("-4", "此账号已被管理员强退，若有疑问请联系管理员");

    /**
     * 异常编号
     */
    private String messageCode;

    /**
     * 对messageCode 异常信息进行补充说明
     */
    private String detailMessage;
}
