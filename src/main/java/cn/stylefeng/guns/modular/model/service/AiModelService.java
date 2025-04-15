package cn.stylefeng.guns.modular.model.service;

import cn.stylefeng.guns.modular.model.model.ModelRequest;
import cn.stylefeng.guns.modular.model.model.ModelResponse;
import cn.stylefeng.guns.modular.model.model.OllamaRequest;
import cn.stylefeng.guns.modular.model.model.OllamaResponse;
import cn.stylefeng.guns.modular.model.model.TextSegment;
import cn.stylefeng.guns.modular.model.entity.Model;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * AI模型服务接口
 * 提供模型管理、调用和状态查询等功能
 */
public interface AiModelService {
    /**
     * 生成文本
     * @param request 请求参数
     * @return 生成结果
     */
    ModelResponse generate(ModelRequest request);
    
    /**
     * 对话
     * @param request 请求参数
     * @return 对话结果
     */
    ModelResponse chat(ModelRequest request);

    /**
     * 流式对话
     * @param request 请求参数
     * @return SSE发射器
     */
    SseEmitter streamChat(ModelRequest request);
    
    /**
     * 向量化
     * @param request 请求参数
     * @return 向量化结果
     */
    ModelResponse embedding(ModelRequest request);

    /**
     * 获取可用模型列表
     */
    Map<String, Object> getAvailableModels();

    /**
     * 获取服务状态
     */
    Map<String, Object> getServiceStatus();

    /**
     * Ollama模型生成
     */
    OllamaResponse ollamaGenerate(OllamaRequest request);

    /**
     * Ollama模型对话
     */
    OllamaResponse ollamaChat(OllamaRequest request);

    /**
     * Ollama模型流式对话
     */
    SseEmitter ollamaStreamChat(OllamaRequest request);

    /**
     * Ollama模型向量化
     */
    OllamaResponse ollamaEmbedding(OllamaRequest request);

    /**
     * 创建模型
     */
    Boolean createModel(Model model);

    /**
     * 更新模型
     */
    Boolean updateModel(Model model);

    /**
     * 删除模型
     */
    Boolean deleteModel(String modelId);

    /**
     * 获取模型详情
     */
    Map<String, Object> getModelDetails(String modelId);

    /**
     * 获取模型列表
     */
    List<Model> getModelList();

    /**
     * 添加文档
     */
    Boolean addDocuments(List<TextSegment> documents);
} 