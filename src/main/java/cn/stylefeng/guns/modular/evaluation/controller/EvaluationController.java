package cn.stylefeng.guns.modular.evaluation.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.evaluation.entity.EvaluationResult;
import cn.stylefeng.guns.modular.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/evaluation")
@Validated
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/evaluate")
    public ApiResponse<EvaluationResult> evaluateResponse(
            @RequestParam @NotBlank(message = "问题不能为空") String question,
            @RequestParam @NotBlank(message = "回答不能为空") String answer,
            @RequestParam(required = false) List<String> context) {
        try {
            EvaluationResult result = evaluationService.evaluateResponse(question, answer, context);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("评估失败：" + e.getMessage());
        }
    }

    @PostMapping("/batch-evaluate")
    public ApiResponse<List<EvaluationResult>> batchEvaluate(
            @RequestBody @Valid @NotNull(message = "请求列表不能为空") List<EvaluationRequest> requests) {
        try {
            List<EvaluationResult> results = requests.stream()
                    .map(request -> evaluationService.evaluateResponse(
                            request.getQuestion(),
                            request.getAnswer(),
                            request.getContext()))
                    .collect(Collectors.toList());
            return ApiResponse.success(results);
        } catch (Exception e) {
            return ApiResponse.error("批量评估失败：" + e.getMessage());
        }
    }

    public static class EvaluationRequest {
        @NotBlank(message = "问题不能为空")
        private String question;

        @NotBlank(message = "回答不能为空")
        private String answer;

        private List<String> context;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public List<String> getContext() {
            return context;
        }

        public void setContext(List<String> context) {
            this.context = context;
        }
    }
}
