package com.rainbow.security.service;

import java.util.List;

/**
 * @author lihao3
 * @Date 2020/12/23 14:51
 */
public interface RainbowSecurityService {

    /**
     * 根据login获取他的权限标识集合
     *
     * @param loginId
     * @return
     */
    List<String> getPermissionCodeList(String loginId);

    /**
     * 根据login获取他的角色代码列表
     *
     * @param loginId
     * @return
     */
    List<String> getRoleCodeList(String loginId);
}
