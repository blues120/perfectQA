<template>
  <el-container class="app-container">
    <el-main>
      <chat-interface />
    </el-main>
  </el-container>
</template>

<script setup>
import ChatInterface from './components/ChatInterface.vue'
</script>

<style>
.app-container {
  height: 100vh;
  padding: 20px;
}
</style> 