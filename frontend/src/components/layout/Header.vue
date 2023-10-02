<template>
    <header aria-label="Site Header" class="bg-white">
        <div
            class="mx-auto flex h-16 max-w-screen-xl items-center gap-8 px-4 sm:px-6 lg:px-8"
        >
            <a class="block text-teal-600" href="/">
                <span class="sr-only">Home</span>
                <Logo class="h-8"/>
            </a>

            <div class="flex flex-1 items-center justify-end md:justify-between">
                <nav aria-label="Site Nav" class="hidden md:block">
                    <ul class="flex items-center gap-6 text-sm">
                        <li>
                            <a class="text-gray-500 transition hover:text-gray-500/75" href="/">
                                About
                            </a>
                        </li>

                        <li>
                            <a class="text-gray-500 transition hover:text-gray-500/75" href="/product/index">
                                Catalog
                            </a>
                        </li>

                        <li v-if="currentUser">
                            <a class="text-gray-500 transition hover:text-gray-500/75" href="/product/importer">
                                Import
                            </a>
                        </li>

                        <li>
                            <a class="text-gray-500 transition hover:text-gray-500/75" href="/demo/index">
                                Demo
                            </a>
                        </li>
                    </ul>
                </nav>

                <div class="flex items-center gap-4">
                    <div v-if="!currentUser" class="sm:flex sm:gap-4">
                        <a class="block rounded-md bg-teal-600 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-teal-700" href="/login">
                            Login
                        </a>
                    </div>
                    <div v-if="currentUser" class="sm:flex sm:gap-4">
                        <span>{{ currentUser.fullName }}</span>
                    </div>


                    <button class="block rounded bg-gray-100 p-2.5 text-gray-600 transition hover:text-gray-600/75 md:hidden" >
                        <span class="sr-only">Toggle menu</span>
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            class="h-5 w-5"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                            stroke-width="2"
                        >
                            <path
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                d="M4 6h16M4 12h16M4 18h16"
                            />
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    </header>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import authenticationService from "../../services/authenticationService";
import Logo from "./Logo.vue";
import { CurrentUser } from "@/api/services/session";
import { useRoute } from "vue-router";

const route = useRoute();

const currentUser = ref<CurrentUser>();

const refreshUser = async () => {
    currentUser.value = await authenticationService.getCurrentUser();
}

onMounted( () => {
    refreshUser();
});


//TODO change to vuex store
watch(
    () => route.path,
    () => {
        console.log("route changed", route);
        refreshUser();
    }
);
</script>

<style scoped></style>

