package com.jy.study.web.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.kaptcha.Constants;
import com.jy.study.common.constant.UserConstants;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.core.domain.entity.SysUser;
import com.jy.study.common.utils.DateUtils;
import com.jy.study.common.utils.MessageUtils;
import com.jy.study.common.utils.ShiroUtils;
import com.jy.study.common.utils.StringUtils;
import com.jy.study.framework.manager.AsyncManager;
import com.jy.study.framework.manager.factory.AsyncFactory;
import com.jy.study.framework.shiro.service.SysPasswordService;
import com.jy.study.framework.web.service.ConfigService;
import com.jy.study.system.service.ISysUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/web")
public class WebAuthController extends BaseController {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private SysPasswordService passwordService;
    
    @Autowired
    private ConfigService configService;

    @GetMapping("/login")
    public String login(ModelMap mmap) {
        mmap.put("captchaEnabled", configService.getKey("sys.account.captchaEnabled"));
        mmap.put("captchaType", "math");
        return "web/login";
    }

    @GetMapping("/register")
    public String register(ModelMap mmap) {
        mmap.put("captchaEnabled", configService.getKey("sys.account.captchaEnabled"));
        mmap.put("captchaType", "math");
        return "web/register";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, String validateCode, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return AjaxResult.error("用户名或密码不能为空");
            }

            // 验证码校验
            if (configService.getKey("sys.account.captchaEnabled").equals("true")) {
                if (StringUtils.isEmpty(validateCode)) {
                    return AjaxResult.error("验证码不能为空");
                }
                if (!validateCode.equalsIgnoreCase((String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY))) {
                    return AjaxResult.error("验证码错误");
                }
            }
            
            // 用户验证
            SysUser user = userService.selectUserByLoginName(username);
            if (user == null) {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
//                    MessageUtils.message("user.not.exists")));
                return AjaxResult.error("用户不存在");
            }
            
            if (!passwordService.matches(user, password)) {
//                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
//                    MessageUtils.message("user.password.not.match")));
                return AjaxResult.error("密码错误");
            }

            // 记录登录信息
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS,
//                MessageUtils.message("user.login.success")));
            recordLoginInfo(user);
            
            session.setAttribute("webUser", user);
            return AjaxResult.success("登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("系统错误：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public AjaxResult ajaxRegister(SysUser user, String validateCode, HttpServletRequest request) {
        HttpSession session = request.getSession();
        boolean captchaEnabled = "true".equals(configService.getKey("sys.account.captchaEnabled"));
        
        if (captchaEnabled) {
            String verifyCode = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (StringUtils.isEmpty(validateCode) || !validateCode.equalsIgnoreCase(verifyCode)) {
                return AjaxResult.error("验证码错误");
            }
        }

        if (StringUtils.isEmpty(user.getLoginName())) {
            return AjaxResult.error("用户名不能为空");
        }
        
        if (StringUtils.isEmpty(user.getPassword())) {
            return AjaxResult.error("密码不能为空");
        }
        
        if (user.getPassword().length() < UserConstants.PASSWORD_MIN_LENGTH 
                || user.getPassword().length() > UserConstants.PASSWORD_MAX_LENGTH) {
            return AjaxResult.error("密码长度必须在5到20个字符之间");
        }
        
        if (user.getLoginName().length() < UserConstants.USERNAME_MIN_LENGTH 
                || user.getLoginName().length() > UserConstants.USERNAME_MAX_LENGTH) {
            return AjaxResult.error("账户长度必须在2到20个字符之间");
        }
        
        if (!userService.checkLoginNameUnique(user)) {
            return AjaxResult.error("保存用户'" + user.getLoginName() + "'失败，注册账号已存在");
        }
        
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return AjaxResult.error("保存用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }

        // 设置前台用户角色和部门
        user.setRoleIds(new Long[]{3L}); // 前台用户角色
        user.setDeptId(300L); // 前台用户部门
        user.setStatus("0"); // 正常状态
        user.setCreateBy("web_register"); // 标识来源
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setUserName(user.getLoginName());
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        
        boolean regFlag = userService.insertUser(user) > 0;
        if (!regFlag) {
            return AjaxResult.error("注册失败,请联系系统管理人员");
        }
        
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getLoginName(), com.jy.study.common.constant.Constants.REGISTER, 
            MessageUtils.message("user.register.success")));
        
        return AjaxResult.success();
    }

    /**
     * 记录登录信息
     */
    private void recordLoginInfo(SysUser user) {
        user.setLoginIp(ShiroUtils.getIp());
        user.setLoginDate(DateUtils.getNowDate());
        userService.updateUserInfo(user);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SysUser user = (SysUser)session.getAttribute("webUser");
        if (user != null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getLoginName(), com.jy.study.common.constant.Constants.LOGOUT, 
                MessageUtils.message("user.logout.success")));
        }
        session.removeAttribute("webUser");
        return "redirect:/web";
    }
} 