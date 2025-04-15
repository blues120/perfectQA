package cn.stylefeng.guns.modular.model.service;

import java.util.List;
import java.util.Map;

public interface ModelService {
    /**
     * 创建新模型
     * @param modelInfo 模型信息
     * @return 创建后的模型信息
     */
    Map<String, Object> createModel(Map<String, Object> modelInfo);

    /**
     * 更新模型信息
     * @param modelId 模型ID
     * @param updates 更新内容
     * @return 更新后的模型信息
     */
    Map<String, Object> updateModel(String modelId, Map<String, Object> updates);

    /**
     * 删除模型
     * @param modelId 模型ID
     * @return 是否删除成功
     */
    boolean deleteModel(String modelId);

    /**
     * 获取模型信息
     * @param modelId 模型ID
     * @return 模型信息
     */
    Map<String, Object> getModel(String modelId);

    /**
     * 获取所有模型列表
     * @return 模型列表
     */
    List<Map<String, Object>> listModels();

    /**
     * 创建模型版本
     * @param modelId 模型ID
     * @param versionInfo 版本信息
     * @return 版本号
     */
    String createModelVersion(String modelId, Map<String, Object> versionInfo);

    /**
     * 获取模型的所有版本
     * @param modelId 模型ID
     * @return 版本列表
     */
    List<String> getModelVersions(String modelId);
} 