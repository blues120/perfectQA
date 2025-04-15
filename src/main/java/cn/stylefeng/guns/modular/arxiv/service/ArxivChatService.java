package cn.stylefeng.guns.modular.arxiv.service;


import cn.stylefeng.guns.modular.arxiv.entity.ArxivChat;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Arxiv论文聊天记录Service接口
 */
public interface ArxivChatService extends IService<ArxivChat> {

    /**
     * 创建聊天记录
     *
     * @param paperId 论文ID
     * @param query 用户查询
     * @param response 系统回答
     * @param userId 用户ID
     * @return 聊天记录
     */
    ArxivChat createChat(String paperId, String query, String response, String userId);

    /**
     * 获取用户的聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<ArxivChat> getUserChats(String userId);
}
