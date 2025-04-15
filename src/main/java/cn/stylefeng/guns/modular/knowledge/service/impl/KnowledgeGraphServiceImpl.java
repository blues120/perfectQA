package cn.stylefeng.guns.modular.knowledge.service.impl;

import cn.stylefeng.guns.modular.knowledge.service.KnowledgeGraphService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private final Map<String, List<Map<String, Object>>> relations = new HashMap<>();
    private final Map<String, Map<String, Object>> entities = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> knowledgeBases = new HashMap<>();

    @Override
    public Map<String, Object> getKnowledgeGraph(String query) {
        Map<String, Object> graph = new HashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        
        // 根据查询条件过滤节点和边
        if (query != null && !query.isEmpty()) {
            // 搜索相关节点
            for (Map<String, Object> entity : entities.values()) {
                if (entity.get("label").toString().toLowerCase().contains(query.toLowerCase()) ||
                    entity.get("description").toString().toLowerCase().contains(query.toLowerCase())) {
                    nodes.add(entity);
                    
                    // 获取相关边
                    String nodeId = (String) entity.get("id");
                    List<Map<String, Object>> nodeRelations = relations.getOrDefault(nodeId, new ArrayList<>());
                    edges.addAll(nodeRelations);
                }
            }
        } else {
            // 返回所有节点和边
            nodes.addAll(entities.values());
            Set<Map<String, Object>> uniqueEdges = new HashSet<>();
            for (List<Map<String, Object>> relationList : relations.values()) {
                uniqueEdges.addAll(relationList);
            }
            edges.addAll(uniqueEdges);
        }
        
        graph.put("nodes", nodes);
        graph.put("edges", edges);
        graph.put("totalNodes", nodes.size());
        graph.put("totalEdges", edges.size());
        
        return graph;
    }

    @Override
    public boolean addKnowledgeNode(Map<String, Object> node) {
        String nodeId = (String) node.get("id");
        if (nodeId != null) {
            // 添加节点属性
            node.put("createTime", new Date());
            node.put("updateTime", new Date());
            node.put("status", "active");
            
            entities.put(nodeId, node);
            
            // 更新知识库
            String kbId = (String) node.get("knowledgeBaseId");
            if (kbId != null) {
                knowledgeBases.computeIfAbsent(kbId, k -> new ArrayList<>()).add(node);
            }
            
            return true;
        }
        return false;
    }

    @Override
    public boolean addKnowledgeRelation(Map<String, Object> relation) {
        String sourceId = (String) relation.get("source");
        String targetId = (String) relation.get("target");
        if (sourceId != null && targetId != null && entities.containsKey(sourceId) && entities.containsKey(targetId)) {
            // 添加关系属性
            relation.put("createTime", new Date());
            relation.put("updateTime", new Date());
            relation.put("status", "active");
            
            relations.computeIfAbsent(sourceId, k -> new ArrayList<>()).add(relation);
            relations.computeIfAbsent(targetId, k -> new ArrayList<>()).add(relation);
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> searchNodes(String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Map<String, Object> entity : entities.values()) {
            if (entity.get("label").toString().toLowerCase().contains(keyword.toLowerCase()) ||
                entity.get("description").toString().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(entity);
            }
        }
        
        // 按相关度排序
        results.sort((a, b) -> {
            String labelA = a.get("label").toString().toLowerCase();
            String labelB = b.get("label").toString().toLowerCase();
            return Integer.compare(labelB.indexOf(keyword.toLowerCase()), labelA.indexOf(keyword.toLowerCase()));
        });
        
        return results;
    }

    @Override
    public List<Map<String, Object>> getNodeRelations(String nodeId) {
        List<Map<String, Object>> nodeRelations = relations.getOrDefault(nodeId, new ArrayList<>());
        
        // 添加相关节点信息
        for (Map<String, Object> relation : nodeRelations) {
            String sourceId = (String) relation.get("source");
            String targetId = (String) relation.get("target");
            
            if (sourceId.equals(nodeId)) {
                relation.put("relatedNode", entities.get(targetId));
            } else {
                relation.put("relatedNode", entities.get(sourceId));
            }
        }
        
        return nodeRelations;
    }

    @Override
    public boolean updateKnowledgeNode(String nodeId, Map<String, Object> node) {
        if (entities.containsKey(nodeId)) {
            // 保留原有属性
            Map<String, Object> existingNode = entities.get(nodeId);
            node.put("createTime", existingNode.get("createTime"));
            node.put("updateTime", new Date());
            node.put("status", existingNode.get("status"));
            
            entities.put(nodeId, node);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteKnowledgeNode(String nodeId) {
        if (entities.containsKey(nodeId)) {
            // 删除节点
            entities.remove(nodeId);
            
            // 删除相关关系
            relations.remove(nodeId);
            
            // 从知识库中移除
            for (List<Map<String, Object>> kb : knowledgeBases.values()) {
                kb.removeIf(node -> nodeId.equals(node.get("id")));
            }
            
            return true;
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getRelations() {
        Set<Map<String, Object>> uniqueRelations = new HashSet<>();
        for (List<Map<String, Object>> relationList : relations.values()) {
            uniqueRelations.addAll(relationList);
        }
        return new ArrayList<>(uniqueRelations);
    }

    @Override
    public List<Map<String, Object>> getEntities() {
        return new ArrayList<>(entities.values());
    }
    
    // 新增方法：获取知识库列表
    public List<Map<String, Object>> getKnowledgeBases() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : knowledgeBases.entrySet()) {
            Map<String, Object> kbInfo = new HashMap<>();
            kbInfo.put("id", entry.getKey());
            kbInfo.put("name", entry.getKey());
            kbInfo.put("nodeCount", entry.getValue().size());
            result.add(kbInfo);
        }
        return result;
    }
    
    // 新增方法：获取知识库详情
    public Map<String, Object> getKnowledgeBaseDetail(String kbId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> nodes = knowledgeBases.getOrDefault(kbId, new ArrayList<>());
        
        result.put("id", kbId);
        result.put("name", kbId);
        result.put("nodes", nodes);
        result.put("nodeCount", nodes.size());
        
        return result;
    }
} 