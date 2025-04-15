package cn.stylefeng.guns.modular.knowledge.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.knowledge.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/graph")
public class KnowledgeGraphController {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getKnowledgeGraph(@RequestParam(required = false) String query) {
        try {
            Map<String, Object> graph = knowledgeGraphService.getKnowledgeGraph(query);
            return ApiResponse.success(graph);
        } catch (Exception e) {
            return ApiResponse.error("获取知识图谱失败：" + e.getMessage());
        }
    }

    @PostMapping("/node")
    public ApiResponse<Boolean> addKnowledgeNode(@RequestBody Map<String, Object> node) {
        try {
            boolean result = knowledgeGraphService.addKnowledgeNode(node);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("添加知识节点失败：" + e.getMessage());
        }
    }

    @PostMapping("/relation")
    public ApiResponse<Boolean> addKnowledgeRelation(@RequestBody Map<String, Object> relation) {
        try {
            boolean result = knowledgeGraphService.addKnowledgeRelation(relation);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("添加知识关系失败：" + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<Map<String, Object>>> searchNodes(@RequestParam String keyword) {
        try {
            List<Map<String, Object>> nodes = knowledgeGraphService.searchNodes(keyword);
            return ApiResponse.success(nodes);
        } catch (Exception e) {
            return ApiResponse.error("搜索知识节点失败：" + e.getMessage());
        }
    }

    @GetMapping("/node/{nodeId}/relations")
    public ApiResponse<List<Map<String, Object>>> getNodeRelations(@PathVariable String nodeId) {
        try {
            List<Map<String, Object>> relations = knowledgeGraphService.getNodeRelations(nodeId);
            return ApiResponse.success(relations);
        } catch (Exception e) {
            return ApiResponse.error("获取节点关系失败：" + e.getMessage());
        }
    }

    @PutMapping("/node/{nodeId}")
    public ApiResponse<Boolean> updateKnowledgeNode(@PathVariable String nodeId, @RequestBody Map<String, Object> node) {
        try {
            boolean result = knowledgeGraphService.updateKnowledgeNode(nodeId, node);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("更新知识节点失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/node/{nodeId}")
    public ApiResponse<Boolean> deleteKnowledgeNode(@PathVariable String nodeId) {
        try {
            boolean result = knowledgeGraphService.deleteKnowledgeNode(nodeId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("删除知识节点失败：" + e.getMessage());
        }
    }

    @GetMapping("/relations")
    public ApiResponse<List<Map<String, Object>>> getRelations() {
        try {
            List<Map<String, Object>> relations = knowledgeGraphService.getRelations();
            return ApiResponse.success(relations);
        } catch (Exception e) {
            return ApiResponse.error("获取关系列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/entities")
    public ApiResponse<List<Map<String, Object>>> getEntities() {
        try {
            List<Map<String, Object>> entities = knowledgeGraphService.getEntities();
            return ApiResponse.success(entities);
        } catch (Exception e) {
            return ApiResponse.error("获取实体列表失败：" + e.getMessage());
        }
    }
}
