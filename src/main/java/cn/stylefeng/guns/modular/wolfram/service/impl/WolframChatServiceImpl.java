package cn.stylefeng.guns.modular.wolfram.service.impl;


import cn.stylefeng.guns.modular.wolfram.entity.WolframChat;
import cn.stylefeng.guns.modular.wolfram.mapper.WolframChatMapper;
import cn.stylefeng.guns.modular.wolfram.service.WolframChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Wolfram Alpha聊天记录Service实现类
 */
@Service
@Transactional
public class WolframChatServiceImpl extends ServiceImpl<WolframChatMapper, WolframChat> implements WolframChatService {

    @Override
    public WolframChat createChat(String query, String response, String userId) {
        WolframChat chat = new WolframChat();
        chat.setQuery(query);
        chat.setResponse(response);
        chat.setCreateTime(LocalDateTime.now());
        chat.setUserId(userId);
        save(chat);
        return chat;
    }

    @Override
    public List<WolframChat> getUserChats(String userId) {
        return baseMapper.findByUserId(userId);
    }
}
