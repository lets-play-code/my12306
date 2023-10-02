import vue from "@vitejs/plugin-vue";

export default {
  plugins: [vue()],
  testMatch: ["**/tests/**/*.{spec,test}.{js,jsx,ts,tsx}", "**/__tests__/*.{js,jsx,ts,tsx}"],
  test: {
    environment: "happy-dom"
  }
};
