<template>
  <div class="search-engine-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>搜索引擎对话</span>
          <el-select v-model="selectedEngine" placeholder="选择搜索引擎" style="width: 200px">
            <el-option label="Google" value="google" />
            <el-option label="Bing" value="bing" />
            <el-option label="Baidu" value="baidu" />
          </el-select>
        </div>
      </template>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" 
               :class="['message', message.type]">
            <div class="message-content">{{ message.content }}</div>
            <div v-if="message.searchResults" class="search-results">
              <el-card v-for="(result, idx) in message.searchResults" :key="idx" 
                       class="result-card">
                <template #header>
                  <div class="result-header">
                    <a :href="result.url" target="_blank">{{ result.title }}</a>
                  </div>
                </template>
                <div class="result-snippet">{{ result.snippet }}</div>
              </el-card>
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
  name: 'SearchEngineChat',
  setup() {
    const messages = ref([])
    const userInput = ref('')
    const loading = ref(false)
    const selectedEngine = ref('google')
    const messagesContainer = ref(null)

    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    const sendMessage = async () => {
      if (!userInput.value.trim()) return

      const message = userInput.value
      messages.value.push({ type: 'user', content: message })
      userInput.value = ''
      loading.value = true

      try {
        const response = await axios.post('/api/v1/search/chat', {
          query: message,
          engine: selectedEngine.value
        })

        messages.value.push({
          type: 'assistant',
          content: response.data.answer,
          searchResults: response.data.results
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
      selectedEngine,
      messagesContainer,
      sendMessage
    }
  }
}
</script>

<style scoped>
.search-engine-chat {
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

.search-results {
  margin-top: 10px;
}

.result-card {
  margin-bottom: 10px;
}

.result-header {
  font-weight: bold;
}

.result-snippet {
  color: #666;
  font-size: 0.9em;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 