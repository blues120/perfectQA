package cn.stylefeng.guns.modular.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.agent.entity.Agent;
import cn.stylefeng.guns.modular.agent.mapper.AgentMapper;
import cn.stylefeng.guns.modular.agent.service.AgentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能代理服务实现类
 */
@Service
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<Agent> page(long current, long size) {
        Page<Agent> page = new Page<>(current, size);
        LambdaQueryWrapper<Agent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Agent::getSortOrder)
                  .orderByDesc(Agent::getCreateTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Agent> searchAgents(
            long current,
            long size,
            String keyword,
            String type,
            String status,
            Boolean isEnabled,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Page<Agent> page = new Page<>(current, size);
        return this.baseMapper.searchAgents(page, keyword, type, status, isEnabled, startTime, endTime);
    }

    @Override
    public List<Agent> findEnabledAgents() {
        return this.baseMapper.findEnabledAgents();
    }

    @Override
    public List<Agent> findByType(String type) {
        return this.baseMapper.findByType(type);
    }

    @Override
    public List<Agent> findByCreateUser(String createUser) {
        return this.baseMapper.findByCreateUser(createUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Agent createAgent(Agent agent) {
        // 设置默认值
        if (agent.getIsEnabled() == null) {
            agent.setIsEnabled(true);
        }

        if (agent.getSortOrder() == null) {
            agent.setSortOrder(0);
        }

        if (agent.getStatus() == null) {
            agent.setStatus("idle");
        }

        agent.setCreateTime(LocalDateTime.now());
        agent.setUpdateTime(LocalDateTime.now());

        // 验证配置JSON
        validateConfig(agent.getConfig());

        this.save(agent);
        return agent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Agent updateAgent(Long id, Agent updatedAgent) {
        Agent agent = this.getById(id);
        if (agent == null) {
            throw new RuntimeException("代理不存在: " + id);
        }

        // 更新字段
        if (updatedAgent.getName() != null) {
            agent.setName(updatedAgent.getName());
        }

        if (updatedAgent.getDescription() != null) {
            agent.setDescription(updatedAgent.getDescription());
        }

        if (updatedAgent.getConfig() != null) {
            // 验证配置JSON
            validateConfig(updatedAgent.getConfig());
            agent.setConfig(updatedAgent.getConfig());
        }

        if (updatedAgent.getType() != null) {
            agent.setType(updatedAgent.getType());
        }

        if (updatedAgent.getStatus() != null) {
            agent.setStatus(updatedAgent.getStatus());
        }

        if (updatedAgent.getIsEnabled() != null) {
            agent.setIsEnabled(updatedAgent.getIsEnabled());
        }

        if (updatedAgent.getSortOrder() != null) {
            agent.setSortOrder(updatedAgent.getSortOrder());
        }

        agent.setUpdateTime(LocalDateTime.now());

        this.updateById(agent);
        return agent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Agent updateStatus(Long id, String status) {
        Agent agent = this.getById(id);
        if (agent == null) {
            throw new RuntimeException("代理不存在: " + id);
        }

        agent.setStatus(status);
        agent.setUpdateTime(LocalDateTime.now());

        this.updateById(agent);
        return agent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Agent toggleEnabled(Long id, Boolean isEnabled) {
        Agent agent = this.getById(id);
        if (agent == null) {
            throw new RuntimeException("代理不存在: " + id);
        }

        agent.setIsEnabled(isEnabled);
        agent.setUpdateTime(LocalDateTime.now());

        this.updateById(agent);
        return agent;
    }

    @Override
    public Map<String, Object> executeAgent(Long id, Map<String, Object> params) {
        Agent agent = this.getById(id);
        if (agent == null) {
            throw new RuntimeException("代理不存在: " + id);
        }

        if (!agent.getIsEnabled()) {
            throw new RuntimeException("代理已禁用");
        }

        // 这里只是简单实现，实际项目中会根据代理类型和配置进行实际的执行
        Map<String, Object> result = new HashMap<>();
        result.put("agentId", id);
        result.put("agentName", agent.getName());
        result.put("params", params);
        result.put("executionTime", LocalDateTime.now());
        result.put("status", "success");

        // 更新代理状态
        updateStatus(id, "executed");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAgent(Long id) {
        return this.removeById(id);
    }

    /**
     * 验证配置是否为有效的JSON
     *
     * @param config 配置字符串
     */
    private void validateConfig(String config) {
        if (config != null) {
            try {
                objectMapper.readTree(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("配置不是有效的JSON格式: " + e.getMessage());
            }
        }
    }
}
