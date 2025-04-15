package cn.stylefeng.guns.modular.agent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.agent.entity.Agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能代理服务接口
 */
public interface AgentService extends IService<Agent> {

    /**
     * 分页查询代理
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Agent> page(long current, long size);

    /**
     * 高级搜索
     *
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param type 类型
     * @param status 状态
     * @param isEnabled 是否启用
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Agent> searchAgents(
            long current,
            long size,
            String keyword,
            String type,
            String status,
            Boolean isEnabled,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 获取启用的代理
     *
     * @return 代理列表
     */
    List<Agent> findEnabledAgents();

    /**
     * 根据类型查询代理
     *
     * @param type 类型
     * @return 代理列表
     */
    List<Agent> findByType(String type);

    /**
     * 根据创建用户查询代理
     *
     * @param createUser 创建用户
     * @return 代理列表
     */
    List<Agent> findByCreateUser(String createUser);

    /**
     * 创建代理
     *
     * @param agent 代理信息
     * @return 创建后的代理
     */
    Agent createAgent(Agent agent);

    /**
     * 更新代理
     *
     * @param id 代理ID
     * @param agent 更新信息
     * @return 更新后的代理
     */
    Agent updateAgent(Long id, Agent agent);

    /**
     * 更新代理状态
     *
     * @param id 代理ID
     * @param status 状态
     * @return 更新后的代理
     */
    Agent updateStatus(Long id, String status);

    /**
     * 启用或禁用代理
     *
     * @param id 代理ID
     * @param isEnabled 是否启用
     * @return 更新后的代理
     */
    Agent toggleEnabled(Long id, Boolean isEnabled);

    /**
     * 执行代理
     *
     * @param id 代理ID
     * @param params 执行参数
     * @return 执行结果
     */
    Map<String, Object> executeAgent(Long id, Map<String, Object> params);

    /**
     * 删除代理
     *
     * @param id 代理ID
     * @return 是否成功
     */
    boolean deleteAgent(Long id);
}
