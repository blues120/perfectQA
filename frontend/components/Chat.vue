<template>
  <div class="flex flex-col h-full">
    <div class="flex-1 overflow-y-auto p-4 space-y-4">
      <div v-if="!isConnected" class="text-center text-gray-500">
        {{ $t('chat.connecting') }}
      </div>
      <div v-else-if="messages.length === 0" class="text-center text-gray-500">
        {{ $t('chat.start') }}
      </div>
      <div v-else v-for="(message, index) in messages" :key="index" class="mb-4">
        <div :class="['flex', message.type === 'USER' ? 'justify-end' : 'justify-start']">
          <div :class="['max-w-[70%] rounded-lg p-3', message.type === 'USER' ? 'bg-blue-500 text-white' : 'bg-gray-200']">
            {{ message.content }}
          </div>
        </div>
      </div>
    </div>
    <div class="p-4 border-t bg-white">
      <div class="flex justify-between items-center mb-2">
        <button
          @click="clearHistory"
          class="text-sm text-gray-500 hover:text-gray-700"
        >
          {{ $t('chat.clear') }}
        </button>
        <div class="text-sm" :class="connectionStatusClass">
          {{ connectionStatusText }}
        </div>
      </div>
      <form @submit.prevent="sendMessage" class="flex gap-2">
        <input
          v-model="newMessage"
          type="text"
          class="flex-1 p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          :placeholder="$t('chat.placeholder')"
          :disabled="!isConnected"
        />
        <button
          type="submit"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
          :disabled="!isConnected || !newMessage.trim()"
        >
          {{ $t('chat.send') }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const messages = ref([])
const newMessage = ref('')
const stompClient = ref(null)
const sessionId = ref(null)
const isConnected = ref(false)
const connectionError = ref(null)

const connectionStatusText = computed(() => {
  if (connectionError.value) return $t('chat.error')
  if (isConnected.value) return $t('chat.connected')
  return $t('chat.connecting')
})

const connectionStatusClass = computed(() => {
  if (connectionError.value) return 'text-red-500'
  if (isConnected.value) return 'text-green-500'
  return 'text-yellow-500'
})

const connect = () => {
  const socket = new SockJS('/ws')
  stompClient.value = new Client({
    webSocketFactory: () => socket,
    onConnect: () => {
      console.log('Connected to WebSocket')
      isConnected.value = true
      connectionError.value = null
      stompClient.value.subscribe('/topic/messages', (message) => {
        const receivedMessage = JSON.parse(message.body)
        messages.value.push(receivedMessage)
      })
    },
    onDisconnect: () => {
      console.log('Disconnected from WebSocket')
      isConnected.value = false
    },
    onStompError: (frame) => {
      console.error('STOMP error:', frame)
      connectionError.value = frame
      isConnected.value = false
    }
  })
  stompClient.value.activate()
}

const sendMessage = () => {
  if (newMessage.value.trim() && stompClient.value && isConnected.value) {
    const message = {
      content: newMessage.value,
      sessionId: sessionId.value
    }
    stompClient.value.publish({
      destination: '/app/send',
      body: JSON.stringify(message)
    })
    newMessage.value = ''
  }
}

const clearHistory = async () => {
  try {
    await fetch('/api/chat/clear', { method: 'POST' })
    messages.value = []
    if (stompClient.value) {
      stompClient.value.deactivate()
      connect()
    }
  } catch (error) {
    console.error('Error clearing history:', error)
  }
}

onMounted(() => {
  connect()
})

onUnmounted(() => {
  if (stompClient.value) {
    stompClient.value.deactivate()
  }
})
</script> 