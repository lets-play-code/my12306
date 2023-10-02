import { createApp, provide, Ref, ref } from 'vue'
import '@/index.css'
import router from './router'
import App from './App.vue'


const errorMessage = ref('');
export const showMessage = (message: string) => {
  errorMessage.value = message;
};

const app = createApp(App)
app.provide('errorMessage', errorMessage);
app.use(router)
app.mount('#app')
