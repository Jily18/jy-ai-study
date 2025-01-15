package com.jy.study.framework.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.jy.study.common.core.domain.entity.SysUser;
import com.jy.study.system.service.ISysUserService;

public class WebUserRealm extends AuthorizingRealm {
    
    @Autowired
    private ISysUserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 前台用户只需要基本权限
        info.addRole("frontend");
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        
        SysUser user = userService.selectUserByLoginName(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }
        
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }
} 