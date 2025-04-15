<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-bold">{{ $t('model.title') }}</h2>
      <button
        @click="showAddModel = true"
        class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
      >
        {{ $t('model.add') }}
      </button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="model in models"
        :key="model.id"
        class="p-4 border rounded-lg"
        :class="{ 'border-blue-500': model.isDefault }"
      >
        <div class="flex justify-between items-start">
          <div>
            <h3 class="font-bold">{{ model.name }}</h3>
            <p class="text-sm text-gray-600">{{ model.description }}</p>
          </div>
          <div class="flex gap-2">
            <button
              @click="editModel(model)"
              class="text-blue-500 hover:text-blue-700"
            >
              {{ $t('model.edit') }}
            </button>
            <button
              @click="deleteModel(model.id)"
              class="text-red-500 hover:text-red-700"
              :disabled="model.isDefault"
            >
              {{ $t('model.delete') }}
            </button>
          </div>
        </div>
        <div class="mt-2 text-sm">
          <p>{{ $t('model.url') }}: {{ model.baseUrl }}</p>
          <p>{{ $t('model.modelName') }}: {{ model.modelName }}</p>
          <p v-if="model.isDefault" class="text-blue-500">
            {{ $t('model.default') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Add/Edit Model Dialog -->
    <div
      v-if="showAddModel || showEditModel"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
    >
      <div class="bg-white p-6 rounded-lg w-full max-w-md">
        <h3 class="text-lg font-bold mb-4">
          {{ showEditModel ? $t('model.edit') : $t('model.add') }}
        </h3>
        <form @submit.prevent="saveModel" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">
              {{ $t('model.name') }}
            </label>
            <input
              v-model="currentModel.name"
              type="text"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">
              {{ $t('model.url') }}
            </label>
            <input
              v-model="currentModel.baseUrl"
              type="text"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">
              {{ $t('model.modelName') }}
            </label>
            <input
              v-model="currentModel.modelName"
              type="text"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">
              {{ $t('model.description') }}
            </label>
            <textarea
              v-model="currentModel.description"
              class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              rows="3"
            />
          </div>
          <div class="flex items-center">
            <input
              v-model="currentModel.isDefault"
              type="checkbox"
              class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label class="ml-2 block text-sm text-gray-900">
              {{ $t('model.setDefault') }}
            </label>
          </div>
          <div class="flex justify-end gap-2">
            <button
              type="button"
              @click="closeDialog"
              class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
            >
              {{ $t('model.cancel') }}
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
            >
              {{ $t('model.save') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const models = ref([])
const showAddModel = ref(false)
const showEditModel = ref(false)
const currentModel = ref({
  name: '',
  baseUrl: '',
  modelName: '',
  description: '',
  isDefault: false
})

const loadModels = async () => {
  try {
    const response = await fetch('/api/models')
    models.value = await response.json()
  } catch (error) {
    console.error('Error loading models:', error)
  }
}

const editModel = (model) => {
  currentModel.value = { ...model }
  showEditModel.value = true
}

const deleteModel = async (id) => {
  try {
    await fetch(`/api/models/${id}`, { method: 'DELETE' })
    await loadModels()
  } catch (error) {
    console.error('Error deleting model:', error)
  }
}

const saveModel = async () => {
  try {
    const url = showEditModel.value
      ? `/api/models/${currentModel.value.id}`
      : '/api/models'
    const method = showEditModel.value ? 'PUT' : 'POST'
    
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(currentModel.value)
    })
    
    if (response.ok) {
      await loadModels()
      closeDialog()
    }
  } catch (error) {
    console.error('Error saving model:', error)
  }
}

const closeDialog = () => {
  showAddModel.value = false
  showEditModel.value = false
  currentModel.value = {
    name: '',
    baseUrl: '',
    modelName: '',
    description: '',
    isDefault: false
  }
}

onMounted(() => {
  loadModels()
})
</script> 