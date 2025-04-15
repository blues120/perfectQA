<template>
  <el-card class="text-to-image-container">
    <template #header>
      <div class="card-header">
        <span>文生图</span>
        <el-select v-model="selectedModel" placeholder="选择模型">
          <el-option
            v-for="model in models"
            :key="model.value"
            :label="model.label"
            :value="model.value"
          />
        </el-select>
      </div>
    </template>

    <div class="prompt-input">
      <el-input
        v-model="prompt"
        type="textarea"
        :rows="3"
        placeholder="请输入图片描述..."
        :maxlength="500"
        show-word-limit
      />
    </div>

    <div class="generate-button">
      <el-button
        type="primary"
        :loading="generating"
        @click="generateImage"
      >
        生成图片
      </el-button>
    </div>

    <div v-if="imageData" class="image-result">
      <el-image
        :src="imageData"
        fit="contain"
        :preview-src-list="[imageData]"
      />
    </div>

    <div v-if="error" class="error-message">
      <el-alert
        :title="error"
        type="error"
        show-icon
        @close="error = ''"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const prompt = ref('')
const selectedModel = ref('stable-diffusion')
const generating = ref(false)
const imageData = ref('')
const error = ref('')

const models = [
  { value: 'stable-diffusion', label: 'Stable Diffusion' },
  { value: 'dall-e', label: 'DALL-E' },
  { value: 'midjourney', label: 'Midjourney' }
]

const generateImage = async () => {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入图片描述')
    return
  }

  generating.value = true
  error.value = ''
  imageData.value = ''

  try {
    const response = await axios.post('/api/v1/text-to-image/generate', {
      prompt: prompt.value,
      model: selectedModel.value
    }, {
      headers: {
        'X-User-ID': 'current-user-id' // 这里需要替换为实际的用户ID
      }
    })

    imageData.value = response.data.image_data
    ElMessage.success('图片生成成功')
  } catch (err) {
    error.value = err.response?.data?.message || '生成图片失败，请重试'
    ElMessage.error(error.value)
  } finally {
    generating.value = false
  }
}
</script>

<style scoped>
.text-to-image-container {
  max-width: 800px;
  margin: 20px auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.prompt-input {
  margin: 20px 0;
}

.generate-button {
  margin: 20px 0;
  text-align: center;
}

.image-result {
  margin: 20px 0;
  text-align: center;
}

.image-result .el-image {
  max-width: 100%;
  max-height: 500px;
}

.error-message {
  margin: 20px 0;
}
</style> 