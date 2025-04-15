package cn.stylefeng.guns.modular.database.service.impl;


import cn.stylefeng.guns.modular.database.entity.DatabaseChat;
import cn.stylefeng.guns.modular.database.mapper.DatabaseChatMapper;
import cn.stylefeng.guns.modular.database.service.DatabaseChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据库聊天记录Service实现类
 */
@Service
@Transactional
public class DatabaseChatServiceImpl extends ServiceImpl<DatabaseChatMapper, DatabaseChat> implements DatabaseChatService {

    @Override
    public DatabaseChat createChat(String databaseType, String connectionString, String query, String response, String userId) {
        DatabaseChat chat = new DatabaseChat();
        chat.setDatabaseType(databaseType);
        chat.setConnectionString(connectionString);
        chat.setQuery(query);
        chat.setResponse(response);
        chat.setCreateTime(LocalDateTime.now());
        chat.setUserId(userId);
        save(chat);
        return chat;
    }

    @Override
    public List<DatabaseChat> getUserChats(String userId) {
        return baseMapper.findByUserId(userId);
    }

    @Override
    public List<DatabaseChat> getChatsByDatabaseType(String databaseType) {
        return baseMapper.findByDatabaseType(databaseType);
    }
}
