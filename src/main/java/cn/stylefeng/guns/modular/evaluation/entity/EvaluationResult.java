package cn.stylefeng.guns.modular.evaluation.entity;

import java.util.List;

public class EvaluationResult {
    private double relevanceScore;
    private double completenessScore;
    private double coherenceScore;
    private double overallScore;
    private List<String> suggestions;
    
    public double getRelevanceScore() {
        return relevanceScore;
    }
    
    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
    
    public double getCompletenessScore() {
        return completenessScore;
    }
    
    public void setCompletenessScore(double completenessScore) {
        this.completenessScore = completenessScore;
    }
    
    public double getCoherenceScore() {
        return coherenceScore;
    }
    
    public void setCoherenceScore(double coherenceScore) {
        this.coherenceScore = coherenceScore;
    }
    
    public double getOverallScore() {
        return overallScore;
    }
    
    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
} 