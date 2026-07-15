<template>
  <div class="section">
    <div class="section-title">
      <h2>留言互动</h2>
      <p>分享你对本次社会实践活动的看法与感受</p>
      <div class="underline"></div>
    </div>

    <!-- 毛玻璃留言表单 -->
    <div class="glass-form">
      <h3 style="margin-bottom: 20px; color: #3a3a3a; font-size: 24px; font-family: 'ZCOOL XiaoWei', 'STKaiti', 'KaiTi', 'SimSun', serif; text-align: center;">写下你想对小朋友说的话</h3>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="留言标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入留言标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="留言内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="写下你想说的话..." maxlength="500" show-word-limit />
        </el-form-item>
        <el-button type="primary" @click="submitMessage" :loading="submitting" style="width: 100%;">
          提交留言
        </el-button>
      </el-form>
    </div>

    <!-- 留言列表 -->
    <div class="message-list">
      <div class="message-item" v-for="msg in messages" :key="msg.id">
        <div class="msg-header">
          <span class="msg-user">{{ msg.username }}</span>
          <span class="msg-time">{{ formatDate(msg.createTime) }}</span>
        </div>
        <div class="msg-title">{{ msg.title }}</div>
        <div class="msg-content">{{ msg.content }}</div>
        <div class="message-reply" v-if="msg.replied && msg.reply">
          <div class="reply-label">管理员回复</div>
          <div>{{ msg.reply }}</div>
        </div>
      </div>

      <div v-if="!messages.length" style="text-align: center; padding: 40px 0; color: var(--text-light);">
        <p>暂无留言，来第一个留言吧！</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { publicApi } from '../../api'

const messages = ref([])
const formRef = ref(null)
const submitting = ref(false)

const form = ref({ title: '', content: '' })

const rules = {
  title: [{ required: true, message: '请输入留言标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}

const submitMessage = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await publicApi.createMessage({
      username: '匿名用户',
      title: form.value.title,
      content: form.value.content
    })
    ElMessage.success('留言提交成功！')
    form.value = { title: '', content: '' }
    loadMessages()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

const loadMessages = async () => {
  const res = await publicApi.getMessages()
  messages.value = res.data || []
}

onMounted(loadMessages)
</script>
