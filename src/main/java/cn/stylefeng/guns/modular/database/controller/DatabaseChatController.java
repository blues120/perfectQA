package cn.stylefeng.guns.modular.database.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.database.entity.DatabaseChat;
import cn.stylefeng.guns.modular.database.service.DatabaseChatService;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据库聊天记录控制器
 */
@RestController
@RequestMapping("/api/database-chat")
public class DatabaseChatController {

    @Autowired
    private DatabaseChatService databaseChatService;

    /**
     * 创建聊天记录
     */
    @PostMapping
    public ResponseData<DatabaseChat> createChat(@RequestBody Map<String, String> request) {
        String databaseType = request.get("databaseType");
        String connectionString = request.get("connectionString");
        String query = request.get("query");
        String response = request.get("response");
        String userId = request.get("userId");

        DatabaseChat chat = databaseChatService.createChat(databaseType, connectionString, query, response, userId);
        new SuccessResponseData<>(chat);
        return new SuccessResponseData<>(chat);
    }

    /**
     * 获取用户聊天记录
     */
    @GetMapping("/history/{userId}")
    public ResponseData<List<DatabaseChat>> getUserChats(@PathVariable String userId) {
        return new SuccessResponseData<>(databaseChatService.getUserChats(userId));
    }

    /**
     * 获取特定数据库类型的聊天记录
     */
//    @GetMapping("/type/{databaseType}")
//    public ResponseData<List<DatabaseChat>> getByDatabaseType(@PathVariable String databaseType) {
//        return new SuccessResponseData<>(databaseChatService.getByDatabaseType(databaseType));
//    }

    /**
     * 删除聊天记录
     */
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteChat(@PathVariable Long id) {
        databaseChatService.removeById(id);
        return new SuccessResponseData<>();
    }

    /**
     * 获取聊天记录详情
     */
    @GetMapping("/{id}")
    public ResponseData<DatabaseChat> getChatDetail(@PathVariable Long id) {
        return new SuccessResponseData<>(databaseChatService.getById(id));
    }
}
