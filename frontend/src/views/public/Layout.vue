<template>
  <div class="app-layout">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <img src="../../assets/logo.png" alt="logo" class="logo-img" />
        <span class="logo-text">筑梦护苗实践队</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="rgba(90, 66, 24, 0.65)"
        active-text-color="#d48b12"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/videos">
          <el-icon><VideoCamera /></el-icon>
          <span>实践视频</span>
        </el-menu-item>
        <el-menu-item index="/gallery">
          <el-icon><PictureFilled /></el-icon>
          <span>活动相册</span>
        </el-menu-item>
        <el-menu-item index="/messages">
          <el-icon><ChatDotRound /></el-icon>
          <span>留言互动</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <span>泉州职业技术大学八方书院</span>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <div class="main-area">
      <header class="top-bar">
        <span class="top-title">{{ currentTitle }}</span>
      </header>

      <main class="main-content">
        <router-view />
      </main>

      <footer class="glass-footer">
        <div class="footer-text">
          筑梦护苗实践队 &nbsp;|&nbsp; 泉州职业技术大学八方书院暑期社会实践
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => {
  if (route.path === '/') return '/'
  return '/' + route.path.split('/')[1]
})

const titleMap = {
  '/': '首页概览',
  '/videos': '实践视频',
  '/gallery': '活动相册',
  '/messages': '留言互动'
}

const currentTitle = computed(() => {
  if (route.path.startsWith('/videos/')) return '视频详情'
  return titleMap[route.path] || '筑梦护苗实践队'
})
</script>
