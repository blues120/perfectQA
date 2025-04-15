package cn.stylefeng.guns.modular.model.entity;

import lombok.Data;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class ModelMetrics {
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong totalTokens = new AtomicLong(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger totalErrors = new AtomicInteger(0);

    public void recordResponse(long responseTime, int tokenCount) {
        totalResponseTime.addAndGet(responseTime);
        totalTokens.addAndGet(tokenCount);
        totalRequests.incrementAndGet();
    }

    public void recordError() {
        totalErrors.incrementAndGet();
    }

    public double getAverageResponseTime() {
        return totalRequests.get() > 0 ? 
            (double) totalResponseTime.get() / totalRequests.get() : 0;
    }

    public double getAverageTokensPerRequest() {
        return totalRequests.get() > 0 ? 
            (double) totalTokens.get() / totalRequests.get() : 0;
    }

    public double getErrorRate() {
        return totalRequests.get() > 0 ? 
            (double) totalErrors.get() / totalRequests.get() : 0;
    }
} 