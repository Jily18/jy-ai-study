package com.jy.study.web.controller.web;

import com.jy.study.web.controller.common.CommonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/web")
public class WebAuthController extends BaseController {

    /**
     * 是否开启记住我功能
     */
    @Value("${shiro.rememberMe.enabled: true}")
    private boolean rememberMe;

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
        // 是否开启记住我
        mmap.put("isRemembered", rememberMe);
        return "web/login";
    }

    @GetMapping("/register")
    public String register(ModelMap mmap) {
        mmap.put("captchaEnabled", configService.getKey("sys.account.captchaEnabled"));
        mmap.put("captchaType", "math");
        // 是否开启记住我
        mmap.put("isRemembered", rememberMe);
        return "web/register";
    }

    @PostMapping("/login")
    @ResponseBody
    public AjaxResult ajaxLogin(String username, String password, Boolean rememberMe) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe != null && rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return success();
        } catch (AuthenticationException e) {
            String msg = "用户或密码错误";
            if (StringUtils.isNotEmpty(e.getMessage())) {
                msg = e.getMessage();
            }
            return error(msg);
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public AjaxResult ajaxRegister(SysUser user, String validateCode, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            
            // 首先判断验证码功能是否开启
            boolean captchaEnabled = configService.getKey("sys.account.captchaEnabled").equals("true");
            
            // 验证码校验 - 如果开启了验证码，必须验证通过才能继续
            if (captchaEnabled) {
                if (StringUtils.isEmpty(validateCode)) {
                    return AjaxResult.error("验证码不能为空");
                }
                String verifyCode = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);

                if (StringUtils.isEmpty(verifyCode)) {
                    return AjaxResult.error("验证码已过期，请刷新验证码");
                }
                // 校验完后立即删除session中的验证码
                session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
                if (!validateCode.equalsIgnoreCase(verifyCode)) {
                    return AjaxResult.error("验证码错误");
                }
            }

            // 基本参数校验
            if (StringUtils.isEmpty(user.getLoginName())) {
                return AjaxResult.error("用户名不能为空");
            }
            
            if (StringUtils.isEmpty(user.getPassword())) {
                return AjaxResult.error("密码不能为空");
            }
            
            // 密码长度校验
            if (user.getPassword().length() < UserConstants.PASSWORD_MIN_LENGTH 
                    || user.getPassword().length() > UserConstants.PASSWORD_MAX_LENGTH) {
                return AjaxResult.error("密码长度必须在5到20个字符之间");
            }
            
            // 密码复杂度校验
            if (!user.getPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                return AjaxResult.error("密码必须包含字母和数字，且长度至少为8位");
            }
            
            // 用户名长度校验
            if (user.getLoginName().length() < UserConstants.USERNAME_MIN_LENGTH 
                    || user.getLoginName().length() > UserConstants.USERNAME_MAX_LENGTH) {
                return AjaxResult.error("账户长度必须在2到20个字符之间");
            }
            
            // 用户名唯一性校验
            if (!userService.checkLoginNameUnique(user)) {
                return AjaxResult.error("保存用户'" + user.getLoginName() + "'失败，注册账号已存在");
            }
            
            // 邮箱格式和唯一性校验
            if (StringUtils.isNotEmpty(user.getEmail())) {
                if (!user.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                    return AjaxResult.error("邮箱格式不正确");
                }
                if (!userService.checkEmailUnique(user)) {
                    return AjaxResult.error("保存用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
                }
            }

            // 所有验证通过后，开始注册流程
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
            
            return AjaxResult.success("注册成功");
        } catch (Exception e) {
            logger.error("注册失败", e);
            return AjaxResult.error("注册失败：" + e.getMessage());
        }
    }


    @GetMapping("/checkLogin")
    @ResponseBody
    public AjaxResult checkLogin() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            return success();
        }
        return error("未登录");
    }
} 