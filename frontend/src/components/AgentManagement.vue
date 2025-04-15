<template>
  <div class="agent-management">
    <el-card class="agent-card">
      <template #header>
        <div class="card-header">
          <span>智能体管理</span>
          <el-button type="primary" @click="showCreateDialog">创建智能体</el-button>
        </div>
      </template>
      
      <el-table :data="agents" style="width: 100%">
        <el-table-column prop="name" label="名称" width="180" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="capabilities" label="能力">
          <template #default="{ row }">
            <el-tag v-for="capability in row.capabilities" :key="capability" class="capability-tag">
              {{ capability }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="interactWithAgent(row)">交互</el-button>
            <el-button type="danger" size="small" @click="deleteAgent(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建智能体对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建智能体" width="500px">
      <el-form :model="newAgent" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="newAgent.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="newAgent.description" type="textarea" />
        </el-form-item>
        <el-form-item label="能力">
          <el-select
            v-model="newAgent.capabilities"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入能力"
          >
            <el-option
              v-for="item in capabilityOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createAgent">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 交互对话框 -->
    <el-dialog v-model="interactDialogVisible" :title="'与 ' + currentAgent?.name + ' 交互'" width="600px">
      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in chatMessages" :key="index" 
               :class="['message', message.type]">
            {{ message.content }}
          </div>
        </div>
        <div class="input-area">
          <el-input
            v-model="userMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            @keyup.enter.native="sendMessage"
          />
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

export default {
  name: 'AgentManagement',
  setup() {
    const agents = ref([])
    const createDialogVisible = ref(false)
    const interactDialogVisible = ref(false)
    const currentAgent = ref(null)
    const chatMessages = ref([])
    const userMessage = ref('')
    const messagesContainer = ref(null)

    const newAgent = ref({
      name: '',
      description: '',
      capabilities: []
    })

    const capabilityOptions = [
      '文档处理',
      '数据分析',
      '代码生成',
      '文本摘要',
      '问答系统',
      '翻译',
      '内容创作'
    ]

    const fetchAgents = async () => {
      try {
        const response = await axios.get('/api/v1/agents')
        agents.value = response.data
      } catch (error) {
        ElMessage.error('获取智能体列表失败')
      }
    }

    const showCreateDialog = () => {
      createDialogVisible.value = true
      newAgent.value = {
        name: '',
        description: '',
        capabilities: []
      }
    }

    const createAgent = async () => {
      try {
        await axios.post('/api/v1/agents', null, {
          params: newAgent.value
        })
        ElMessage.success('创建智能体成功')
        createDialogVisible.value = false
        fetchAgents()
      } catch (error) {
        ElMessage.error('创建智能体失败')
      }
    }

    const deleteAgent = async (agent) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除智能体 ${agent.name} 吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        await axios.delete(`/api/v1/agents/${agent.name}`)
        ElMessage.success('删除智能体成功')
        fetchAgents()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除智能体失败')
        }
      }
    }

    const interactWithAgent = (agent) => {
      currentAgent.value = agent
      interactDialogVisible.value = true
      chatMessages.value = []
    }

    const sendMessage = async () => {
      if (!userMessage.value.trim()) return

      const message = userMessage.value
      chatMessages.value.push({ type: 'user', content: message })
      userMessage.value = ''

      try {
        const response = await axios.post(
          `/api/v1/agents/${currentAgent.value.name}/interact`,
          message
        )
        chatMessages.value.push({ type: 'agent', content: response.data })
      } catch (error) {
        ElMessage.error('发送消息失败')
      }
    }

    onMounted(() => {
      fetchAgents()
    })

    return {
      agents,
      createDialogVisible,
      interactDialogVisible,
      currentAgent,
      chatMessages,
      userMessage,
      messagesContainer,
      newAgent,
      capabilityOptions,
      showCreateDialog,
      createAgent,
      deleteAgent,
      interactWithAgent,
      sendMessage
    }
  }
}
</script>

<style scoped>
.agent-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.capability-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
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
  margin-bottom: 10px;
  padding: 8px 12px;
  border-radius: 4px;
  max-width: 80%;
}

.message.user {
  background-color: #e6f7ff;
  margin-left: auto;
}

.message.agent {
  background-color: #f0f0f0;
  margin-right: auto;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 