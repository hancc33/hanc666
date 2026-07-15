import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/public/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/public/Home.vue') },
      { path: 'videos', name: 'Videos', component: () => import('../views/public/Videos.vue') },
      { path: 'videos/:id', name: 'VideoDetail', component: () => import('../views/public/VideoDetail.vue') },
      { path: 'gallery', name: 'Gallery', component: () => import('../views/public/Gallery.vue') },
      { path: 'messages', name: 'Messages', component: () => import('../views/public/Messages.vue') },
      { path: 'upload', name: 'Upload', component: () => import('../views/public/Upload.vue') },
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/Login.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'videos', name: 'AdminVideos', component: () => import('../views/admin/Videos.vue') },
      { path: 'images', name: 'AdminImages', component: () => import('../views/admin/Images.vue') },
      { path: 'messages', name: 'AdminMessages', component: () => import('../views/admin/Messages.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next({ name: 'AdminLogin' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
