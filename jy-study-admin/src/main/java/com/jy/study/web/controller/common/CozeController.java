package com.jy.study.web.controller.common;

import com.jy.study.common.ai.CozeWorkFlow;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

//在这个controller里面调用coze的接口
@Controller
@RequestMapping("/coze")
public class CozeController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(CozeController.class);
    
    @Autowired
    private CozeWorkFlow cozeWorkFlow;

    /**
     * 生成试题
     */
    @PostMapping("/generate/questions")
    @ResponseBody
    public AjaxResult generateQuestions(Integer xuanze, Integer tiankong, 
            Integer panduan, Integer jianda, String file) {
        try {
            // 准备参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("xuanze", xuanze);
            parameters.put("tiankong", tiankong);
            parameters.put("panduan", panduan);
            parameters.put("jianda", jianda);
            parameters.put("File", file);
            
            // 调用工作流
            String result = cozeWorkFlow.runWorkflow(parameters);
            
            if (result != null) {
                return AjaxResult.success("生成成功", result);
            } else {
                return AjaxResult.error("生成失败");
            }
        } catch (Exception e) {
            log.error("生成试题异常", e);
            return AjaxResult.error("生成失败：" + e.getMessage());
        }
    }
}
