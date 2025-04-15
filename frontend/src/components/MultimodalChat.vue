<template>
  <div class="multimodal-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>多模态图片对话</span>
        </div>
      </template>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" 
               :class="['message', message.type]">
            <div v-if="message.imageUrl" class="message-image">
              <el-image :src="message.imageUrl" fit="contain" />
            </div>
            <div class="message-content">{{ message.content }}</div>
          </div>
        </div>

        <div class="input-area">
          <el-upload
            class="upload-area"
            action="/api/v1/multimodal/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>

          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="输入您的问题..."
            @keyup.enter.native="sendMessage"
          />
          <el-button type="primary" @click="sendMessage" :loading="loading">
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'MultimodalChat',
  setup() {
    const messages = ref([])
    const userInput = ref('')
    const loading = ref(false)
    const currentImageUrl = ref('')
    const messagesContainer = ref(null)

    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    const handleUploadSuccess = (response) => {
      currentImageUrl.value = response.url
      ElMessage.success('图片上传成功')
    }

    const handleUploadError = () => {
      ElMessage.error('图片上传失败')
    }

    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      if (!isImage) {
        ElMessage.error('只能上传图片文件')
        return false
      }
      return true
    }

    const sendMessage = async () => {
      if (!userInput.value.trim() || !currentImageUrl.value) return

      const message = userInput.value
      messages.value.push({ 
        type: 'user', 
        content: message,
        imageUrl: currentImageUrl.value
      })
      userInput.value = ''
      loading.value = true

      try {
        const response = await axios.post('/api/v1/multimodal/chat', {
          imageUrl: currentImageUrl.value,
          prompt: message,
          userId: 'current-user-id' // TODO: 从用户认证获取
        })

        messages.value.push({
          type: 'assistant',
          content: response.data.response
        })
      } catch (error) {
        ElMessage.error('发送消息失败')
      } finally {
        loading.value = false
        scrollToBottom()
      }
    }

    return {
      messages,
      userInput,
      loading,
      currentImageUrl,
      messagesContainer,
      handleUploadSuccess,
      handleUploadError,
      beforeUpload,
      sendMessage
    }
  }
}
</script>

<style scoped>
.multimodal-chat {
  padding: 20px;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 10px;
}

.message {
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 4px;
}

.message.user {
  background-color: #e6f7ff;
  margin-left: auto;
  max-width: 80%;
}

.message.assistant {
  background-color: #f0f0f0;
  margin-right: auto;
  max-width: 80%;
}

.message-image {
  margin-bottom: 10px;
}

.message-image .el-image {
  max-width: 100%;
  max-height: 300px;
}

.input-area {
  display: flex;
  gap: 10px;
}

.upload-area {
  margin-right: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 