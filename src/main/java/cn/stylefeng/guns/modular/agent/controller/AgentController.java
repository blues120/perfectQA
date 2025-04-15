package cn.stylefeng.guns.modular.agent.controller;

import cn.stylefeng.guns.modular.agent.entity.Agent;
import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能代理控制器
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * 分页查询代理列表
     */
    @GetMapping("/page")
    public ResponseData<Page<Agent>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return new SuccessResponseData<>(agentService.page(current, size));
    }

    /**
     * 高级搜索代理列表
     */
    @GetMapping("/search")
    public ResponseData<Page<Agent>> search(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return new SuccessResponseData<>(agentService.searchAgents(current, size, keyword, type, status, isEnabled, startTime, endTime));
    }

    /**
     * 获取所有已启用的代理
     */
    @GetMapping("/enabled")
    public ResponseData<List<Agent>> findEnabledAgents() {
        return new SuccessResponseData<>(agentService.findEnabledAgents());
    }

    /**
     * 根据类型查询代理
     */
    @GetMapping("/type/{type}")
    public ResponseData<List<Agent>> findByType(@PathVariable String type) {
        return new SuccessResponseData<>(agentService.findByType(type));
    }

    /**
     * 根据创建用户查询代理
     */
    @GetMapping("/user/{createUser}")
    public ResponseData<List<Agent>> findByCreateUser(@PathVariable String createUser) {
        return new SuccessResponseData<>(agentService.findByCreateUser(createUser));
    }

    /**
     * 根据ID获取代理详情
     */
    @GetMapping("/{id}")
    public ResponseData<Agent> getById(@PathVariable Long id) {
        Agent agent = agentService.getById(id);
        if (agent == null) {
            return new ErrorResponseData<>("408","代理不存在");
        }
        return new SuccessResponseData<>(agent);
    }

    /**
     * 创建代理
     */
    @PostMapping
    public ResponseData<Agent> create(@RequestBody Agent agent) {
        return new SuccessResponseData<>(agentService.createAgent(agent));
    }

    /**
     * 更新代理
     */
    @PutMapping("/{id}")
    public ResponseData<Agent> update(@PathVariable Long id, @RequestBody Agent agent) {
        return new SuccessResponseData<>(agentService.updateAgent(id, agent));
    }

    /**
     * 更新代理状态
     */
    @PutMapping("/{id}/status")
    public ResponseData<Agent> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return new SuccessResponseData<>(agentService.updateStatus(id, status));
    }

    /**
     * 启用或禁用代理
     */
    @PutMapping("/{id}/enabled")
    public ResponseData<Agent> toggleEnabled(@PathVariable Long id, @RequestParam Boolean isEnabled) {
        return new SuccessResponseData<>(agentService.toggleEnabled(id, isEnabled));
    }

    /**
     * 执行代理
     */
    @PostMapping("/{id}/execute")
    public ResponseData<Map<String, Object>> executeAgent(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return new SuccessResponseData<>(agentService.executeAgent(id, params));
    }

    /**
     * 删除代理
     */
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> delete(@PathVariable Long id) {
        boolean result = agentService.deleteAgent(id);
        return result ? new SuccessResponseData<>(true) : new ErrorResponseData<>("408","删除失败");
    }
}
