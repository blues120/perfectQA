<template>
  <div class="document-upload">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <span>Document Upload</span>
          <el-button type="text" @click="clearFiles">Clear All</el-button>
        </div>
      </template>

      <el-upload
        class="upload-area"
        drag
        action="/api/v1/chat/document"
        :headers="headers"
        :data="uploadData"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-progress="handleUploadProgress"
        :before-upload="beforeUpload"
        :file-list="fileList"
        :on-remove="handleRemove"
        multiple
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          Drop file here or <em>click to upload</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            Support PDF, Word, and text files, size limit 10MB
          </div>
        </template>
      </el-upload>

      <div class="upload-progress" v-if="uploadProgress > 0">
        <el-progress
          :percentage="uploadProgress"
          :status="uploadStatus"
        />
        <div class="progress-status">{{ processingStatus }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const fileList = ref([])
const uploadProgress = ref(0)
const uploadStatus = ref('')
const processingStatus = ref('')
const district = ref('')
const department = ref('')

const headers = computed(() => ({
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}))

const uploadData = computed(() => ({
  district: district.value,
  department: department.value
}))

const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  const isSupportedType = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'text/plain'
  ].includes(file.type)

  if (!isSupportedType) {
    ElMessage.error('Unsupported file type. Only PDF, Word, and text files are allowed')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('File size cannot exceed 10MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file) => {
  uploadProgress.value = 100
  uploadStatus.value = 'success'
  processingStatus.value = 'File uploaded successfully'
  ElMessage.success('File uploaded successfully')
}

const handleUploadError = (error, file) => {
  uploadProgress.value = 0
  uploadStatus.value = 'exception'
  let errorMessage = 'Upload failed'
  
  try {
    const errorResponse = JSON.parse(error.message)
    errorMessage = errorResponse.message || errorResponse.error || 'Upload failed'
  } catch (e) {
    console.error('Error parsing error response:', e)
  }
  
  processingStatus.value = errorMessage
  ElMessage.error(errorMessage)
}

const handleUploadProgress = (event, file) => {
  uploadProgress.value = Math.round(event.percent)
  uploadStatus.value = ''
  processingStatus.value = 'Uploading...'
}

const handleRemove = (file) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

const clearFiles = () => {
  fileList.value = []
  uploadProgress.value = 0
  uploadStatus.value = ''
  processingStatus.value = ''
  ElMessage.success('All files cleared')
}
</script>

<style scoped>
.document-upload {
  margin-bottom: 20px;
}

.upload-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-area {
  width: 100%;
}

.upload-progress {
  margin-top: 20px;
}

.progress-status {
  margin-top: 10px;
  text-align: center;
  color: #606266;
}

.el-upload__tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}
</style> 