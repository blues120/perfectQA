// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: { enabled: true },
    modules: [
        '@nuxtjs/tailwindcss',
        '@nuxtjs/i18n',
    ],
    i18n: {
        locales: [
            { code: 'en', file: 'en.json' },
            { code: 'zh', file: 'zh.json' }
        ],
        defaultLocale: 'en',
        lazy: true,
        langDir: 'locales/',
        strategy: 'prefix_except_default'
    },
    runtimeConfig: {
        public: {
            apiBase: process.env.API_BASE || 'http://localhost:8080/api'
        }
    }
}) 