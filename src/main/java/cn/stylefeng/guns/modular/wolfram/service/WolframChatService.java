package cn.stylefeng.guns.modular.wolfram.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.wolfram.entity.WolframChat;

import java.util.List;

/**
 * Wolfram Alpha聊天记录Service接口
 */
public interface WolframChatService extends IService<WolframChat> {

    /**
     * 创建聊天记录
     *
     * @param query 用户查询
     * @param response 系统回答
     * @param userId 用户ID
     * @return 聊天记录
     */
    WolframChat createChat(String query, String response, String userId);

    /**
     * 获取用户的聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<WolframChat> getUserChats(String userId);
}
