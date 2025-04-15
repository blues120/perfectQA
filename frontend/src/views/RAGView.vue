<template>
  <div class="rag-view">
    <el-container>
      <el-header>
        <h1>RAG Chat</h1>
      </el-header>
      
      <el-main>
        <el-row :gutter="20">
          <el-col :span="24">
            <DocumentUpload />
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <RAGChat />
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import DocumentUpload from '@/components/DocumentUpload.vue'
import RAGChat from '@/components/RAGChat.vue'
</script>

<style scoped>
.rag-view {
  height: 100vh;
  padding: 20px;
}

.el-header {
  background-color: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.el-main {
  padding: 0;
}

.el-row {
  margin-bottom: 20px;
}

h1 {
  margin: 0;
  color: #303133;
}
</style> 