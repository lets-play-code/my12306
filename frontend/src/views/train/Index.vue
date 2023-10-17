<template>
    <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="description" label="车次" width="180"></el-table-column>
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
import { showMessage } from "@/main";

const tableData = ref([]);

const handleClick = async (row: any) => {
    try {
        const res = await axios.post('/trains/'+row.id+'/tickets', {
          from: row.stops.at(0).id,
          to: row.stops.at(-1).id
        });
        showMessage("购票成功");
    } catch (e) {
        console.log(e);
    }
};

const fetch = async () => {
    try {
        tableData.value = await axios.get('/trains');
        tableData.value.forEach((item: any) => {
            item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
        });
    } catch (e) {
        console.log(e);
    }
};
fetch();

</script>