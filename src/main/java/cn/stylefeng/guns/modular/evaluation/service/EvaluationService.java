package cn.stylefeng.guns.modular.evaluation.service;

import cn.stylefeng.guns.modular.evaluation.entity.EvaluationResult;
import java.util.List;

public interface EvaluationService {
    /**
     * 评估回答的质量
     * @param question 问题
     * @param answer 回答
     * @param context 上下文
     * @return 评估结果
     */
    EvaluationResult evaluateResponse(String question, String answer, List<String> context);
} 