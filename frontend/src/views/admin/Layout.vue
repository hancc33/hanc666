<template>
  <el-container style="min-height: 100vh;">
    <el-aside width="220px" style="background: #1a1a2e;">
      <div style="padding: 20px; text-align: center; border-bottom: 1px solid rgba(255,255,255,0.1);">
        <div style="font-size: 28px;">🌱</div>
        <div style="color: white; font-size: 15px; font-weight: 600; margin-top: 6px;">后台管理</div>
      </div>
      <el-menu :default-active="$route.path" router background-color="#1a1a2e" text-color="rgba(255,255,255,0.7)" active-text-color="#e74c3c" style="border-right: none;">
        <el-menu-item index="/admin">
          <el-icon><DataBoard /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/videos">
          <el-icon><VideoCamera /></el-icon>
          <span>视频管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/images">
          <el-icon><PictureFilled /></el-icon>
          <span>图片管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/messages">
          <el-icon><ChatDotRound /></el-icon>
          <span>留言管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background: white; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.08); padding: 0 24px;">
        <h3 style="color: var(--text-dark); font-size: 16px;">{{ pageTitle }}</h3>
        <div style="display: flex; align-items: center; gap: 16px;">
          <router-link to="/" target="_blank" style="color: var(--primary); font-size: 14px;">访问前台 →</router-link>
          <el-dropdown @command="handleCommand">
            <span style="cursor: pointer; color: var(--text-medium); display: flex; align-items: center; gap: 6px;">
              <el-avatar :size="32" style="background: var(--primary);">管</el-avatar>
              {{ username }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main style="background: #f5f7fa; padding: 24px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const username = ref(localStorage.getItem('admin_token') ? '管理员' : '')

const pageTitle = computed(() => {
  const titles = {
    '/admin': '控制台概览',
    '/admin/videos': '视频管理',
    '/admin/images': '图片管理',
    '/admin/messages': '留言管理'
  }
  return titles[route.path] || '后台管理'
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('admin_token')
    ElMessage.success('已退出登录')
    router.push('/admin/login')
  }
}
</script>
