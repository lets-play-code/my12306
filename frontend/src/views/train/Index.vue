<template>
    <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="name" label="车次" width="180"></el-table-column>
        <el-table-column label="" width="180">
            <template #default="{ row }">
                <el-button type="primary" size="mini" @click="handleClick(row)">购票</el-button>
            </template>
        </el-table-column>
    </el-table>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import  axios from "@/api/index";
const tableData = ref([]);

const handleClick = async (row: any) => {
    try {
        const res = await axios.post('/trains/'+row.id+'/tickets', {});
    } catch (e) {
        console.log(e);
    }
};

const fetch = async () => {
    try {
        tableData.value = await axios.get('/trains');
    } catch (e) {
        console.log(e);
    }
};
fetch();

</script>