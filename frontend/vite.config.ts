import {defineConfig, Plugin, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import * as fs from 'fs'
// @ts-ignore
import path from 'path'

// https://vitejs.dev/config/

// @ts-ignore
const dirname = __dirname;

// 根据 vite 的 mode 动态加载对应的 .env 文件（vite --mode xxx 会设置 process.env.npm_lifecycle_event）
const viteMode = process.env.npm_lifecycle_event?.includes('test') ? 'test' : 'development';
const env = loadEnv(viteMode, process.cwd(), '');
const apiUrl = env.VITE_API_URL || 'http://localhost:8080';
const frontendPort = parseInt(env.VITE_SERVER_PORT || (viteMode === 'test' ? '9991' : '9990'));

export default defineConfig({
    resolve:{
        alias:{
            '@' : path.resolve(dirname, './src')
        },
    },
    plugins: [vue()],
    build: {
        outDir: path.resolve(dirname, './build/dist/public')
    },
    server: {
      port: frontendPort,
      proxy: {
        '/api': {
          target: apiUrl,
          changeOrigin: true,
        }
      }
    }
})
