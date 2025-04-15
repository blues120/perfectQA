package cn.stylefeng.guns.modular.performance.service.impl;

import cn.stylefeng.guns.modular.performance.service.PerformanceMetricsService;
import org.springframework.stereotype.Service;
import java.util.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.OperatingSystemMXBean;

@Service
public class PerformanceMetricsServiceImpl implements PerformanceMetricsService {

    private final Map<String, List<Map<String, Object>>> performanceHistory = new HashMap<>();
    private final Map<String, Double> alertThresholds = new HashMap<>();
    private final List<Map<String, Object>> activeAlerts = new ArrayList<>();
    private final Timer metricsCollector = new Timer();

    public PerformanceMetricsServiceImpl() {
        // 初始化告警阈值
        alertThresholds.put("cpuUsage", 0.8);
        alertThresholds.put("memoryUsage", 0.8);
        alertThresholds.put("responseTime", 200.0);
        alertThresholds.put("errorRate", 0.05);
        
        // 启动定时收集指标
        metricsCollector.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectMetrics();
            }
        }, 0, 60000); // 每分钟收集一次
    }

    @Override
    public Map<String, Object> getModelPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", 0.95);
        metrics.put("responseTime", 150);
        metrics.put("throughput", 1000);
        metrics.put("errorRate", 0.02);
        metrics.put("lastUpdate", new Date());
        return metrics;
    }

    @Override
    public Map<String, Object> getSystemPerformanceMetrics() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("cpuUsage", osBean.getSystemLoadAverage());
        metrics.put("memoryUsage", (double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax());
        metrics.put("diskUsage", 0.45);
        metrics.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        metrics.put("lastUpdate", new Date());
        return metrics;
    }

    @Override
    public Map<String, Object> getUserBehaviorMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("activeUsers", 1000);
        metrics.put("sessionDuration", 300);
        metrics.put("conversionRate", 0.25);
        metrics.put("lastUpdate", new Date());
        return metrics;
    }

    @Override
    public Map<String, Object> getApiCallMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalCalls", 10000);
        metrics.put("successRate", 0.98);
        metrics.put("averageLatency", 200);
        metrics.put("lastUpdate", new Date());
        return metrics;
    }

    @Override
    public Map<String, Object> comparePerformance(Map<String, Object> metrics1, Map<String, Object> metrics2) {
        Map<String, Object> comparison = new HashMap<>();
        
        for (String key : metrics1.keySet()) {
            if (metrics2.containsKey(key)) {
                Object value1 = metrics1.get(key);
                Object value2 = metrics2.get(key);
                
                if (value1 instanceof Number && value2 instanceof Number) {
                    double diff = ((Number) value2).doubleValue() - ((Number) value1).doubleValue();
                    double percentage = (diff / ((Number) value1).doubleValue()) * 100;
                    comparison.put(key + "_diff", diff);
                    comparison.put(key + "_percentage", percentage);
                    
                    // 检查是否触发告警
                    checkAlertThreshold(key, ((Number) value2).doubleValue());
                }
            }
        }
        
        return comparison;
    }

    @Override
    public List<Map<String, Object>> getPerformanceHistory(String timeRange) {
        return performanceHistory.getOrDefault(timeRange, new ArrayList<>());
    }

    @Override
    public Map<String, Object> optimizePerformance(Map<String, Object> metrics) {
        Map<String, Object> suggestions = new HashMap<>();
        
        // 检查模型性能指标
        if (metrics.containsKey("accuracy") && ((Number) metrics.get("accuracy")).doubleValue() < 0.9) {
            suggestions.put("accuracy", "建议增加训练数据量或调整模型参数");
        }
        
        if (metrics.containsKey("responseTime") && ((Number) metrics.get("responseTime")).doubleValue() > 200) {
            suggestions.put("responseTime", "建议优化算法复杂度或增加计算资源");
        }
        
        // 检查系统性能指标
        if (metrics.containsKey("cpuUsage") && ((Number) metrics.get("cpuUsage")).doubleValue() > 0.8) {
            suggestions.put("cpuUsage", "建议增加CPU资源或优化代码");
        }
        
        if (metrics.containsKey("memoryUsage") && ((Number) metrics.get("memoryUsage")).doubleValue() > 0.8) {
            suggestions.put("memoryUsage", "建议增加内存资源或优化内存使用");
        }
        
        return suggestions;
    }

    @Override
    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        status.put("lastCheck", new Date());
        status.put("health", calculateHealthStatus());
        status.put("activeAlerts", activeAlerts.size());
        return status;
    }
    
    // 新增方法：设置告警阈值
    public void setAlertThreshold(String metric, double threshold) {
        alertThresholds.put(metric, threshold);
    }
    
    // 新增方法：获取活跃告警
    public List<Map<String, Object>> getActiveAlerts() {
        return new ArrayList<>(activeAlerts);
    }
    
    // 新增方法：清除告警
    public void clearAlert(String alertId) {
        activeAlerts.removeIf(alert -> alertId.equals(alert.get("id")));
    }
    
    // 私有方法：收集指标
    private void collectMetrics() {
        Map<String, Object> currentMetrics = new HashMap<>();
        currentMetrics.putAll(getModelPerformanceMetrics());
        currentMetrics.putAll(getSystemPerformanceMetrics());
        currentMetrics.putAll(getUserBehaviorMetrics());
        currentMetrics.putAll(getApiCallMetrics());
        
        // 保存到历史记录
        String timeKey = new Date().toString();
        performanceHistory.computeIfAbsent(timeKey, k -> new ArrayList<>()).add(currentMetrics);
        
        // 检查告警
        checkAllAlertThresholds(currentMetrics);
    }
    
    // 私有方法：检查所有告警阈值
    private void checkAllAlertThresholds(Map<String, Object> metrics) {
        for (Map.Entry<String, Double> entry : alertThresholds.entrySet()) {
            String metric = entry.getKey();
            double threshold = entry.getValue();
            
            if (metrics.containsKey(metric)) {
                double value = ((Number) metrics.get(metric)).doubleValue();
                checkAlertThreshold(metric, value);
            }
        }
    }
    
    // 私有方法：检查单个告警阈值
    private void checkAlertThreshold(String metric, double value) {
        double threshold = alertThresholds.getOrDefault(metric, 0.0);
        
        if (value > threshold) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("id", UUID.randomUUID().toString());
            alert.put("metric", metric);
            alert.put("value", value);
            alert.put("threshold", threshold);
            alert.put("timestamp", new Date());
            alert.put("status", "active");
            
            activeAlerts.add(alert);
        }
    }
    
    // 私有方法：计算健康状态
    private String calculateHealthStatus() {
        if (activeAlerts.isEmpty()) {
            return "good";
        } else if (activeAlerts.size() < 3) {
            return "warning";
        } else {
            return "critical";
        }
    }
} 