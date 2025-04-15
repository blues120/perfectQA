<template>
  <div class="flex items-center gap-2">
    <input
      type="file"
      ref="fileInput"
      class="hidden"
      @change="handleFileChange"
      :accept="accept"
    />
    <button
      @click="triggerFileInput"
      class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-500"
    >
      {{ $t('file.upload') }}
    </button>
    <div v-if="uploadedFile" class="flex items-center gap-2">
      <span class="text-sm text-gray-600">{{ uploadedFile.originalFilename }}</span>
      <button
        @click="removeFile"
        class="text-red-500 hover:text-red-700"
      >
        ×
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  accept: {
    type: String,
    default: '*/*'
  },
  sessionId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['uploaded', 'removed'])

const fileInput = ref(null)
const uploadedFile = ref(null)

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)
  formData.append('sessionId', props.sessionId)

  try {
    const response = await fetch('/api/files/upload', {
      method: 'POST',
      body: formData
    })
    uploadedFile.value = await response.json()
    emit('uploaded', uploadedFile.value)
  } catch (error) {
    console.error('Error uploading file:', error)
  }
}

const removeFile = async () => {
  if (uploadedFile.value) {
    try {
      await fetch(`/api/files/${uploadedFile.value.storedFilename}`, {
        method: 'DELETE'
      })
      uploadedFile.value = null
      emit('removed')
    } catch (error) {
      console.error('Error removing file:', error)
    }
  }
}
</script> 