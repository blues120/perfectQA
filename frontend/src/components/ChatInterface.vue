<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <div class="settings-container">
        <el-form-item label="Select Model">
          <el-select v-model="selectedModel" @change="handleModelChange" placeholder="Select a model">
            <el-option
              v-for="model in models"
              :key="model"
              :label="model"
              :value="model"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Select Instruction">
          <el-select v-model="selectedInstruction" @change="handleInstructionChange" placeholder="Select an instruction">
            <el-option label="None" value="" />
            <el-option
              v-for="instruction in instructions"
              :key="instruction.id"
              :label="instruction.name"
              :value="instruction.id"
            />
          </el-select>
        </el-form-item>
      </div>

      <div class="document-upload">
        <el-upload
          class="upload-demo"
          action="/api/documents/upload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :before-upload="beforeUpload"
        >
          <el-button type="primary">Upload Document</el-button>
          <template #tip>
            <div class="el-upload__tip">
              Upload text documents for context-aware responses
            </div>
          </template>
        </el-upload>
      </div>

      <div class="messages-container">
        <div v-for="(message, index) in messages" :key="index" class="message-wrapper">
          <el-card
            :class="['message-card', message.type === 'user' ? 'user-message' : 'assistant-message']"
            shadow="hover"
          >
            <div class="message-content">{{ message.content }}</div>
          </el-card>
        </div>
        <div ref="messagesEnd"></div>
      </div>

      <div class="input-container">
        <el-input
          v-model="input"
          placeholder="Type your message..."
          @keyup.enter="handleSend"
        />
        <el-button type="primary" @click="handleSend">Send</el-button>
        <el-button @click="handleClear">Clear</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const messages = ref([])
const input = ref('')
const instructions = ref([])
const models = ref([])
const selectedInstruction = ref('')
const selectedModel = ref('')
const messagesEnd = ref(null)

onMounted(() => {
  fetchInstructions()
  fetchModels()
})

watch(messages, () => {
  scrollToBottom()
})

const fetchInstructions = async () => {
  try {
    const response = await axios.get('/api/instructions')
    instructions.value = response.data
  } catch (error) {
    console.error('Error fetching instructions:', error)
  }
}

const fetchModels = async () => {
  try {
    const response = await axios.get('/api/ollama/models')
    models.value = response.data
    if (models.value.length > 0) {
      selectedModel.value = models.value[0]
    }
  } catch (error) {
    console.error('Error fetching models:', error)
  }
}

const scrollToBottom = () => {
  messagesEnd.value?.scrollIntoView({ behavior: 'smooth' })
}

const handleSend = async () => {
  if (!input.value.trim()) return

  const userMessage = { content: input.value, type: 'user' }
  messages.value.push(userMessage)
  input.value = ''

  try {
    const response = await axios.post('/api/chat', input.value)
    const aiMessage = { content: response.data, type: 'assistant' }
    messages.value.push(aiMessage)
  } catch (error) {
    console.error('Error sending message:', error)
  }
}

const handleClear = async () => {
  try {
    await axios.post('/api/chat/clear')
    messages.value = []
  } catch (error) {
    console.error('Error clearing chat:', error)
  }
}

const handleInstructionChange = async (instructionId) => {
  selectedInstruction.value = instructionId
  try {
    await axios.post(`/api/chat/instruction/${instructionId}`)
    messages.value = []
  } catch (error) {
    console.error('Error setting instruction:', error)
  }
}

const handleModelChange = async (modelName) => {
  selectedModel.value = modelName
  try {
    await axios.post('/api/chat/model', { modelName })
    messages.value = []
  } catch (error) {
    console.error('Error setting model:', error)
  }
}

const handleUploadSuccess = () => {
  ElMessage.success('Document uploaded successfully')
}

const handleUploadError = () => {
  ElMessage.error('Failed to upload document')
}

const beforeUpload = (file) => {
  const isText = file.type === 'text/plain'
  if (!isText) {
    ElMessage.error('Only text files are allowed')
    return false
  }
  return true
}
</script>

<style scoped>
.chat-container {
  max-width: 800px;
  margin: 0 auto;
}

.chat-card {
  padding: 20px;
}

.settings-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.settings-container .el-form-item {
  flex: 1;
}

.document-upload {
  margin-bottom: 20px;
}

.messages-container {
  height: 400px;
  overflow-y: auto;
  margin: 20px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.message-wrapper {
  display: flex;
  margin-bottom: 10px;
}

.message-card {
  max-width: 70%;
  padding: 10px;
}

.user-message {
  margin-left: auto;
  background-color: #409eff;
  color: white;
}

.assistant-message {
  margin-right: auto;
  background-color: #f2f6fc;
}

.message-content {
  word-wrap: break-word;
}

.input-container {
  display: flex;
  gap: 10px;
}

.input-container .el-input {
  flex: 1;
}
</style> 