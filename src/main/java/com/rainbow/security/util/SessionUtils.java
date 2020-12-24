package com.rainbow.security.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @ClassName : SessionUtils
 * @Author : lihao
 * @Date: 2020/12/24 21:00
 * @Description : session工具类
 */
public class SessionUtils {

    /**
     * 获取session对象
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        return request.getSession(false);
    }

    /**
     * 根据key查询对应的值
     *
     * @param sessionKey
     * @return
     */
    public static Object getSessionVal(String sessionKey) {
        Object attribute = getSession().getAttribute(sessionKey);
        if (StringUtils.isEmpty(attribute)) {
            return "";
        }
        return attribute;
    }

    /**
     * 根据key查询对应的值
     *
     * @param sessionKey
     * @return
     */
    public static void setSessionVal(String sessionKey, Object sessionVal) {
        if (!StringUtils.isEmpty(sessionKey) && !StringUtils.isEmpty(sessionVal)) {
            getSession().setAttribute(sessionKey, sessionVal);
        }
    }

    /**
     * 根据key查询这个session属性是否存在
     *
     * @param sessionKey
     * @return
     */
    public static boolean isExists(String sessionKey) {
        return getSessionVal(sessionKey) != null;
    }

    /**
     * 根据key删除他对于的属性
     *
     * @param SessionKey
     */
    public static void delSessionKey(String SessionKey) {
        if (!StringUtils.isEmpty(SessionKey)) {
            getSession().removeAttribute(SessionKey);
        }
    }

}
