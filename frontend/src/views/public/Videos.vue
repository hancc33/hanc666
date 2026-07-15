<template>
  <div class="section">
    <div class="section-title">
      <h2>实践视频</h2>
      <p>记录志愿者走进儿童群体，通过安全教育、成长陪伴等形式开展暑期社会实践的全过程</p>
      <div class="underline"></div>
    </div>

    <div style="text-align: center; margin-bottom: 24px; display: flex; justify-content: center; gap: 16px; align-items: center; flex-wrap: wrap;">
      <el-radio-group v-model="activeCategory">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="实践记录">实践记录</el-radio-button>
        <el-radio-button value="活动宣传">活动宣传</el-radio-button>
        <el-radio-button value="成果展示">成果展示</el-radio-button>
      </el-radio-group>
      <el-button type="primary" @click="uploadVisible = true">
        <el-icon><Upload /></el-icon> 分享我的实践视频
      </el-button>
    </div>

    <div class="card-grid" v-if="filteredVideos.length">
      <div class="card video-card" v-for="v in filteredVideos" :key="v.id" @click="$router.push(`/videos/${v.id}`)">
        <div class="card-cover">
          <img v-if="v.coverUrl" :src="v.coverUrl" :alt="v.title" />
          <div class="play-btn">▶</div>
        </div>
        <div class="card-body">
          <h3>{{ v.title }}</h3>
          <p>{{ v.description }}</p>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px;">
            <span class="card-tag">{{ v.category || '实践记录' }}</span>
            <span style="font-size: 12px; color: var(--text-light);">{{ formatDate(v.publishTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else style="text-align: center; padding: 60px 0; color: var(--text-light);">
      <p style="font-size: 15px;">暂无视频，快来分享你的实践故事吧！</p>
    </div>

    <!-- 上传视频对话框 -->
    <el-dialog v-model="uploadVisible" title="分享我的实践视频" width="560px" destroy-on-close>
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-position="top">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入视频标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="视频简介" prop="description">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" placeholder="描述视频内容、拍摄背景等..." maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="上传视频（MP4，最大200MB）" prop="videoFile">
          <div style="width: 100%;">
            <el-upload
              v-if="!uploadForm.videoUrl"
              drag
              :auto-upload="false"
              :show-file-list="false"
              accept="video/mp4"
              :on-change="handleVideoChange"
              style="width: 100%;"
            >
              <el-icon :size="40" style="color: var(--primary);"><VideoCamera /></el-icon>
              <div style="margin-top: 8px; color: var(--text-medium);">将视频拖拽到此处，或 <em style="color: var(--primary);">点击上传</em></div>
            </el-upload>
            <div v-else style="display: flex; align-items: center; gap: 12px; padding: 12px; background: rgba(245,166,35,0.06); border-radius: 8px;">
              <el-icon color="var(--primary)" :size="20"><CircleCheckFilled /></el-icon>
              <span style="flex: 1; font-size: 13px;">{{ uploadForm.videoFile?.name }}</span>
              <el-button type="danger" size="small" text @click="clearVideo">移除</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" @click="submitVideo" :loading="uploading">提交发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { publicApi } from '../../api'

const videos = ref([])
const activeCategory = ref('')
const uploadVisible = ref(false)
const uploading = ref(false)
const uploadFormRef = ref(null)

const uploadForm = ref({
  title: '',
  description: '',
  videoFile: null,
  videoUrl: ''
})

const uploadRules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入视频简介', trigger: 'blur' }]
}

const filteredVideos = computed(() => {
  if (!activeCategory.value) return videos.value
  return videos.value.filter(v => v.category === activeCategory.value)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 10)
}

const handleVideoChange = (file) => {
  const raw = file.raw
  if (raw.type !== 'video/mp4') { ElMessage.warning('仅支持MP4格式'); return }
  if (raw.size > 200 * 1024 * 1024) { ElMessage.warning('视频不能超过200MB'); return }
  uploadForm.value.videoFile = raw
  uploadForm.value.videoUrl = URL.createObjectURL(raw)
}

const clearVideo = () => {
  uploadForm.value.videoFile = null
  uploadForm.value.videoUrl = ''
}

const submitVideo = async () => {
  const valid = await uploadFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!uploadForm.value.videoFile) { ElMessage.warning('请先选择视频文件'); return }

  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', uploadForm.value.videoFile)
    const uploadRes = await axios.post('/api/public/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    if (uploadRes.data.code !== 200) { ElMessage.error(uploadRes.data.message || '上传失败'); return }

    const saveRes = await axios.post('/api/public/videos', {
      title: uploadForm.value.title,
      description: uploadForm.value.description,
      videoUrl: uploadRes.data.data,
      category: '实践记录',
      publishTime: new Date().toISOString().replace('Z', '').substring(0, 19)
    })
    if (saveRes.data.code === 200) {
      ElMessage.success('视频发布成功！')
      uploadVisible.value = false
      uploadForm.value = { title: '', description: '', videoFile: null, videoUrl: '' }
      loadVideos()
    } else {
      ElMessage.error('保存失败')
    }
  } catch (e) {
    ElMessage.error('发布失败')
  } finally {
    uploading.value = false
  }
}

const loadVideos = async () => {
  const res = await publicApi.getVideos()
  videos.value = res.data || []
}

onMounted(loadVideos)
</script>
