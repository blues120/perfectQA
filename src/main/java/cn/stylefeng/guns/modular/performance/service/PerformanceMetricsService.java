package cn.stylefeng.guns.modular.performance.service;

import java.util.List;
import java.util.Map;

/**
 * 性能指标服务接口
 */
public interface PerformanceMetricsService {
    
    /**
     * 获取模型性能指标
     * @return 包含模型性能指标的Map
     */
    Map<String, Object> getModelPerformanceMetrics();
    
    /**
     * 获取系统性能指标
     * @return 包含系统性能指标的Map
     */
    Map<String, Object> getSystemPerformanceMetrics();
    
    /**
     * 获取用户行为指标
     * @return 包含用户行为指标的Map
     */
    Map<String, Object> getUserBehaviorMetrics();
    
    /**
     * 获取API调用指标
     * @return 包含API调用指标的Map
     */
    Map<String, Object> getApiCallMetrics();

    /**
     * 比较性能指标
     * @param metrics1 第一个性能指标
     * @param metrics2 第二个性能指标
     * @return 比较结果
     */
    Map<String, Object> comparePerformance(Map<String, Object> metrics1, Map<String, Object> metrics2);

    /**
     * 获取性能历史记录
     * @param timeRange 时间范围
     * @return 性能历史记录
     */
    List<Map<String, Object>> getPerformanceHistory(String timeRange);

    /**
     * 优化性能
     * @param metrics 当前性能指标
     * @return 优化建议
     */
    Map<String, Object> optimizePerformance(Map<String, Object> metrics);

    /**
     * 获取服务状态
     * @return 服务状态信息
     */
    Map<String, Object> getServiceStatus();
} 