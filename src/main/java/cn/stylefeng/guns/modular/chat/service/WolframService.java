package cn.stylefeng.guns.modular.chat.service;

import java.util.Map;

public interface WolframService {
    
    /**
     * 执行数学计算
     * @param expression 数学表达式
     * @return 计算结果
     */
    String calculate(String expression);
    
    /**
     * 获取数学步骤
     * @param expression 数学表达式
     * @return 计算步骤
     */
    String getSteps(String expression);
    
    /**
     * 执行单位转换
     * @param value 数值
     * @param fromUnit 源单位
     * @param toUnit 目标单位
     * @return 转换结果
     */
    String convertUnit(double value, String fromUnit, String toUnit);
    
    /**
     * 获取数学公式图像
     * @param expression 数学表达式
     * @return 图像URL
     */
    String getFormulaImage(String expression);
    
    /**
     * 执行数据分析
     * @param data 数据
     * @param analysisType 分析类型
     * @return 分析结果
     */
    Map<String, Object> analyzeData(String data, String analysisType);
} 