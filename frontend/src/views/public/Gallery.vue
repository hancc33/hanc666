<template>
  <div class="section">
    <div class="section-title">
      <h2>活动相册</h2>
      <p>记录志愿服务、儿童课堂、团队合影等珍贵瞬间</p>
      <div class="underline"></div>
    </div>

    <div style="text-align: center; margin-bottom: 24px; display: flex; justify-content: center; gap: 16px; align-items: center; flex-wrap: wrap;">
      <el-radio-group v-model="activeCategory">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="志愿服务">志愿服务</el-radio-button>
        <el-radio-button value="课堂活动">课堂活动</el-radio-button>
        <el-radio-button value="团队合影">团队合影</el-radio-button>
      </el-radio-group>
      <el-button type="primary" @click="uploadVisible = true">
        <el-icon><Upload /></el-icon> 上传团队日常照片
      </el-button>
    </div>

    <div class="gallery-grid" v-if="filteredImages.length">
      <div class="gallery-item" v-for="img in filteredImages" :key="img.id" @click="openPreview(img)">
        <img v-if="img.imageUrl" :src="img.imageUrl" :alt="img.title" loading="lazy" />
        <div class="gallery-overlay">
          <div style="font-weight: 600;">{{ img.title }}</div>
          <div style="font-size: 12px; opacity: 0.8;">{{ img.description }}</div>
        </div>
      </div>
    </div>

    <div v-else style="text-align: center; padding: 60px 0; color: var(--text-light);">
      <p style="font-size: 15px;">暂无活动图片，快来上传吧！</p>
    </div>

    <!-- 图片预览 -->
    <el-dialog v-model="previewVisible" :title="previewImage?.title" width="70%" destroy-on-close>
      <div style="text-align: center;">
        <img :src="previewImage?.imageUrl" :alt="previewImage?.title" style="max-width: 100%; border-radius: 8px;" />
        <p v-if="previewImage?.description" style="margin-top: 12px; color: var(--text-medium);">{{ previewImage.description }}</p>
      </div>
    </el-dialog>

    <!-- 上传照片对话框 -->
    <el-dialog v-model="uploadVisible" title="上传团队日常照片" width="560px" destroy-on-close>
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-position="top">
        <el-form-item label="照片标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入照片标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="照片描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="2" placeholder="描述这张照片..." maxlength="200" />
        </el-form-item>
        <el-form-item label="选择分类">
          <el-select v-model="uploadForm.category" placeholder="选择分类" style="width: 100%;">
            <el-option label="志愿服务" value="志愿服务" />
            <el-option label="课堂活动" value="课堂活动" />
            <el-option label="团队合影" value="团队合影" />
            <el-option label="实践过程" value="实践过程" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传照片（支持多张）" prop="files">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            list-type="picture-card"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept="image/*"
            multiple
            :limit="9"
          >
            <el-icon :size="24"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" @click="submitImages" :loading="uploading">提交上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { publicApi } from '../../api'

const images = ref([])
const activeCategory = ref('')
const previewVisible = ref(false)
const previewImage = ref(null)
const uploadVisible = ref(false)
const uploading = ref(false)
const uploadFormRef = ref(null)
const uploadRef = ref(null)
const fileList = ref([])

const uploadForm = ref({
  title: '',
  description: '',
  category: '团队合影'
})

const uploadRules = {
  title: [{ required: true, message: '请输入照片标题', trigger: 'blur' }]
}

const filteredImages = computed(() => {
  if (!activeCategory.value) return images.value
  return images.value.filter(i => i.category === activeCategory.value)
})

const openPreview = (img) => {
  previewImage.value = img
  previewVisible.value = true
}

const handleFileChange = (file) => {
  if (!file.raw.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    uploadRef.value?.handleRemove(file)
    return
  }
  fileList.value.push(file)
}

const handleFileRemove = (file) => {
  fileList.value = fileList.value.filter(f => f.uid !== file.uid)
}

const submitImages = async () => {
  const valid = await uploadFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!fileList.value.length) { ElMessage.warning('请至少选择一张照片'); return }

  uploading.value = true
  try {
    for (const item of fileList.value) {
      const fd = new FormData()
      fd.append('file', item.raw)
      const uploadRes = await axios.post('/api/public/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
      if (uploadRes.data.code !== 200) { ElMessage.error('图片上传失败'); continue }

      await axios.post('/api/public/images', {
        title: uploadForm.value.title,
        description: uploadForm.value.description,
        imageUrl: uploadRes.data.data,
        category: uploadForm.value.category
      })
    }
    ElMessage.success('照片上传成功！')
    uploadVisible.value = false
    uploadForm.value = { title: '', description: '', category: '团队合影' }
    fileList.value = []
    loadImages()
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const loadImages = async () => {
  const res = await publicApi.getImages()
  images.value = res.data || []
}

onMounted(loadImages)
</script>
