<template>
  <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h3>留言管理</h3>
      <el-input v-model="keyword" placeholder="搜索留言..." style="width: 300px;" @keyup.enter="load" clearable @clear="load">
        <template #append>
          <el-button @click="load">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="messages" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户" width="120" />
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.replied ? 'success' : 'info'" size="small">
            {{ row.replied ? '已回复' : '未回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="120">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openReply(row)">回复</el-button>
          <el-popconfirm title="确认删除？" @confirm="deleteItem(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: center; margin-top: 20px;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="totalElements"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="load"
        @current-change="load"
      />
    </div>

    <el-dialog v-model="replyVisible" title="回复留言" width="500px">
      <div style="margin-bottom: 16px;">
        <div style="font-weight: 600; margin-bottom: 4px;">{{ currentMsg?.username }} - {{ currentMsg?.title }}</div>
        <div style="color: var(--text-medium); font-size: 14px; padding: 12px; background: #f5f5f5; border-radius: 8px;">{{ currentMsg?.content }}</div>
      </div>
      <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="输入回复内容..." />
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const messages = ref([])
const replyVisible = ref(false)
const currentMsg = ref(null)
const replyContent = ref('')
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)

const formatDate = (d) => d ? d.replace('T', ' ').substring(0, 16) : ''

const load = async () => {
  const params = {
    page: currentPage.value - 1,
    size: pageSize.value
  }
  if (keyword.value) params.keyword = keyword.value

  const res = await adminApi.getMessages(params)
  messages.value = res.data?.content || []
  totalElements.value = res.data?.totalElements || 0
}

const openReply = (msg) => {
  currentMsg.value = msg
  replyContent.value = msg.reply || ''
  replyVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  await adminApi.replyMessage(currentMsg.value.id, replyContent.value)
  ElMessage.success('回复成功')
  replyVisible.value = false
  load()
}

const deleteItem = async (id) => {
  await adminApi.deleteMessage(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
