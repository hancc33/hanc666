<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="stat in stats" :key="stat.label">
        <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
          <div style="display: flex; align-items: center; gap: 16px;">
            <div :style="{ width: '56px', height: '56px', borderRadius: '12px', background: stat.bg, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '28px' }">
              {{ stat.icon }}
            </div>
            <div>
              <div style="font-size: 28px; font-weight: 700; color: var(--text-dark);">{{ stat.count }}</div>
              <div style="font-size: 14px; color: var(--text-light);">{{ stat.label }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div style="margin-top: 24px; background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
      <h3 style="margin-bottom: 16px;">快速操作</h3>
      <div style="display: flex; flex-wrap: wrap; gap: 12px;">
        <el-button type="primary" @click="$router.push('/admin/videos')">管理视频</el-button>
        <el-button type="success" @click="$router.push('/admin/images')">管理图片</el-button>
        <el-button @click="$router.push('/admin/messages')">查看留言</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api'

const stats = ref([
  { label: '视频', count: 0, icon: '🎬', bg: '#fff0f0' },
  { label: '图片', count: 0, icon: '📷', bg: '#f0f9ff' },
  { label: '留言', count: 0, icon: '💬', bg: '#f0fff4' }
])

onMounted(async () => {
  try {
    const [v, i, m] = await Promise.all([
      adminApi.getVideos(),
      adminApi.getImages(),
      adminApi.getMessages()
    ])
    stats.value[0].count = (v.data?.content || v.data || []).length
    stats.value[1].count = (i.data || []).length
    stats.value[2].count = (m.data?.content || m.data || []).length
  } catch (e) {
    console.error(e)
  }
})
</script>
