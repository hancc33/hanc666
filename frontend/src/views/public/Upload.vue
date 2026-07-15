<template>
  <div class="upload-page">
    <div class="section">
      <div class="section-title">
        <h2>发布视频</h2>
        <div class="underline"></div>
      </div>

      <div class="upload-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">

          <!-- 视频上传区域 -->
          <el-form-item label="上传视频" prop="videoUrl">
            <div class="video-upload-wrapper">
              <!-- 未上传时：显示上传区域 -->
              <el-upload
                v-if="!form.videoUrl"
                class="video-uploader"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept="video/mp4"
                :on-change="handleVideoChange"
              >
                <div class="upload-inner">
                  <el-icon :size="48" class="upload-icon"><VideoCamera /></el-icon>
                  <div class="upload-text">将视频拖拽到此处，或 <em>点击上传</em></div>
                  <div class="upload-tip">仅支持 MP4 格式，最大 200MB</div>
                </div>
              </el-upload>

              <!-- 已上传时：显示预览 -->
              <div v-else class="video-preview">
                <video :src="form.videoUrl" controls class="preview-player"></video>
                <div class="preview-info">
                  <el-icon color="var(--primary)"><CircleCheckFilled /></el-icon>
                  <span>视频上传成功</span>
                </div>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  class="remove-btn"
                  @click="removeVideo"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
          </el-form-item>

          <!-- 视频简介 -->
          <el-form-item label="视频简介" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="5"
              placeholder="请输入视频简介，描述视频内容、拍摄背景、实践过程等..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="submitting"
              :disabled="!form.videoUrl"
              @click="handleSubmit"
            >
              <el-icon v-if="!submitting"><Upload /></el-icon>
              {{ submitting ? '发布中...' : '提交发布' }}
            </el-button>
          </el-form-item>

        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// ========== 表单数据 ==========
const formRef = ref(null)
const submitting = ref(false)
const rawVideoFile = ref(null)

const form = reactive({
  videoUrl: '',      // 上传成功后后端返回的视频地址
  description: ''    // 用户输入的视频简介
})

const rules = {
  description: [
    { required: true, message: '请输入视频简介', trigger: 'blur' },
    { min: 5, message: '视频简介至少5个字符', trigger: 'blur' }
  ]
}

// ========== 第一步：选择视频文件 ==========
const handleVideoChange = (uploadFile) => {
  const file = uploadFile.raw

  if (file.type !== 'video/mp4') {
    ElMessage.warning('仅支持 MP4 格式视频')
    return
  }
  if (file.size > 200 * 1024 * 1024) {
    ElMessage.warning('视频大小不能超过 200MB')
    return
  }

  rawVideoFile.value = file
  form.videoUrl = URL.createObjectURL(file)
  ElMessage.success('视频已选择，请填写简介后提交')
}

// ========== 移除已选视频 ==========
const removeVideo = () => {
  rawVideoFile.value = null
  form.videoUrl = ''
}

// ========== 第二步+第三步：提交 ==========
const handleSubmit = async () => {
  // 校验表单
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (!rawVideoFile.value) {
    ElMessage.warning('请先选择视频文件')
    return
  }

  submitting.value = true

  try {
    // ---- 第一步：调用 /api/admin/file/upload 上传视频文件 ----
    const formData = new FormData()
    formData.append('file', rawVideoFile.value)

    const uploadRes = await axios.post('/api/public/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (uploadRes.data.code !== 200) {
      ElMessage.error(uploadRes.data.message || '视频上传失败')
      return
    }

    const videoUrl = uploadRes.data.data  // 后端返回的视频 URL

    // ---- 第二步：调用 /api/admin/videos 保存视频信息 ----
    const videoData = {
      videoUrl: videoUrl,
      description: form.description
    }

    const saveRes = await axios.post('/api/public/videos', videoData, {
      headers: { 'Content-Type': 'application/json' }
    })

    if (saveRes.data.code === 200) {
      ElMessage.success('视频发布成功！')
      // 重置表单
      form.videoUrl = ''
      form.description = ''
      rawVideoFile.value = null
    } else {
      ElMessage.error(saveRes.data.message || '视频信息保存失败')
    }

  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败，请检查网络后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.upload-page {
  min-height: calc(100vh - 56px - 200px);
  background: var(--bg-cream);
}

.section {
  max-width: 800px;
  margin: 0 auto;
  padding: 48px 24px;
}

.section-title {
  text-align: center;
  margin-bottom: 32px;
}

.section-title h2 {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-dark);
  margin-bottom: 8px;
}

.section-title .underline {
  width: 50px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary), var(--warm));
  border-radius: 2px;
  margin: 10px auto 0;
  position: relative;
}

.section-title .underline::after {
  content: '';
  position: absolute;
  top: -2.5px;
  left: 50%;
  transform: translateX(-50%);
  width: 8px;
  height: 8px;
  background: var(--primary);
  border-radius: 50%;
}

/* ========== 表单卡片 ========== */
.upload-card {
  background: var(--white);
  border-radius: var(--radius);
  padding: 36px 32px;
  box-shadow: var(--shadow);
}

/* ========== 上传区域 ========== */
.video-uploader {
  width: 100%;
}

.video-uploader :deep(.el-upload) {
  width: 100%;
}

.video-uploader :deep(.el-upload-dragger) {
  width: 100%;
  height: 220px;
  border-color: rgba(245, 166, 35, 0.25);
  border-radius: var(--radius);
  background: rgba(245, 166, 35, 0.03);
  transition: all 0.3s;
}

.video-uploader :deep(.el-upload-dragger:hover) {
  border-color: var(--primary);
  background: rgba(245, 166, 35, 0.06);
}

.upload-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.upload-icon {
  color: var(--primary);
  margin-bottom: 12px;
}

.upload-text {
  font-size: 15px;
  color: var(--text-medium);
}

.upload-text em {
  color: var(--primary);
  font-style: normal;
  font-weight: 600;
}

.upload-tip {
  font-size: 12px;
  color: var(--text-light);
  margin-top: 8px;
}

/* ========== 视频预览 ========== */
.video-preview {
  width: 100%;
  position: relative;
  border-radius: var(--radius);
  overflow: hidden;
  border: 1px solid rgba(245, 166, 35, 0.2);
}

.preview-player {
  width: 100%;
  max-height: 360px;
  display: block;
  background: #000;
}

.preview-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(245, 166, 35, 0.06);
  font-size: 14px;
  color: var(--primary-dark);
  font-weight: 500;
}

.remove-btn {
  position: absolute;
  top: 12px;
  right: 12px;
}

/* ========== 提交按钮 ========== */
.submit-btn {
  background: linear-gradient(135deg, var(--primary), var(--warm));
  border: none;
  padding: 12px 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(245, 166, 35, 0.35);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .upload-card {
    padding: 24px 18px;
  }

  .video-uploader :deep(.el-upload-dragger) {
    height: 180px;
  }
}
</style>
