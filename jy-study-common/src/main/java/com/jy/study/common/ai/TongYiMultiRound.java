package com.jy.study.common.ai;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TongYiMultiRound {
    @Value("${modelTongyi.apiKey}")
    private static String apiKey;

    /**
     * 创建生成参数对象
     *
     * @param messages 对话消息列表，包含与用户和AI助手的对话历史
     * @return 返回构建好的GenerationParam对象
     *
     * 此方法用于根据当前对话上下文，创建并返回一个GenerationParam对象，该对象用于指导文本生成过程
     * 使用了默认的模型、消息列表、结果格式和topP参数，这些参数是根据具体的业务需求和模型特性预先配置好的
     */
    public static GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                .model("qwen-turbo") // 设置使用的模型为“qwen-turbo”
                .messages(messages) // 设置对话消息列表
                .resultFormat(GenerationParam.ResultFormat.MESSAGE) // 设置结果格式为MESSAGE
                .topP(0.8) // 设置topP参数为0.8，用于控制生成文本的多样性
                .build(); // 构建并返回GenerationParam对象
    }


    /**
     * 根据提供的参数调用生成模型，并返回生成的结果
     * 此方法封装了生成模型的调用过程，提供了一个统一的入口，方便在不同场景下使用生成模型
     */
    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    // 主函数入口
    public static void main(String[] args) {
        // 设置API密钥
        Constants.apiKey= apiKey;
        try {
            // 初始化消息列表
            List<Message> messages = new ArrayList<>();

            // 向消息列表中添加系统初始化消息
            messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
            // 循环三次，收集用户输入并获取模型回复
            for (int i = 0; i < 3;i++) {
                // 创建Scanner对象用于接收用户输入
                Scanner scanner = new Scanner(System.in);
                // 提示用户输入
                System.out.print("请输入：");
                // 接收用户输入
                String userInput = scanner.nextLine();
                // 检查用户是否希望退出
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                // 将用户输入作为消息添加到消息列表中
                messages.add(createMessage(Role.USER, userInput));
                // 创建生成参数对象
                GenerationParam param = createGenerationParam(messages);
                // 调用模型生成回复
                GenerationResult result = callGenerationWithMessages(param);
                // 输出模型回复
                System.out.println("模型输出："+result.getOutput().getChoices().get(0).getMessage().getContent());
                // 将模型回复作为消息添加到消息列表中
                messages.add(result.getOutput().getChoices().get(0).getMessage());
            }
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 处理可能的异常
            e.printStackTrace();
        }
        // 退出程序
        System.exit(0);
    }


    private static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }
}