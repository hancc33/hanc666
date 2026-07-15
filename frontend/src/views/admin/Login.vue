<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #e74c3c 0%, #c0392b 50%, #f39c12 100%);">
    <div style="background: white; padding: 48px 40px; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); width: 400px; max-width: 90vw;">
      <div style="text-align: center; margin-bottom: 32px;">
        <div style="font-size: 48px; margin-bottom: 12px;">🌱</div>
        <h1 style="font-size: 22px; color: var(--text-dark);">后台管理系统</h1>
        <p style="color: var(--text-light); font-size: 14px; margin-top: 4px;">助苗·助梦·护苗</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading" size="large" style="width: 100%; height: 44px; font-size: 16px;">
          登 录
        </el-button>
      </el-form>

      <div style="text-align: center; margin-top: 20px;">
        <router-link to="/" style="color: var(--text-light); font-size: 13px;">← 返回前台首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await adminApi.login(form.value)
    if (res.code === 200) {
      localStorage.setItem('admin_token', res.data.token)
      ElMessage.success('登录成功')
      router.push('/admin')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (e) {
    // 无 response 多为网络/代理失败（后端未部署或 vercel.json 未指向真实域名）
    const msg = e.response?.data?.message
      || (e.response ? '登录失败，请检查账号密码' : '无法连接服务器，请确认后端已部署且 API 代理配置正确')
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>
