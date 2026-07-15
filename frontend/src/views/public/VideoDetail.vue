<template>
  <div class="section" v-if="video">
    <div style="max-width: 900px; margin: 0 auto;">
      <div class="video-player">
        <video controls :src="video.videoUrl" preload="metadata"></video>
      </div>

      <div style="margin-top: 24px;">
        <h1 style="font-size: 26px; margin-bottom: 8px;">{{ video.title }}</h1>
        <div style="display: flex; gap: 16px; color: var(--text-light); font-size: 14px; margin-bottom: 16px;">
          <span class="card-tag">{{ video.category || '实践记录' }}</span>
          <span>{{ formatDate(video.publishTime) }}</span>
        </div>
        <p style="color: var(--text-medium); line-height: 1.8; font-size: 16px;">{{ video.description }}</p>
      </div>

      <!-- Scripts -->
      <div v-if="scripts.length" style="margin-top: 40px;">
        <h2 style="font-size: 22px; margin-bottom: 16px; color: var(--primary);">📄 视频脚本</h2>
        <div class="card" v-for="s in scripts" :key="s.id" style="margin-bottom: 12px; cursor: default;">
          <div class="card-body" style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <h3>{{ s.title }}</h3>
              <p>{{ s.description }}</p>
              <span class="card-tag">{{ s.fileType }}</span>
            </div>
            <div style="display: flex; gap: 8px;">
              <el-button type="primary" size="small" @click="viewScript(s)">查看</el-button>
              <el-button type="success" size="small" @click="downloadScript(s)">下载</el-button>
            </div>
          </div>
        </div>
      </div>

      <div style="text-align: center; margin-top: 40px;">
        <router-link to="/videos">
          <el-button>← 返回视频列表</el-button>
        </router-link>
      </div>
    </div>

    <!-- Script Viewer Dialog -->
    <el-dialog v-model="showScriptViewer" :title="currentScript?.title" width="70%">
      <iframe v-if="currentScript && currentScript.fileType === 'pdf'"
        :src="currentScript.fileUrl" style="width: 100%; height: 60vh; border: none;"></iframe>
      <pre v-else-if="currentScript" style="white-space: pre-wrap; font-size: 15px; line-height: 1.8; max-height: 60vh; overflow: auto; padding: 16px; background: #f5f5f5; border-radius: 8px;">{{ scriptContent }}</pre>
    </el-dialog>
  </div>
  <div v-else class="section" style="text-align: center;">
    <el-empty description="视频不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { publicApi } from '../../api'

const route = useRoute()
const video = ref(null)
const scripts = ref([])
const showScriptViewer = ref(false)
const currentScript = ref(null)
const scriptContent = ref('')

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}

const viewScript = async (script) => {
  currentScript.value = script
  if (script.fileType === 'txt') {
    try {
      const response = await fetch(script.fileUrl)
      scriptContent.value = await response.text()
    } catch (e) {
      scriptContent.value = '无法加载文件内容'
    }
  }
  showScriptViewer.value = true
}

const downloadScript = (script) => {
  const a = document.createElement('a')
  a.href = script.fileUrl
  a.download = script.title
  a.click()
}

onMounted(async () => {
  const id = route.params.id
  try {
    const [vRes, sRes] = await Promise.all([
      publicApi.getVideo(id),
      publicApi.getVideoScripts(id)
    ])
    video.value = vRes.data
    scripts.value = sRes.data || []
  } catch (e) {
    console.error(e)
  }
})
</script>
