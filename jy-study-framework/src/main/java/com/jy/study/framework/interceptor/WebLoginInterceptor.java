package com.jy.study.framework.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前台登录拦截器
 */
@Component
public class WebLoginInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebLoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        
        // 如果是白名单路径，直接放行
        if (isAllowPath(path)) {
            return true;
        }
        
        // 如果不是需要登录的路径，直接放行
        if (!isRequireLoginPath(path)) {
            return true;
        }

        // 使用Shiro检查登录状态
        Subject subject = SecurityUtils.getSubject();
        log.debug("WebLoginInterceptor - checking auth status: {}", subject != null ? subject.isAuthenticated() : "null");

        if (subject == null || !subject.isAuthenticated()) {
            log.warn("User not authenticated, redirecting to login page");
            // 如果是AJAX请求，返回JSON
            if (isAjaxRequest(request)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
                return false;
            }
            // 普通请求重定向到登录页
            response.sendRedirect(request.getContextPath() + "/web/login");
            return false;
        }
        return true;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }

    private boolean isRequireLoginPath(String path) {
        // 需要登录的接口路径
        String[] requireLoginPaths = {
                "/web/interaction/like",
                "/web/interaction/unlike",
                "/web/interaction/collect",
                "/web/interaction/uncollect",
                "/web/user/"
                
        };

        for (String loginPath : requireLoginPaths) {
            if (path.startsWith(loginPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowPath(String path) {
        // 定义白名单路径
        String[] allowPaths = {
                "/web/login",
                "/web/register",
                "/web/captcha",
                "/web/index",
                "/web/public",
                "/web/checkLogin",
                "/web/interaction/view",
                "/css/",
                "/js/",
                "/img/",
                "/ajax/",
                "/captcha/"
        };

        for (String allowPath : allowPaths) {
            if (path.startsWith(allowPath)) {
                return true;
            }
        }
        return false;
    }
} 