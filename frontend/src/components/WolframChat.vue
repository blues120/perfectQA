<template>
  <div class="wolfram-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>Wolfram对话</span>
        </div>
      </template>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" 
               :class="['message', message.type]">
            <div class="message-content">{{ message.content }}</div>
            <div v-if="message.wolframResult" class="wolfram-result">
              <el-image v-if="message.wolframResult.image" 
                       :src="message.wolframResult.image" 
                       fit="contain" />
              <div v-if="message.wolframResult.text" 
                   class="result-text">
                {{ message.wolframResult.text }}
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="输入数学问题或计算表达式..."
            @keyup.enter.native="sendMessage"
          />
          <el-button type="primary" @click="sendMessage" :loading="loading">
            计算
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
  name: 'WolframChat',
  setup() {
    const messages = ref([])
    const userInput = ref('')
    const loading = ref(false)
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
        const response = await axios.post('/api/v1/wolfram/chat', {
          query: message,
          userId: 'current-user-id' // TODO: 从用户认证获取
        })

        messages.value.push({
          type: 'assistant',
          content: response.data.response,
          wolframResult: response.data.result
        })
      } catch (error) {
        ElMessage.error('计算失败')
      } finally {
        loading.value = false
        scrollToBottom()
      }
    }

    return {
      messages,
      userInput,
      loading,
      messagesContainer,
      sendMessage
    }
  }
}
</script>

<style scoped>
.wolfram-chat {
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

.wolfram-result {
  margin-top: 10px;
}

.wolfram-result .el-image {
  max-width: 100%;
  max-height: 300px;
}

.result-text {
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 