import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
// @ts-ignore
import path from 'path'

// https://vitejs.dev/config/

// @ts-ignore
const dirname = __dirname;
export default defineConfig({
    resolve:{
        alias:{
            '@' : path.resolve(__dirname, './src')
        },
    },
    plugins: [vue()],
    build: {
        outDir: path.resolve(dirname, './build/dist/public')
    },
    server: {
      port: 9090,
      hmr: {
        overlay: true
      },
      proxy: {
        '/api': {
          target: 'http://localhost:8080/',
          changeOrigin: true,
        },
        '/landing': {
          target: 'https://indigo-hyacinth-1v1fht.mysxl.cn/',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/.*/, '')
        }
      }
    }

})
