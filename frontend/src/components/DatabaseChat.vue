<template>
  <div class="database-chat">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>数据库对话</span>
          <el-select v-model="selectedDatabase" placeholder="选择数据库类型" style="width: 200px">
            <el-option label="MySQL" value="mysql" />
            <el-option label="PostgreSQL" value="postgresql" />
            <el-option label="MongoDB" value="mongodb" />
          </el-select>
        </div>
      </template>

      <div class="connection-form">
        <el-input v-model="connectionString" placeholder="输入数据库连接字符串" />
        <el-button type="primary" @click="testConnection">测试连接</el-button>
      </div>

      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" 
               :class="['message', message.type]">
            <div class="message-content">{{ message.content }}</div>
            <div v-if="message.queryResult" class="query-result">
              <el-table :data="message.queryResult" style="width: 100%">
                <el-table-column v-for="(value, key) in message.queryResult[0]" 
                               :key="key" :prop="key" :label="key" />
              </el-table>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="输入SQL查询语句..."
            @keyup.enter.native="sendMessage"
          />
          <el-button type="primary" @click="sendMessage" :loading="loading">
            执行查询
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
  name: 'DatabaseChat',
  setup() {
    const messages = ref([])
    const userInput = ref('')
    const loading = ref(false)
    const selectedDatabase = ref('mysql')
    const connectionString = ref('')
    const messagesContainer = ref(null)

    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    const testConnection = async () => {
      try {
        await axios.post('/api/v1/database/test-connection', {
          databaseType: selectedDatabase.value,
          connectionString: connectionString.value
        })
        ElMessage.success('连接成功')
      } catch (error) {
        ElMessage.error('连接失败')
      }
    }

    const sendMessage = async () => {
      if (!userInput.value.trim()) return

      const message = userInput.value
      messages.value.push({ type: 'user', content: message })
      userInput.value = ''
      loading.value = true

      try {
        const response = await axios.post('/api/v1/database/chat', {
          databaseType: selectedDatabase.value,
          connectionString: connectionString.value,
          query: message,
          userId: 'current-user-id' // TODO: 从用户认证获取
        })

        messages.value.push({
          type: 'assistant',
          content: response.data.response,
          queryResult: response.data.result
        })
      } catch (error) {
        ElMessage.error('查询执行失败')
      } finally {
        loading.value = false
        scrollToBottom()
      }
    }

    return {
      messages,
      userInput,
      loading,
      selectedDatabase,
      connectionString,
      messagesContainer,
      testConnection,
      sendMessage
    }
  }
}
</script>

<style scoped>
.database-chat {
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

.query-result {
  margin-top: 10px;
}

.connection-form {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.connection-form .el-input {
  flex: 1;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 