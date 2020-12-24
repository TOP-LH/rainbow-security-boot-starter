package com.rainbow.security.aop.aspect;

import com.rainbow.security.aop.annotation.RainbowCheckPerms;
import com.rainbow.security.aop.annotation.RainbowCheckRoles;
import com.rainbow.security.exception.NoPermsException;
import com.rainbow.security.service.RainbowSecurityService;
import com.rainbow.security.util.RainbowSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 鉴权AOP
 *
 * @author lihao3
 * @Date 2020/12/18 16:29
 */
@Aspect
@Component
@Slf4j
public class RainbowSecurityAspect {

    @Autowired
    private RainbowSecurityService rainbowSecurityService;

    /**
     * 设置登录才可以访问的接口的切入点
     */
    @Pointcut("@annotation(com.rainbow.security.aop.annotation.RainbowCheckLogin)")
    public void rainbowCheckLoginPointcut() {
    }

    /**
     * 设置拥有此权限标识才能访问的接口的切入点
     */
    @Pointcut("@annotation(com.rainbow.security.aop.annotation.RainbowCheckPerms)")
    public void rainbowCheckPermsPointcut() {
    }

    /**
     * 设置拥有此角色才能访问的接口的切入点
     */
    @Pointcut("@annotation(com.rainbow.security.aop.annotation.RainbowCheckRoles)")
    public void rainbowCheckRolesPointcut() {
    }

    /**
     * 判断是否登录，没有则抛出异常
     */
    @Before("rainbowCheckLoginPointcut()")
    public void beforeRainbowCheckLogin() {
        RainbowSecurityUtils.isLogin();
    }

    /**
     * 判断操作人是否拥有该权限，没有则抛出异常
     */
    @Before("rainbowCheckPermsPointcut()")
    public void beforeRainbowCheckPermsPointcut(JoinPoint joinPoint) {
        // 判断是否登录，没有则抛出异常
        String loginID = RainbowSecurityUtils.getLoginID();
        // 如果已登录则根据loginID获取他的权限标识
        List<String> permissionCodeList = rainbowSecurityService.getPermissionCodeList(loginID);
        // 或得注解上的值
        RainbowCheckPerms rainbowCheckPerms = getAnnotationRainbowCheckPerms(joinPoint);
        isRequestInterface(permissionCodeList, rainbowCheckPerms, null);
    }

    /**
     * 判断操作人是否拥有该角色权限，没有则抛出异常
     */
    @Before("rainbowCheckRolesPointcut()")
    public void beforeRainbowCheckRolesPointcut(JoinPoint joinPoint) {
        // 判断是否登录，没有则抛出异常
        String loginID = RainbowSecurityUtils.getLoginID();
        // 如果已登录则根据loginID获取他的权限标识
        List<String> roleCodeList = rainbowSecurityService.getRoleCodeList(loginID);
        // 或得注解上的值
        RainbowCheckRoles rainbowCheckRoles = getAnnotationRainbowCheckRoles(joinPoint);
        isRequestInterface(roleCodeList, null, rainbowCheckRoles);
    }

    /**
     * 是否存在权限标识注解，如果存在就获取
     */
    private RainbowCheckPerms getAnnotationRainbowCheckPerms(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RainbowCheckPerms.class);
        }
        return null;
    }

    /**
     * 是否存在权限标识注解，如果存在就获取
     */
    private RainbowCheckRoles getAnnotationRainbowCheckRoles(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RainbowCheckRoles.class);
        }
        return null;
    }

    /**
     * 鉴权方法
     *
     * @param AuthorityList     操作人所拥有的权限标识集合或角色集合
     * @param rainbowCheckPerms 权限标识注解对象
     * @param rainbowCheckRoles 角色注解对象
     */
    private void isRequestInterface(List<String> AuthorityList,
                                    RainbowCheckPerms rainbowCheckPerms,
                                    RainbowCheckRoles rainbowCheckRoles) {
        boolean isAnd;
        String[] perms;
        if (rainbowCheckPerms != null) {
            // 判断权限标识必须全部具有还是只要具有一个就可以通过
            isAnd = rainbowCheckPerms.isAnd();
            // 拿到注解上的权限标识
            perms = rainbowCheckPerms.value();

        } else {
            // 判断权限标识必须全部具有还是只要具有一个就可以通过
            isAnd = rainbowCheckRoles.isAnd();
            // 拿到注解上的权限标识
            perms = rainbowCheckRoles.value();
        }
        // 如果是全部都需要则循环判断
        if (isAnd) {
            for (String perm : perms) {
                if (AuthorityList.contains(perm) == false) {
                    throw new NoPermsException("暂无访问该接口的权限");
                }
            }
        } else {
            for (String perm : perms) {
                if (AuthorityList.contains(perm) == true) {
                    return;
                }
            }
            if (perms.length > 0) {
                throw new NoPermsException("暂无访问该接口的权限");
            }
        }
    }
}
