import { createRouter, createWebHistory } from 'vue-router'
import ModelPerformance from '../components/ModelPerformance.vue'
import RAGView from '@/views/RAGView.vue'
import AgentManagement from '../components/AgentManagement.vue'
import SearchEngineChat from '../components/SearchEngineChat.vue'
import DatabaseChat from '../components/DatabaseChat.vue'
import MultimodalChat from '../components/MultimodalChat.vue'
import ArxivChat from '../components/ArxivChat.vue'
import WolframChat from '../components/WolframChat.vue'
import TextToImage from '../components/TextToImage.vue'

const routes = [
    // ... existing code ...
    {
        path: '/model-performance',
        name: 'ModelPerformance',
        component: ModelPerformance
    },
    {
        path: '/rag',
        name: 'RAG',
        component: RAGView
    },
    {
        path: '/agents',
        name: 'AgentManagement',
        component: AgentManagement
    },
    {
        path: '/search',
        name: 'SearchEngineChat',
        component: SearchEngineChat
    },
    {
        path: '/database',
        name: 'DatabaseChat',
        component: DatabaseChat
    },
    {
        path: '/multimodal',
        name: 'MultimodalChat',
        component: MultimodalChat
    },
    {
        path: '/arxiv',
        name: 'ArxivChat',
        component: ArxivChat
    },
    {
        path: '/wolfram',
        name: 'WolframChat',
        component: WolframChat
    },
    {
        path: '/text-to-image',
        name: 'TextToImage',
        component: TextToImage
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router 