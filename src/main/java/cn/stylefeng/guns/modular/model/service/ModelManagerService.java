package cn.stylefeng.guns.modular.model.service;

import cn.stylefeng.guns.modular.model.entity.ModelConfig;
import cn.stylefeng.guns.modular.model.entity.ModelMetrics;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ModelManagerService extends IService<ModelConfig> {
    ModelConfig getDefaultModel();
    
    ModelConfig getModelByName(String name);
    
    String selectOptimalModel();
    
    void recordModelUsage(String modelName);
    
    void recordModelCompletion(String modelName);
    
    void recordModelPerformance(String modelName, long responseTime, int tokenCount);
    
    void recordModelError(String modelName);
    
    Map<String, ModelMetrics> getModelPerformanceMetrics();
    
    Map<String, String> getAvailableModels();
} 