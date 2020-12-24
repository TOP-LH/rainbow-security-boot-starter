package com.rainbow.security.exception;

/**
 * 被管理员强T下线的异常
 *
 * @author lihao3
 * @Date 2020/12/24 12:56
 */
public class AdminMandatoryLogout extends RuntimeException{

    public AdminMandatoryLogout(String message) {
        super(message);
    }
}
