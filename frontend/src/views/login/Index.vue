<template>
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-md w-full space-y-8">
            <div class="sm:mx-auto sm:w-full sm:max-w-sm">
                <Logo class="mx-auto h-16 w-auto text-gray-900"/>
                <h2 class="mt-10 text-center text-3xl font-extrabold text-gray-900">Sign in to your account</h2>
            </div>

            <form @submit.prevent="submitForm" class="mt-10 space-y-6">
                <div class="rounded-md shadow-sm -space-y-px">
                    <div>
                        <label for="email" class="sr-only">Email address</label>
                        <input v-model.trim="loginUser.username" id="email" name="email" type="email" autocomplete="email" required
                            class="appearance-none rounded-none relative block w-full px-3 py-2 border border-white-300 placeholder-white-500 text-white-900 rounded-t-md focus:outline-none focus:ring-teal-500 focus:border-teal-500 focus:z-10 sm:text-sm"
                            placeholder="Email address" />
                    </div>
                    <div>
                        <label for="password" class="sr-only">Password</label>
                        <input v-model.trim="loginUser.password" id="password" name="password" type="password"
                            autocomplete="current-password" required
                            class="appearance-none rounded-none relative block w-full px-3 py-2 border border-white-300 placeholder-white-500 text-white-900 rounded-t-md focus:outline-none focus:ring-teal-500 focus:border-teal-500 focus:z-10 sm:text-sm"
                            placeholder="Password" />
                    </div>
                </div>

                <!--
                <div class="flex items-center justify-between">
                    <div class="text-sm">
                        <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">Forgot your password?</a>
                    </div>
                </div>
                -->

                <div>
                    <button type="submit"
                        class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-teal-600 hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500">
                        Sign in
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>
  
<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import Logo from "@/components/layout/Logo.vue";
import { LoginUser } from "@/model/LoginUser";
import authentication from "@/services/authenticationService";
import { showMessage } from "@/main";

const loginUser = ref<LoginUser>({
    username: "",
    password: ""
})

const router = useRouter();
const submitForm = async () => {
    try {
        await authentication.login(loginUser.value);
        router.push('/product/index');
    } catch (error) {
        showMessage((error as Error).message);
    }
};
</script>

<style>
@import "../../index.css";
</style>