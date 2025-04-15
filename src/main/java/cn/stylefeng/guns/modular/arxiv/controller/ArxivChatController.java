package cn.stylefeng.guns.modular.arxiv.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.arxiv.entity.ArxivChat;
import cn.stylefeng.guns.modular.arxiv.service.ArxivChatService;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Arxiv论文聊天记录Controller
 */
@RestController
@RequestMapping("/api/arxiv")
public class ArxivChatController {

    private final ArxivChatService arxivChatService;

    @Autowired
    public ArxivChatController(ArxivChatService arxivChatService) {
        this.arxivChatService = arxivChatService;
    }

    /**
     * 创建聊天记录
     */
    @PostMapping("/chat")
    public ResponseData<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String paperId = request.get("paperId");
        String query = request.get("query");
        String userId = request.get("userId");

        // TODO: 获取论文内容并生成回答
        String response = "论文分析结果";

        arxivChatService.createChat(paperId, query, response, userId);
        Map<String, String> map = new HashMap<>();
        map.put("response", response);
        return new SuccessResponseData<>(map);
    }

    /**
     * 获取用户聊天历史
     */
    @GetMapping("/history")
    public ResponseData<List<ArxivChat>> getHistory(@RequestParam String userId) {
        List<ArxivChat> chats = arxivChatService.getUserChats(userId);
        return new SuccessResponseData<>(chats);
    }

    /**
     * 删除聊天记录
     */
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteChat(@PathVariable Long id) {
        arxivChatService.removeById(id);
        return new SuccessResponseData<>();
    }

    /**
     * 获取聊天记录详情
     */
    @GetMapping("/{id}")
    public ResponseData<ArxivChat> getChatDetail(@PathVariable Long id) {
        return new SuccessResponseData<>(arxivChatService.getById(id));
    }
}
