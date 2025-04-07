package com.jy.study.web.controller.common;

import com.jy.study.common.ai.TongYiPicture;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ttl")
public class TTLController extends BaseController {
    //文生图大模型

    @Autowired
    private TongYiPicture tongYiPicture;
    
    @PostMapping("/generate/image")
    @ResponseBody
    public AjaxResult generateImage(String title) {
        try {
            String imageUrl = tongYiPicture.generaPic(title);
            return AjaxResult.success("生成成功", imageUrl);
        } catch (Exception e) {
            return AjaxResult.error("生成失败：" + e.getMessage());
        }
    }
}
