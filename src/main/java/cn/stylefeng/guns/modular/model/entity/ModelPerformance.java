package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("model_performance")
public class ModelPerformance {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String modelId;
    private String version;
    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Double latency;
    private Integer totalSamples;
    private Integer correctPredictions;
    private String testDataset;
    private String evaluationMetrics;
    private Date evaluationTime;
    private String status;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String remark;
} 