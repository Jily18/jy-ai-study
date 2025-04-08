package com.jy.study.web.controller.common;

import com.jy.study.common.ai.TongYiVoice;
import com.jy.study.common.core.controller.BaseController;
import com.jy.study.common.core.domain.AjaxResult;
import com.jy.study.common.ossfile.OssClientUtil;
import com.jy.study.lesson.domain.StudyArticle;
import com.jy.study.lesson.service.IStudyArticleService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

@Controller
@RequestMapping("/tts")
public class TTSController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(TTSController.class);
    
    private static final int MAX_TEXT_LENGTH = 10000;

    @Autowired
    private TongYiVoice tongYiVoice;

    @Autowired
    private IStudyArticleService articleService;

    @PostMapping("/generate/voice")
    @ResponseBody
    public AjaxResult generateVoice(Long articleId) {
        try {
            // 1. 查询文章
            StudyArticle article = articleService.selectArticleById(articleId);
            if (article == null) {
                return AjaxResult.error("文章不存在");
            }

            // 2. 检查是否已有语音URL
            if (StringUtils.isNotEmpty(article.getVoiceUrl())) {
                return AjaxResult.success("语音已存在", article.getVoiceUrl());
            }

            // 3. 准备文本内容
            String text = article.getTitle() + "。" + article.getContent();
            // 截取文本以符合API限制
            if (text.length() > MAX_TEXT_LENGTH) {
                text = text.substring(0, MAX_TEXT_LENGTH);
            }

            // 4. 生成语音
            ByteBuffer audioBuffer = tongYiVoice.generateVoice(text);
            if (audioBuffer == null) {
                return AjaxResult.error("语音生成失败");
            }

            // 5. 上传到OSS
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".mp3";
            String objectKey = "voice/" + fileName;

            OSS ossClient = OssClientUtil.getOSSClient();
            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        OssClientUtil.getBucketName(),
                        objectKey,
                        new ByteArrayInputStream(audioBuffer.array())
                );
                ossClient.putObject(putObjectRequest);

                // 6. 生成永久访问URL
                String voiceUrl = "https://" + OssClientUtil.getBucketName() + "." + OssClientUtil.getEndpoint() + "/" + objectKey;

                // 7. 更新文章的voice_url
                article.setVoiceUrl(voiceUrl);
                articleService.updateArticle(article);

                return AjaxResult.success("生成成功", voiceUrl);
            } catch (Exception e) {
                log.error("上传语音文件到OSS失败", e);
                return AjaxResult.error("上传语音文件失败");
            }
        } catch (Exception e) {
            log.error("生成语音失败", e);
            return AjaxResult.error("生成语音失败：" + e.getMessage());
        }
    }
}
