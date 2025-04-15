package cn.stylefeng.guns.modular.knowledge.service;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱服务接口
 */
public interface KnowledgeGraphService {
    
    /**
     * 获取知识图谱
     * @param query 查询条件
     * @return 知识图谱数据
     */
    Map<String, Object> getKnowledgeGraph(String query);
    
    /**
     * 添加知识节点
     * @param node 节点数据
     * @return 添加结果
     */
    boolean addKnowledgeNode(Map<String, Object> node);
    
    /**
     * 添加知识关系
     * @param relation 关系数据
     * @return 添加结果
     */
    boolean addKnowledgeRelation(Map<String, Object> relation);
    
    /**
     * 搜索知识节点
     * @param keyword 关键词
     * @return 匹配的节点列表
     */
    List<Map<String, Object>> searchNodes(String keyword);
    
    /**
     * 获取节点关系
     * @param nodeId 节点ID
     * @return 节点关系列表
     */
    List<Map<String, Object>> getNodeRelations(String nodeId);
    
    /**
     * 更新知识节点
     * @param nodeId 节点ID
     * @param node 节点数据
     * @return 更新结果
     */
    boolean updateKnowledgeNode(String nodeId, Map<String, Object> node);
    
    /**
     * 删除知识节点
     * @param nodeId 节点ID
     * @return 删除结果
     */
    boolean deleteKnowledgeNode(String nodeId);

    /**
     * 获取所有关系
     * @return 关系列表
     */
    List<Map<String, Object>> getRelations();

    /**
     * 获取所有实体
     * @return 实体列表
     */
    List<Map<String, Object>> getEntities();
} 