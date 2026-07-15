<template>
  <div class="app-layout">
    <!-- 移动端顶栏 -->
    <header class="mobile-topbar">
      <button class="hamburger" @click="drawerVisible = true">
        <span></span>
        <span></span>
        <span></span>
      </button>
      <span class="mobile-title">{{ currentTitle }}</span>
      <div style="width: 36px;"></div>
    </header>

    <!-- 移动端抽屉导航 -->
    <el-drawer
      v-model="drawerVisible"
      direction="ltr"
      size="220px"
      :show-close="false"
      class="mobile-drawer"
    >
      <template #header>
        <div class="drawer-logo">
          <img src="../../assets/logo.png" alt="logo" class="logo-img" />
          <span class="logo-text">筑梦护苗实践队</span>
        </div>
      </template>
      <el-menu
        :default-active="activeMenu"
        router
        class="drawer-menu"
        @select="drawerVisible = false"
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
      <div class="drawer-footer">
        <span>泉州职业技术大学八方书院</span>
      </div>
    </el-drawer>

    <!-- 桌面端左侧导航栏 -->
    <aside class="sidebar desktop-sidebar">
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
      <header class="top-bar desktop-topbar">
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
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const drawerVisible = ref(false)

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

<style scoped>
/* ========== 移动端顶栏 ========== */
.mobile-topbar {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 52px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(245, 166, 35, 0.15);
  z-index: 200;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
}

.hamburger {
  width: 36px;
  height: 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
}

.hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: var(--text-dark);
  border-radius: 2px;
  transition: all 0.3s;
}

.mobile-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-dark);
}

/* ========== 抽屉样式覆盖 ========== */
.drawer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;
}

.drawer-menu {
  border-right: none !important;
}

.drawer-menu .el-menu-item {
  margin: 4px 8px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  font-size: 15px;
}

.drawer-menu .el-menu-item:hover {
  background: rgba(245, 166, 35, 0.1) !important;
}

.drawer-menu .el-menu-item.is-active {
  background: rgba(245, 166, 35, 0.15) !important;
  font-weight: 600;
}

.drawer-footer {
  padding: 16px;
  border-top: 1px solid rgba(245, 166, 35, 0.1);
  font-size: 12px;
  color: var(--text-light);
  text-align: center;
}

/* ========== 桌面端隐藏移动端元素 ========== */
.desktop-sidebar { display: flex; }
.desktop-topbar { display: none; }

@media (max-width: 768px) {
  .mobile-topbar { display: flex; }
  .desktop-sidebar { display: none !important; }
  .desktop-topbar { display: none !important; }

  .main-area {
    margin-left: 0 !important;
    padding-top: 52px;
  }
}
</style>
