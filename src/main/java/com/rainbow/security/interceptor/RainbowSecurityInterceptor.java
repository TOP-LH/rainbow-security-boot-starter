package com.rainbow.security.interceptor;

import com.rainbow.security.annotation.RainbowCheckPerms;
import com.rainbow.security.annotation.RainbowCheckRoles;
import com.rainbow.security.exception.NoPermsException;
import com.rainbow.security.service.RainbowSecurityService;
import com.rainbow.security.util.RainbowSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 拦截器（框架核心部分）
 *
 * @author lihao3
 * @Date 2020/12/31 9:30
 */
@Slf4j
@Component
public class RainbowSecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private RainbowSecurityService rainbowSecurityService;

    /**
     * 请求前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 如果不是方法直接放行
        if (handler instanceof HandlerMethod == false) {
            return true;
        }
        // 开始校验登陆
        RainbowSecurityUtils.isLogin();
        // 不是的话拿到method
        HandlerMethod method = (HandlerMethod) handler;
        // 判断接口是否需要权限标识
        RainbowCheckPerms rainbowCheckPerms = method.getMethod().getDeclaringClass().getAnnotation(RainbowCheckPerms.class);
        // 判断接口是否需要角色标识
        RainbowCheckRoles rainbowCheckRoles = method.getMethod().getDeclaringClass().getAnnotation(RainbowCheckRoles.class);
        if (rainbowCheckPerms != null) {
            List<String> permissionCodeList = rainbowSecurityService.getPermissionCodeList(RainbowSecurityUtils.getLoginID());
            isRequestInterface(permissionCodeList, rainbowCheckPerms, null);
        }
        if (rainbowCheckRoles != null) {
            List<String> roleCodeList = rainbowSecurityService.getRoleCodeList(RainbowSecurityUtils.getLoginID());
            isRequestInterface(roleCodeList, null, rainbowCheckRoles);
        }
        return true;
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
