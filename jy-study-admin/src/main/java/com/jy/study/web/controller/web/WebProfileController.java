package com.jy.study.web.controller.web;

import com.jy.study.common.utils.DateUtils;
import com.jy.study.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.core.domain.entity.SysUser;
import com.jy.study.system.service.ISysUserService;
import com.jy.study.framework.shiro.service.SysPasswordService;

@Controller
@RequestMapping("/web/user/profile")
public class WebProfileController extends BaseController {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private SysPasswordService passwordService;

    /**
     * 个人中心页面
     */
    @GetMapping()
    public String profile(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", user);
        return "web/user/profile";
    }

    /**
     * 修改用户信息
     */
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult updateProfile(SysUser user) {
        SysUser currentUser = getSysUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (userService.updateUserInfo(currentUser) > 0) {
            setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePwd")
    @ResponseBody
    public AjaxResult updatePwd(String oldPassword, String newPassword) {
        SysUser user = getSysUser();
        if (!passwordService.matches(user, oldPassword)) {
            return error("修改密码失败，原密码错误");
        }
        if (passwordService.matches(user, newPassword)) {
            return error("新密码不能与原密码相同");
        }
        
        // 更新用户密码信息
        user.setSalt(ShiroUtils.randomSalt());  // 生成新的随机盐值
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), newPassword, user.getSalt()));  // 加密新密码
        user.setPwdUpdateDate(DateUtils.getNowDate());

        if (userService.resetUserPwd(user) > 0) {
            setSysUser(userService.selectUserById(user.getUserId()));
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }
} 