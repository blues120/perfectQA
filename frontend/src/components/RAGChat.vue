<template>
  <div class="rag-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>Chat History</span>
          <el-button type="text" @click="clearHistory">Clear History</el-button>
        </div>
      </template>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" class="message-wrapper">
            <div :class="['message', message.role]">
              <div class="message-header">
                <span class="role">{{ message.role === 'user' ? 'You' : 'Assistant' }}</span>
                <span class="time">{{ formatTime(message.timestamp) }}</span>
              </div>
              <div class="message-content">{{ message.content }}</div>
              <div v-if="message.relevantDocs" class="relevant-docs">
                <div class="docs-title">Relevant Documents:</div>
                <div v-for="(doc, docIndex) in message.relevantDocs" :key="docIndex" class="doc-item">
                  <div class="doc-header">
                    <span class="doc-title">{{ doc.title }}</span>
                    <el-button 
                      type="text" 
                      size="small" 
                      @click="previewDocument(doc)"
                    >
                      Preview
                    </el-button>
                  </div>
                  <div class="doc-content">{{ doc.text }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="input-area">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="Type your question... (Press Ctrl+Enter to send)"
            @keyup.enter.ctrl="sendMessage"
            :disabled="loading"
          />
          <el-button 
            type="primary" 
            @click="sendMessage" 
            :loading="loading"
            :disabled="!inputMessage.trim() || loading"
          >
            Send
          </el-button>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="previewDialogVisible"
      title="Document Preview"
      width="50%"
    >
      <div class="preview-content">
        <h3>{{ previewDocument.title }}</h3>
        <div class="preview-text">{{ previewDocument.text }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { formatDistanceToNow } from 'date-fns'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const previewDialogVisible = ref(false)
const previewDocument = ref({})

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatTime = (timestamp) => {
  return formatDistanceToNow(new Date(timestamp), { addSuffix: true })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = {
    role: 'user',
    content: inputMessage.value,
    timestamp: new Date().toISOString()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const response = await axios.post('/api/v1/chat/message', {
      message: userMessage.content
    })

    const assistantMessage = {
      role: 'assistant',
      content: response.data.response,
      relevantDocs: response.data.relevantDocs,
      timestamp: new Date().toISOString()
    }
    messages.value.push(assistantMessage)
  } catch (error) {
    let errorMessage = 'Failed to get response'
    try {
      const errorResponse = error.response?.data
      errorMessage = errorResponse?.message || errorResponse?.error || errorMessage
    } catch (e) {
      console.error('Error parsing error response:', e)
    }
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const clearHistory = () => {
  messages.value = []
  ElMessage.success('Chat history cleared')
}

const previewDocument = (doc) => {
  previewDocument.value = doc
  previewDialogVisible.value = true
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.rag-chat {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  margin-bottom: 20px;
  background-color: white;
  border-radius: 8px;
}

.message-wrapper {
  margin-bottom: 20px;
}

.message {
  max-width: 80%;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
}

.message.user {
  background-color: #e6f7ff;
  margin-left: auto;
}

.message.assistant {
  background-color: #f0f0f0;
  margin-right: auto;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.9em;
  color: #606266;
}

.message-content {
  word-wrap: break-word;
}

.relevant-docs {
  margin-top: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.docs-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.doc-item {
  margin-bottom: 10px;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.doc-title {
  font-weight: bold;
  color: #303133;
}

.doc-content {
  color: #606266;
  font-size: 0.9em;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}

.preview-content {
  padding: 20px;
}

.preview-content h3 {
  margin-top: 0;
  color: #303133;
}

.preview-text {
  margin-top: 10px;
  color: #606266;
  line-height: 1.6;
}
</style> 