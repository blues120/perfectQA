<template>
  <div class="arxiv-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>ARXIV文献对话</span>
          <el-input
            v-model="paperId"
            placeholder="输入论文ID"
            style="width: 200px"
          />
          <el-button type="primary" @click="loadPaper">加载论文</el-button>
        </div>
      </template>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" 
               :class="['message', message.type]">
            <div class="message-content">{{ message.content }}</div>
            <div v-if="message.paperInfo" class="paper-info">
              <h4>{{ message.paperInfo.title }}</h4>
              <p>{{ message.paperInfo.authors.join(', ') }}</p>
              <p>{{ message.paperInfo.abstract }}</p>
            </div>
          </div>
        </div>

        <div class="input-area">
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
  name: 'ArxivChat',
  setup() {
    const messages = ref([])
    const userInput = ref('')
    const loading = ref(false)
    const paperId = ref('')
    const currentPaperInfo = ref(null)
    const messagesContainer = ref(null)

    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    const loadPaper = async () => {
      if (!paperId.value.trim()) return

      try {
        const response = await axios.get(`/api/v1/arxiv/paper/${paperId.value}`)
        currentPaperInfo.value = response.data
        messages.value.push({
          type: 'system',
          content: '论文已加载',
          paperInfo: currentPaperInfo.value
        })
        ElMessage.success('论文加载成功')
      } catch (error) {
        ElMessage.error('论文加载失败')
      }
    }

    const sendMessage = async () => {
      if (!userInput.value.trim() || !currentPaperInfo.value) return

      const message = userInput.value
      messages.value.push({ type: 'user', content: message })
      userInput.value = ''
      loading.value = true

      try {
        const response = await axios.post('/api/v1/arxiv/chat', {
          paperId: paperId.value,
          query: message,
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
      paperId,
      currentPaperInfo,
      messagesContainer,
      loadPaper,
      sendMessage
    }
  }
}
</script>

<style scoped>
.arxiv-chat {
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

.message.system {
  background-color: #f0f0f0;
  margin: 0 auto;
  max-width: 80%;
}

.paper-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
}

.paper-info h4 {
  margin: 0 0 10px 0;
}

.paper-info p {
  margin: 5px 0;
  color: #666;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 