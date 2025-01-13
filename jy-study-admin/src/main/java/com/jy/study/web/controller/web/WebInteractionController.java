package com.jy.study.web.controller.web;

import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.utils.ShiroUtils;
import com.jy.study.lesson.service.IStudyUserInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.SecurityUtils;

@RestController
@RequestMapping("/web/interaction")
public class WebInteractionController extends BaseController {
    
    @Autowired
    private IStudyUserInteractionService interactionService;
    
    @PostMapping("/like")
    public AjaxResult like(String type, Long targetId) {
        Long userId = getUserId();
        if (userId == null) {
            return error("请先登录");
        }
        boolean result = interactionService.like(userId, type, targetId);
        return result ? success() : error("您已经点过赞了");
    }
    
    @PostMapping("/unlike")
    public AjaxResult unlike(String type, Long targetId) {
        Long userId = getUserId();
        if (userId == null) {
            return error("请先登录");
        }
        boolean result = interactionService.unlike(userId, type, targetId);
        return result ? success() : error("您还没有点赞");
    }
    
    @PostMapping("/collect")
    public AjaxResult collect(String type, Long targetId) {
        Long userId = getUserId();
        if (userId == null) {
            return error("请先登录");
        }
        boolean result = interactionService.collect(userId, type, targetId);
        return result ? success() : error("您已经收藏过了");
    }
    
    @PostMapping("/uncollect")
    public AjaxResult uncollect(String type, Long targetId) {
        Long userId = getUserId();
        if (userId == null) {
            return error("请先登录");
        }
        boolean result = interactionService.uncollect(userId, type, targetId);
        return result ? success() : error("您还没有收藏");
    }
    
    @GetMapping("/status")
    public AjaxResult getStatus(String type, Long targetId) {
        // 获取当前登录用户ID，未登录返回null
        Long userId = null;
        try {
            if (SecurityUtils.getSubject() != null && SecurityUtils.getSubject().getPrincipal() != null) {
                userId = getUserId();
            }
        } catch (Exception e) {
            // 忽略异常，保持userId为null
        }
        
        boolean liked = false;
        boolean collected = false;
        if (userId != null) {
            liked = interactionService.checkLiked(userId, type, targetId);
            collected = interactionService.checkCollected(userId, type, targetId);
        }
        
        return success()
            .put("data", new StatusResult(liked, collected));
    }
    
    @PostMapping("/view")
    public AjaxResult recordView(String type, Long targetId) {
        // 获取当前登录用户ID，未登录返回null
        Long userId = null;
        try {
            if (SecurityUtils.getSubject() != null && SecurityUtils.getSubject().getPrincipal() != null) {
                userId = getUserId();
            }
        } catch (Exception e) {
            // 忽略异常，保持userId为null
        }
        
        String ipAddr = ShiroUtils.getIp();
        interactionService.recordView(userId, type, targetId, ipAddr);
        return success();
    }
    
    // 内部类用于返回状态
    private static class StatusResult {
        private boolean liked;
        private boolean collected;
        
        public StatusResult(boolean liked, boolean collected) {
            this.liked = liked;
            this.collected = collected;
        }
        
        public boolean isLiked() {
            return liked;
        }
        
        public boolean isCollected() {
            return collected;
        }
    }
} 