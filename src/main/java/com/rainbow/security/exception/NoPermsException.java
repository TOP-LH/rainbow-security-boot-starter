package com.rainbow.security.exception;

/**
 * 访问方法却没有该方法需要的权限标识时返回的异常
 *
 * @author lihao3
 * @Date 2020/12/24 9:50
 */
public class NoPermsException extends RuntimeException {

    public NoPermsException(String message) {
        super(message);
    }
}
