<template>
  <div class="model-performance">
    <h2>模型性能监控</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-for="(metrics, modelName) in modelMetrics" :key="modelName" class="model-card">
        <h3>{{ modelName }}</h3>
        <div class="metrics">
          <div class="metric">
            <span class="label">平均响应时间:</span>
            <span class="value">{{ metrics.averageResponseTime.toFixed(2) }}ms</span>
          </div>
          <div class="metric">
            <span class="label">总请求数:</span>
            <span class="value">{{ metrics.totalRequests }}</span>
          </div>
          <div class="metric">
            <span class="label">错误率:</span>
            <span class="value">{{ (metrics.errorRate * 100).toFixed(2) }}%</span>
          </div>
          <div class="metric">
            <span class="label">平均Token数:</span>
            <span class="value">{{ metrics.averageTokens.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';

export default {
  name: 'ModelPerformance',
  setup() {
    const modelMetrics = ref({});
    const loading = ref(true);

    const fetchMetrics = async () => {
      try {
        const response = await axios.get('/api/models/performance/metrics');
        modelMetrics.value = response.data;
      } catch (error) {
        console.error('Failed to fetch model metrics:', error);
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      fetchMetrics();
      // 每30秒刷新一次数据
      setInterval(fetchMetrics, 30000);
    });

    return {
      modelMetrics,
      loading
    };
  }
};
</script>

<style scoped>
.model-performance {
  padding: 20px;
}

.model-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.metric {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.9em;
  color: #666;
}

.value {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}
</style> 