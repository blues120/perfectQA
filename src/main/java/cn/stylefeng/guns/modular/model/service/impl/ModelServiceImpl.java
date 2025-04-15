package cn.stylefeng.guns.modular.model.service.impl;

import cn.stylefeng.guns.modular.model.service.ModelService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ModelServiceImpl implements ModelService {

    // 存储模型信息
    private final Map<String, Map<String, Object>> models = new ConcurrentHashMap<>();
    private final Map<String, List<String>> modelVersions = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> createModel(Map<String, Object> modelInfo) {
        validateModelInfo(modelInfo);
        String modelId = UUID.randomUUID().toString();
        modelInfo.put("id", modelId);
        modelInfo.put("createTime", new Date());
        modelInfo.put("updateTime", new Date());
        models.put(modelId, modelInfo);
        modelVersions.put(modelId, new ArrayList<>());
        return modelInfo;
    }

    @Override
    public Map<String, Object> updateModel(String modelId, Map<String, Object> updates) {
        Map<String, Object> model = getModel(modelId);
        if (model == null) {
            throw new IllegalArgumentException("Model not found");
        }
        validateModelUpdate(updates);
        updates.forEach((key, value) -> {
            if (!key.equals("id") && !key.equals("createTime")) {
                model.put(key, value);
            }
        });
        model.put("updateTime", new Date());
        return model;
    }

    @Override
    public boolean deleteModel(String modelId) {
        if (!models.containsKey(modelId)) {
            return false;
        }
        models.remove(modelId);
        modelVersions.remove(modelId);
        return true;
    }

    @Override
    public Map<String, Object> getModel(String modelId) {
        return models.get(modelId);
    }

    @Override
    public List<Map<String, Object>> listModels() {
        return new ArrayList<>(models.values());
    }

    @Override
    public String createModelVersion(String modelId, Map<String, Object> versionInfo) {
        if (!models.containsKey(modelId)) {
            throw new IllegalArgumentException("Model not found");
        }
        validateVersionInfo(versionInfo);
        String version = versionInfo.get("version").toString();
        List<String> versions = modelVersions.get(modelId);
        if (versions.contains(version)) {
            throw new IllegalArgumentException("Version already exists");
        }
        versions.add(version);
        return version;
    }

    @Override
    public List<String> getModelVersions(String modelId) {
        return modelVersions.getOrDefault(modelId, new ArrayList<>());
    }

    private void validateModelInfo(Map<String, Object> modelInfo) {
        if (modelInfo == null) {
            throw new IllegalArgumentException("Model info cannot be null");
        }
        if (!modelInfo.containsKey("name")) {
            throw new IllegalArgumentException("Model name is required");
        }
        if (!modelInfo.containsKey("type")) {
            throw new IllegalArgumentException("Model type is required");
        }
    }

    private void validateModelUpdate(Map<String, Object> updates) {
        if (updates == null) {
            throw new IllegalArgumentException("Updates cannot be null");
        }
        if (updates.containsKey("id")) {
            throw new IllegalArgumentException("Cannot update model ID");
        }
    }

    private void validateVersionInfo(Map<String, Object> versionInfo) {
        if (versionInfo == null) {
            throw new IllegalArgumentException("Version info cannot be null");
        }
        if (!versionInfo.containsKey("version")) {
            throw new IllegalArgumentException("Version number is required");
        }
    }
} 