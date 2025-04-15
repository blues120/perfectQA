package cn.stylefeng.guns.modular.wolfram.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.wolfram.entity.WolframChat;
import cn.stylefeng.guns.modular.wolfram.service.WolframChatService;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wolfram Alpha聊天记录Controller
 */
@RestController
@RequestMapping("/api/wolfram")
public class WolframChatController {

    private final WolframChatService wolframChatService;

    @Autowired
    public WolframChatController(WolframChatService wolframChatService) {
        this.wolframChatService = wolframChatService;
    }

    /**
     * 创建聊天记录
     */
    @PostMapping("/chat")
    public ResponseData<Map<Object, Object>> chat(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String userId = request.get("userId");

        // TODO: 调用Wolfram Alpha API获取回答
        String response = "Wolfram Alpha计算结果";

        wolframChatService.createChat(query, response, userId);
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("response", response);
        return new SuccessResponseData<>(objectObjectHashMap);
    }

    /**
     * 获取用户聊天历史
     */
    @GetMapping("/history")
    public ResponseData<List<WolframChat>> getHistory(@RequestParam String userId) {
        List<WolframChat> chats = wolframChatService.getUserChats(userId);
        return new SuccessResponseData<>(chats);
    }

    /**
     * 删除聊天记录
     */
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteChat(@PathVariable Long id) {
        wolframChatService.removeById(id);
        return new SuccessResponseData<>();
    }

    /**
     * 获取聊天记录详情
     */
    @GetMapping("/{id}")
    public ResponseData<WolframChat> getChatDetail(@PathVariable Long id) {
        return new SuccessResponseData<>(wolframChatService.getById(id));
    }
}
