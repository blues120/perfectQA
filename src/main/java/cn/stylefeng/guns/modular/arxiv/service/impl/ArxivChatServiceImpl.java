package cn.stylefeng.guns.modular.arxiv.service.impl;


import cn.stylefeng.guns.modular.arxiv.entity.ArxivChat;
import cn.stylefeng.guns.modular.arxiv.mapper.ArxivChatMapper;
import cn.stylefeng.guns.modular.arxiv.service.ArxivChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Arxiv论文聊天记录Service实现类
 */
@Service
@Transactional
public class ArxivChatServiceImpl extends ServiceImpl<ArxivChatMapper, ArxivChat> implements ArxivChatService {

    @Override
    public ArxivChat createChat(String paperId, String query, String response, String userId) {
        ArxivChat chat = new ArxivChat();
        chat.setPaperId(paperId);
        chat.setQuery(query);
        chat.setResponse(response);
        chat.setCreateTime(LocalDateTime.now());
        chat.setUserId(userId);
        save(chat);
        return chat;
    }

    @Override
    public List<ArxivChat> getUserChats(String userId) {
        return baseMapper.findByUserId(userId);
    }
}
