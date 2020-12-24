package com.rainbow.security.constant;

/**
 * rainbow-security常用常量
 *
 * @author lihao3
 * @Date 2020/12/23 15:53
 */
public interface SecurityCodeConstants {

    /**
     * 没token返回的状态码
     */
    String NO_TOKEN_CODE = "-1";

    /**
     * token超时返回的状态码
     */
    String TOKEN_TIME_OUT_CODE = "-2";

    /**
     * 被挤下线返回的状态码
     */
    String OF_LOGOUT = "-3";

    /**
     * 被管理强退返回的状态码
     */
    String MANDATORY_LOGOUT_OUT = "-4";
}
