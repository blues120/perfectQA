<template>
  <div class="min-h-screen bg-gray-100">
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center">
          <h1 class="text-3xl font-bold text-gray-900">{{ $t('title') }}</h1>
          <div class="flex gap-4">
            <button
              @click="showModelManager = !showModelManager"
              class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
            >
              {{ $t('model.title') }}
            </button>
          </div>
        </div>
      </div>
    </header>
    <main>
      <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div class="px-4 py-6 sm:px-0">
          <div v-if="showModelManager" class="mb-6">
            <ModelManager />
          </div>
          <div class="border-4 border-dashed border-gray-200 rounded-lg h-[calc(100vh-12rem)]">
            <div class="p-4">
              <FileUpload
                :session-id="sessionId"
                @uploaded="handleFileUploaded"
                @removed="handleFileRemoved"
              />
            </div>
            <Chat :session-id="sessionId" />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Chat from '~/components/Chat.vue'
import FileUpload from '~/components/FileUpload.vue'
import ModelManager from '~/components/ModelManager.vue'

const showModelManager = ref(false)
const sessionId = ref(null)

const handleFileUploaded = (file) => {
  console.log('File uploaded:', file)
}

const handleFileRemoved = () => {
  console.log('File removed')
}
</script>

<i18n>
{
  "en": {
    "title": "Chat with Ollama",
    "model.title": "Model Manager"
  },
  "zh": {
    "title": "与Ollama聊天",
    "model.title": "模型管理器"
  }
}
</i18n> 