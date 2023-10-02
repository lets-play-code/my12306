<template>
    <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="trainName" label="车次" width="180"></el-table-column>
    </el-table>
    <!-- <el-button type="primary">购票</el-button> -->
</template>
<script setup lang="ts">
import { ref } from 'vue';
import  axios from "@/api/index";
const tableData = ref([]);

const fetch = async () => {
    try {
        tableData.value = await axios.get('/tickets');
    } catch (e) {
        console.log(e);
    }
};
fetch();
</script>