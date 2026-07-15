---
name: vue3-admin-panel
description: Create Vue3 + Element Plus admin dashboard with JWT auth, CRUD pages, file upload, and responsive layout
---

# Vue3 + Element Plus Admin Panel

## When to Use
Building an admin dashboard for managing content, users, or data with:
- Vue3 + Vite
- Element Plus UI library
- JWT authentication
- CRUD operations (Create, Read, Update, Delete)
- File upload (images, videos, documents)

## Project Setup

### 1. Initialize Project
```bash
npm create vite@latest admin -- --template vue
cd admin
npm install
```

### 2. Install Dependencies
```bash
npm install element-plus @element-plus/icons-vue
npm install vue-router@4 axios pinia
npm install @vueuse/core  # optional, for utility functions
```

### 3. Vite Configuration
```javascript
// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## Project Structure

```
src/
├── main.js                 # App entry, Element Plus setup
├── App.vue                 # Root component
├── router/
│   └── index.js            # Route definitions
├── stores/
│   └── user.js             # Pinia store for auth state
├── api/
│   ├── index.js            # Axios instance with interceptors
│   ├── auth.js             # Login/logout API calls
│   └── [entity].js         # CRUD API calls per entity
├── views/
│   ├── admin/
│   │   ├── Login.vue       # Login page
│   │   ├── Layout.vue      # Admin layout with sidebar
│   │   ├── Dashboard.vue   # Dashboard with stats
│   │   └── [Entity]List.vue # CRUD list page
│   └── public/
│       └── Home.vue        # Public homepage (optional)
├── components/
│   ├── FileUpload.vue      # Reusable file upload component
│   └── ImagePreview.vue    # Image preview component
└── assets/
    └── styles/
        └── global.css      # Global styles
```

## Core Components

### 1. Main Entry (main.js)
```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// Register all Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: 'zh-cn' })  // optional Chinese locale

app.mount('#app')
```

### 2. Axios Instance (api/index.js)
```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0 && res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      router.push('/admin/login')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

### 3. Pinia Auth Store (stores/user.js)
```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  async function login(credentials) {
    const res = await loginApi(credentials)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    return res
  }

  async function logout() {
    try { await logoutApi() } catch (e) { /* ignore */ }
    token.value = ''
    localStorage.removeItem('token')
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, login, logout, isLoggedIn }
})
```

### 4. Router (router/index.js)
```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/admin/login',
    name: 'Login',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据概览', icon: 'DataAnalysis' }
      },
      {
        path: 'banners',
        name: 'BannerList',
        component: () => import('@/views/admin/BannerList.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      // Add more routes for each entity
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 管理后台` : '管理后台'
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
```

### 5. Admin Layout (views/admin/Layout.vue)
```vue
<template>
  <el-container class="admin-layout">
    <el-aside width="220px">
      <div class="logo">管理后台</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              管理员
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menuItems = [
  { path: '/admin/dashboard', title: '数据概览', icon: 'DataAnalysis' },
  { path: '/admin/banners', title: '轮播图管理', icon: 'Picture' },
  // Add more menu items
]

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await userStore.logout()
    router.push('/admin/login')
  }
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.el-aside { background: #001529; }
.logo { height: 60px; color: #fff; font-size: 18px; display: flex; align-items: center; justify-content: center; }
.el-header { display: flex; align-items: center; justify-content: flex-end; border-bottom: 1px solid #eee; }
.user-info { display: flex; align-items: center; gap: 5px; cursor: pointer; }
</style>
```

### 6. CRUD List Page Template
```vue
<template>
  <div class="page-container">
    <div class="page-header">
      <h2>实体管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增
      </el-button>
    </div>
    
    <!-- Search/Filter bar -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="searchForm.keyword" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- Data table -->
    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image :src="row.imageUrl" :preview-src-list="[row.imageUrl]" style="width: 80px; height: 60px;" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- Pagination -->
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchData"
      @current-change="fetchData"
    />
    
    <!-- Add/Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <!-- Form fields here -->
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getList, add, update, del } from '@/api/entity'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({})
const formRef = ref(null)
const searchForm = reactive({ keyword: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await getList({ page: page.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增'
  Object.keys(form).forEach(k => form[k] = '')
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  if (form.id) {
    await update(form)
  } else {
    await add(form)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  fetchData()
}

async function handleDelete(id) {
  await del(id)
  ElMessage.success('删除成功')
  fetchData()
}

function handleSearch() { page.value = 1; fetchData() }
function resetSearch() { searchForm.keyword = ''; handleSearch() }

onMounted(() => fetchData())
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
```

### 7. File Upload Component
```vue
<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
    >
      <el-image v-if="modelValue" :src="modelValue" style="width: 120px; height: 90px;" fit="cover" />
      <el-icon v-else class="upload-icon"><Plus /></el-icon>
    </el-upload>
    <div class="upload-tip">{{ tip }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: String, default: '' },
  tip: { type: String, default: '点击上传图片' },
  accept: { type: String, default: 'image/jpeg,image/png,image/gif' },
  maxSize: { type: Number, default: 5 }  // MB
})

const emit = defineEmits(['update:modelValue'])

const uploadUrl = '/api/file/upload'
const headers = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

function beforeUpload(file) {
  const isValidType = props.accept.split(',').includes(file.type)
  const isValidSize = file.size / 1024 / 1024 < props.maxSize
  
  if (!isValidType) {
    ElMessage.error('文件格式不支持')
    return false
  }
  if (!isValidSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }
  return true
}

function handleSuccess(response) {
  emit('update:modelValue', response.data.url)
}

function handleError() {
  ElMessage.error('上传失败')
}
</script>

<style scoped>
.file-upload { display: inline-block; }
.upload-icon { font-size: 28px; color: #909399; width: 120px; height: 90px; display: flex; align-items: center; justify-content: center; border: 1px dashed #dcdfe6; border-radius: 6px; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 5px; }
</style>
```

### 8. API Module Template (api/entity.js)
```javascript
import request from './index'

export function getList(params) {
  return request.get('/entity/list', { params })
}

export function getById(id) {
  return request.get(`/entity/${id}`)
}

export function add(data) {
  return request.post('/entity', data)
}

export function update(data) {
  return request.put('/entity', data)
}

export function del(id) {
  return request.delete(`/entity/${id}`)
}
```

## Common Patterns

### Image Display
```javascript
// Computed property for image URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/uploads/${path}`
}
```

### Form Validation Rules
```javascript
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}
```

### Dialog Form Reset
```javascript
function resetForm() {
  Object.keys(form).forEach(key => {
    form[key] = typeof form[key] === 'string' ? '' : null
  })
}
```

## Styling Tips

### Admin Layout Colors
```css
:root {
  --sidebar-bg: #001529;
  --header-bg: #ffffff;
  --primary-color: #409eff;
}
```

### Table Actions
```css
.el-button + .el-button { margin-left: 10px; }
```

### Responsive Design
```css
@media (max-width: 768px) {
  .el-aside { display: none; }
  .page-header { flex-direction: column; gap: 10px; }
}
```

## Checklist
- [ ] Vite proxy configured for API calls
- [ ] Axios interceptors handle JWT and 401 errors
- [ ] Router guards protect admin routes
- [ ] Element Plus icons registered globally
- [ ] File upload component handles size/type validation
- [ ] CRUD list has pagination, search, and action buttons
- [ ] Dialog forms have proper validation rules
- [ ] Layout responsive on mobile