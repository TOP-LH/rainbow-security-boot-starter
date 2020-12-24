package com.rainbow.security.service.impl;

import com.rainbow.security.service.RainbowSecurityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现类
 *
 * @author lihao3
 * @Date 2020/12/23 17:49
 */
@Service
public class RainbowSecurityServiceImpl implements RainbowSecurityService {

    @Override
    public List<String> getPermissionCodeList(String loginId) {
        return new ArrayList<String>();
    }

    @Override
    public List<String> getRoleCodeList(String loginId) {
        return new ArrayList<String>();
    }
}
