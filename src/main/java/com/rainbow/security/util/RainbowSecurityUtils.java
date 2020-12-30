package com.rainbow.security.util;

import com.rainbow.security.constant.SecurityConstants;
import com.rainbow.security.enums.RainbowSecurityEnum;
import com.rainbow.security.exception.AdminMandatoryLogout;
import com.rainbow.security.exception.MandatoryLogout;
import com.rainbow.security.exception.NoTokenException;
import com.rainbow.security.exception.TokenTimeOutException;
import com.rainbow.security.propertie.RainbowSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 权限控制工具类
 *
 * @author lihao3
 * @Date 2020/12/23 10:41
 */
@Slf4j
@Component
public class RainbowSecurityUtils {

    private static RedisTemplate redisTemplate;
    private static RainbowSecurityProperties rainbowSecurityConfig;

    @Autowired
    public void setRedisCacheTemplate(RedisTemplate redisTemplate) {
        RainbowSecurityUtils.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setRainbowSecurityConfig(RainbowSecurityProperties rainbowSecurityConfig) {
        RainbowSecurityUtils.rainbowSecurityConfig = rainbowSecurityConfig;
    }

    /**
     * 执行登陆方法
     *
     * @param loginID 登录人的唯一标示
     * @return token
     */
    public static String login(String loginID) {
        // 生成一个token
        String token = generateToken();
        // 根据loginID判断该用户是否已经登陆
        String oldToken = byLoginIDGetToken(loginID);
        // 如果token不为空则代表已登录
        if (!StringUtils.isEmpty(oldToken)) {
            // 判断yml配置的是否需要将前者挤线下
            Boolean isShare = rainbowSecurityConfig.getIsShare();
            // 如果需要则将旧token状态修改为失效
            if (!isShare) {
                // 将旧token状态修改为失效
                Long oldTokenExpireTime = redisTemplate.getExpire(getRedisSecurityPrefix() + token, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(getRedisSecurityPrefix() + token, RainbowSecurityEnum.MANDATORY_LOGOUT.getMessageCode(),
                        oldTokenExpireTime, TimeUnit.SECONDS);
                // 将loginID对应的token修改为新的token
                Long loginIDExpireTime = redisTemplate.getExpire(getRedisSecurityPrefix() + loginID, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(getRedisSecurityPrefix() + loginID, token, loginIDExpireTime, TimeUnit.SECONDS);
                // 判断是否需要根据cookie中的token登录
                if (rainbowSecurityConfig.getIsReadCookie()) {
                    // 获取当前会话的上下文
                    HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
                    HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
                    // 将cookie中的token修改为最新版
                    CookieUtils.updateCookie(request, response, rainbowSecurityConfig.getTokenName(), token);
                }
            } else {
                // 如果不需要则直接返回旧token
                log.info("loginID:{}已登录，token为{}", loginID, token);
                return oldToken;
            }
        } else {
            // 如果未登录则将数据缓存起来
            redisTemplate.opsForValue().set(getRedisSecurityPrefix() + token, loginID,
                    rainbowSecurityConfig.getTimeout(), TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(getRedisSecurityPrefix() + loginID, token, rainbowSecurityConfig.getTimeout(), TimeUnit.SECONDS);
            // 判断是否需要根据cookie判断是否登录
            // 如果需要则将token放到cookie中
            if (rainbowSecurityConfig.getIsReadCookie()) {
                // 获取当前会话的 response
                HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
                // 将token放到cookie中
                CookieUtils.addCookie(response, rainbowSecurityConfig.getTokenName(),
                        token, "/", (int) rainbowSecurityConfig.getTimeout());
            }
            log.info("loginID:{}进行登录，token为{}", loginID, token);
        }
        return token;
    }


    /**
     * 用户注销
     */
    public static void logout() {
        // 获取loginID
        String loginID = getLoginID();
        // 根据loginID获取token
        String token = byLoginIDGetToken(loginID);
        // 删除redis缓存
        redisTemplate.delete(getRedisSecurityPrefix() + loginID);
        redisTemplate.delete(getRedisSecurityPrefix() + token);
        // 删除cookie缓存
        delCookieToken();
    }

    

    /**
     * 判断当前回话是否登录
     *
     * @return
     */
    public static Boolean isLogin() {
        return getLoginID() != null;
    }

    /**
     * 通过loginID获取对应的token
     *
     * @param loginID
     * @return
     */
    public static String byLoginIDGetToken(String loginID) {
        Object token = redisTemplate.opsForValue().get(getRedisSecurityPrefix() + loginID);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return token.toString();
    }

    /**
     * 通过token获取对应的loginID
     *
     * @param token
     * @return
     */
    public static String byTokenGetLoginID(String token) {
        Object loginID = redisTemplate.opsForValue().get(getRedisSecurityPrefix() + token);
        if (StringUtils.isEmpty(loginID)) {
            return null;
        }
        if (loginID.toString() == SecurityConstants.OF_LOGOUT || loginID.toString() == SecurityConstants.MANDATORY_LOGOUT_OUT) {
            return null;
        }
        return loginID.toString();
    }


    /**
     * 获取前端传过来的token
     *
     * @return token值
     */
    public static String getRequestToken() {
        // 获取http上下文对象
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 获取token名称
        String tokenName = rainbowSecurityConfig.getTokenName();

        // 1.尝试从header中获取token
        if (rainbowSecurityConfig.getIsReadHead() == true) {
            String tokenValue = request.getHeader(tokenName);
            if (StringUtils.isEmpty(tokenValue)) {
                return null;
            }
            return tokenValue;
        }
        // 2.尝试从cookie中获取token
        if (rainbowSecurityConfig.getIsReadCookie() == true) {
            Cookie cookie = CookieUtils.getCookie(request, tokenName);
            if (cookie != null) {
                String tokenValue = cookie.getValue();
                if (StringUtils.isEmpty(tokenValue)) {
                    return null;
                }
                return tokenValue;
            }
            return null;
        }
        // 3.常使用请求体中获取token
        if (rainbowSecurityConfig.getIsReadBody() == true) {
            Object requestToken = request.getAttribute(tokenName);
            if (StringUtils.isEmpty(requestToken)) {
                return null;
            }
            return requestToken.toString();
        }
        return null;
    }

    /**
     * 根据前端传递的token获取他对于的loginID
     *
     * @return loginID
     */
    public static String getLoginID() {
        // 获取token
        String token = getRequestToken();
        // 如果token为空，则抛出无token的错误
        if (StringUtils.isEmpty(token)) {
            throw new NoTokenException("token为空！");
        } else {
            // 根据token获取loginID，若无则抛出异常
            String loginID = byTokenGetLoginID(token);
            // 删除cookie中的token
            delCookieToken();
            if (StringUtils.isEmpty(loginID)) {
                throw new TokenTimeOutException(RainbowSecurityEnum.TOKEN_TIME_OUT.getDetailMessage());
            }
            // 根据状态码判断是否被挤下线
            if (SecurityConstants.OF_LOGOUT.equals(loginID)) {
                throw new MandatoryLogout(RainbowSecurityEnum.MANDATORY_LOGOUT.getDetailMessage());
            }
            // 根据状态码判断是否被管理员强退
            if (SecurityConstants.MANDATORY_LOGOUT_OUT.equals(loginID)) {
                throw new AdminMandatoryLogout(RainbowSecurityEnum.ADMIN_MANDATORY_LOGOUT.getDetailMessage());
            }
            return loginID;
        }
    }

    /**
     * 根据loginID将对应的数据缓存起来
     *
     * @param loginID 用户唯一标识
     * @param key     数据对应的key,如果为空则使用loginID作为key
     * @param load    存储的数据
     */
    public static void setDataByLoginID(String loginID, String key, Object load) {
        // 判断key是否为空
        if (StringUtils.isEmpty(key)) {
            redisTemplate.opsForValue().set(getRedisSecurityPrefix() + loginID + ":data", load,
                    rainbowSecurityConfig.getTimeout(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(getRedisSecurityPrefix() + loginID + ":data:" + key, load,
                    rainbowSecurityConfig.getTimeout(), TimeUnit.SECONDS);
        }
    }

    /**
     * 根据loginID获取他对于的缓存
     *
     * @param loginID 用户唯一标识
     * @param key     数据对应的key,如果为空则使用loginID作为key
     */
    public static Object getDataByLoginID(String loginID, String key) {
        // 判断loginID是否无异常
        String token = byLoginIDGetToken(loginID);
        if (StringUtils.isEmpty(token)) {
            redisTemplate.delete(getRedisSecurityPrefix() + loginID + ":data");
            redisTemplate.delete(getRedisSecurityPrefix() + loginID + ":data:" + key);
            throw new TokenTimeOutException("该loginID登录时效已过期，请重新登录");
        }
        // 判断key是否为空
        if (StringUtils.isEmpty(key)) {
            return redisTemplate.opsForValue().get(getRedisSecurityPrefix() + loginID + ":data");
        } else {
            return redisTemplate.opsForValue().get(getRedisSecurityPrefix() + loginID + ":data:" + key);
        }
    }

    /**
     * 获取redis前缀常量值
     *
     * @return
     */
    private static String getRedisSecurityPrefix() {
        return rainbowSecurityConfig.getTokenName() + ":loginID:";
    }

    /**
     * 生成新的token
     *
     * @return token
     */
    private static String generateToken() {
        String uuid = UUID.randomUUID().toString();
        int salt = (int) (Math.random() * 10000);
        String token = uuid + "-" + salt;
        return token;
    }

    private static void delCookieToken() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
        CookieUtils.delCookie(request, response, rainbowSecurityConfig.getTokenName());
    }
}
