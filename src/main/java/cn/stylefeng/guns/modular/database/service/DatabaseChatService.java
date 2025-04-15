package cn.stylefeng.guns.modular.database.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.database.entity.DatabaseChat;

import java.util.List;

/**
 * 数据库聊天记录Service接口
 */
public interface DatabaseChatService extends IService<DatabaseChat> {

    /**
     * 创建聊天记录
     *
     * @param databaseType 数据库类型
     * @param connectionString 连接字符串
     * @param query 查询语句
     * @param response 查询结果
     * @param userId 用户ID
     * @return 聊天记录
     */
    DatabaseChat createChat(String databaseType, String connectionString, String query, String response, String userId);

    /**
     * 获取用户的聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<DatabaseChat> getUserChats(String userId);

    /**
     * 根据数据库类型查询聊天记录
     *
     * @param databaseType 数据库类型
     * @return 聊天记录列表
     */
    List<DatabaseChat> getChatsByDatabaseType(String databaseType);
}
