package com.rainbow.security.exception;

/**
 * token过期异常
 *
 * @author lihao3
 * @Date 2020/12/24 12:57
 */
public class TokenTimeOutException extends RuntimeException{

    public TokenTimeOutException(String message) {
        super(message);
    }
}
