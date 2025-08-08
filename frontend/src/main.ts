import { createApp, provide, Ref, ref } from 'vue'
import '@/index.css'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'


const errorMessage = ref('');
export const showMessage = (message: string) => {
  // Reset first to ensure subsequent identical messages still trigger reactivity
  errorMessage.value = '';
  setTimeout(() => {
    errorMessage.value = message;
  }, 0);
};

const app = createApp(App)
app.provide('errorMessage', errorMessage);
app.use(router)
app.use(ElementPlus)
app.mount('#app')
