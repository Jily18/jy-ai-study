package com.jy.study.common.ai;

import com.alibaba.dashscope.audio.asr.transcription.*;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
public class TongYiVoiceToText {
    private static final Logger log = LoggerFactory.getLogger(TongYiVoiceToText.class);
    
    @Value("${modelTongyi.apiKey}")
    private String apiKey;

    /**
     * 将视频URL转换为文字
     * @param videoUrl 视频URL
     * @return 识别的文字内容
     */
    public String videoToText(String videoUrl) {
        try {
            // 创建转写请求参数
            TranscriptionParam param = TranscriptionParam.builder()
                    .apiKey(apiKey)
                    .model("paraformer-v2")
                    .parameter("language_hints", new String[]{"zh", "en"})
                    .fileUrls(Arrays.asList(videoUrl))
                    .build();

            // 提交转写请求
            Transcription transcription = new Transcription();
            TranscriptionResult result = transcription.asyncCall(param);
            
            // 等待任务完成并获取结果
            result = transcription.wait(
                    TranscriptionQueryParam.FromTranscriptionParam(param, result.getTaskId()));

            // 解析转写结果
            StringBuilder textBuilder = new StringBuilder();
            List<TranscriptionTaskResult> taskResultList = result.getResults();
            if (taskResultList != null && !taskResultList.isEmpty()) {
                for (TranscriptionTaskResult taskResult : taskResultList) {
                    String transcriptionUrl = taskResult.getTranscriptionUrl();
                    HttpURLConnection connection =
                            (HttpURLConnection) new URL(transcriptionUrl).openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JsonObject jsonResult = new GsonBuilder().create()
                            .fromJson(reader, JsonObject.class);
                            
                    // 从JSON中提取文本内容
                    if (jsonResult.has("transcripts")) {
                        JsonArray transcripts = jsonResult.getAsJsonArray("transcripts");
                        for (JsonElement transcript : transcripts) {
                            JsonObject transcriptObj = transcript.getAsJsonObject();
                            if (transcriptObj.has("sentences")) {
                                JsonArray sentences = transcriptObj.getAsJsonArray("sentences");
                                for (JsonElement sentence : sentences) {
                                    JsonObject sentenceObj = sentence.getAsJsonObject();
                                    if (sentenceObj.has("text")) {
                                        textBuilder.append(sentenceObj.get("text").getAsString())
                                                .append("\n");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            String textResult = textBuilder.toString().trim();
            if (textResult.isEmpty()) {
                throw new RuntimeException("未能识别出任何文字");
            }
            return textResult;
            
        } catch (Exception e) {
            log.error("视频转文字失败", e);
            throw new RuntimeException("视频转文字失败: " + e.getMessage());
        }
    }
}