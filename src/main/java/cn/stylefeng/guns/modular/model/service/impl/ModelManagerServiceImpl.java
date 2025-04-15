package cn.stylefeng.guns.modular.model.service.impl;

import cn.stylefeng.guns.modular.model.entity.ModelConfig;
import cn.stylefeng.guns.modular.model.entity.ModelMetrics;
import cn.stylefeng.guns.modular.model.mapper.ModelConfigMapper;
import cn.stylefeng.guns.modular.model.service.ModelManagerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ModelManagerServiceImpl extends ServiceImpl<ModelConfigMapper, ModelConfig> implements ModelManagerService {
    private final Map<String, ModelMetrics> modelMetricsMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> modelLoadMap = new ConcurrentHashMap<>();
    private final Map<String, Long> modelLastUsedMap = new ConcurrentHashMap<>();

    @Override
    public ModelConfig getDefaultModel() {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getIsDefault, true);
        return getOne(wrapper);
    }

    @Override
    public ModelConfig getModelByName(String name) {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getName, name);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ModelConfig modelConfig) {
        if (modelConfig.getIsDefault()) {
            // Reset all other models' default status
            LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ModelConfig::getIsDefault, true);
            List<ModelConfig> defaultModels = list(wrapper);
            for (ModelConfig model : defaultModels) {
                model.setIsDefault(false);
                updateById(model);
            }
        }
        return super.save(modelConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ModelConfig modelConfig) {
        if (modelConfig.getIsDefault()) {
            ModelConfig existingModel = getById(modelConfig.getId());
            if (existingModel != null && !existingModel.getIsDefault()) {
                // Reset all other models' default status
                LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ModelConfig::getIsDefault, true);
                List<ModelConfig> defaultModels = list(wrapper);
                for (ModelConfig model : defaultModels) {
                    model.setIsDefault(false);
                    updateById(model);
                }
            }
        }
        return super.updateById(modelConfig);
    }

    @Override
    public String selectOptimalModel() {
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getIsActive, true);
        List<ModelConfig> activeModels = list(wrapper);
        
        if (activeModels.isEmpty()) {
            throw new RuntimeException("No active models available");
        }

        // 获取当前负载最低的模型
        return activeModels.stream()
                .min(Comparator.comparingInt(model -> 
                    modelLoadMap.getOrDefault(model.getName(), new AtomicInteger(0)).get()))
                .map(ModelConfig::getName)
                .orElse(activeModels.get(0).getName());
    }

    @Override
    public void recordModelUsage(String modelName) {
        modelLoadMap.computeIfAbsent(modelName, k -> new AtomicInteger(0)).incrementAndGet();
        modelLastUsedMap.put(modelName, System.currentTimeMillis());
    }

    @Override
    public void recordModelCompletion(String modelName) {
        modelLoadMap.computeIfAbsent(modelName, k -> new AtomicInteger(0)).decrementAndGet();
    }

    @Override
    public void recordModelPerformance(String modelName, long responseTime, int tokenCount) {
        ModelMetrics metrics = modelMetricsMap.computeIfAbsent(modelName, k -> new ModelMetrics());
        metrics.recordResponse(responseTime, tokenCount);
    }

    @Override
    public void recordModelError(String modelName) {
        ModelMetrics metrics = modelMetricsMap.computeIfAbsent(modelName, k -> new ModelMetrics());
        metrics.recordError();
    }

    @Override
    public Map<String, ModelMetrics> getModelPerformanceMetrics() {
        return modelMetricsMap;
    }

    @Override
    public Map<String, String> getAvailableModels() {
        Map<String, String> models = new HashMap<>();
        LambdaQueryWrapper<ModelConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelConfig::getIsActive, true);
        list(wrapper).forEach(model -> 
            models.put(model.getName(), model.getDescription()));
        return models;
    }
} 