package com.jy.study.framework.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class WebLoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        // 获取当前请求路径
        String requestURI = request.getRequestURI();
        
        // 白名单路径直接放行
        if (isAllowPath(requestURI)) {
            return true;
        }
        
        // 只拦截需要登录的操作接口
        if (isRequireLoginPath(requestURI)) {
            // 检查用户是否登录
            HttpSession session = request.getSession();
            if (session.getAttribute("webUser") == null) {
                // 如果是AJAX请求，返回JSON
                if (isAjaxRequest(request)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
                } else {
                    // 普通请求则跳转到登录页
                    response.sendRedirect(request.getContextPath() + "/web/login");
                }
                return false;
            }
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
            "/web/interaction/collect",
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