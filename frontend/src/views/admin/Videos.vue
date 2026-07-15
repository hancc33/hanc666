<template>
  <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h3>视频管理</h3>
      <div style="display: flex; gap: 12px; align-items: center;">
        <el-input v-model="keyword" placeholder="搜索视频标题..." style="width: 200px;" @keyup.enter="load" clearable @clear="load" />
        <el-select v-model="searchCategory" placeholder="选择分类" clearable @change="load" style="width: 150px;">
          <el-option label="全部分类" value="" />
          <el-option label="实践记录" value="实践记录" />
          <el-option label="活动宣传" value="活动宣传" />
          <el-option label="成果展示" value="成果展示" />
          <el-option label="采访记录" value="采访记录" />
        </el-select>
        <el-button @click="load">搜索</el-button>
        <el-button type="primary" @click="openDialog()">添加视频</el-button>
      </div>
    </div>

    <el-table :data="videos" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="视频名称" min-width="200" />
      <el-table-column label="封面" width="120">
        <template #default="{ row }">
          <el-image v-if="row.coverUrl" :src="row.coverUrl" style="width: 80px; height: 50px;" fit="cover" />
          <span v-else style="color: var(--text-light);">无封面</span>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column label="发布时间" width="120">
        <template #default="{ row }">{{ formatDate(row.publishTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="success" @click="openScriptDialog(row)">脚本</el-button>
          <el-popconfirm title="确认删除？" @confirm="deleteItem(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: center; margin-top: 20px;">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="totalElements"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="load"
        @current-change="load"
      />
    </div>

    <!-- Video Dialog -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑视频' : '添加视频'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="视频名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="视频简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="视频文件">
          <div style="display: flex; gap: 12px; align-items: center;">
            <el-tag v-if="form.videoUrl" type="success">已上传</el-tag>
            <el-upload :show-file-list="false" :before-upload="uploadVideo" accept="video/mp4">
              <el-button size="small">上传视频(MP4)</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="封面图片">
          <div style="display: flex; gap: 12px; align-items: flex-start;">
            <el-image v-if="form.coverUrl" :src="form.coverUrl" style="width: 120px; height: 80px; border-radius: 8px;" fit="cover" />
            <el-upload :show-file-list="false" :before-upload="uploadCover" accept="image/*">
              <el-button size="small">上传封面</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option label="实践记录" value="实践记录" />
            <el-option label="活动宣传" value="活动宣传" />
            <el-option label="成果展示" value="成果展示" />
            <el-option label="采访记录" value="采访记录" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- Script Dialog -->
    <el-dialog v-model="scriptDialogVisible" :title="`脚本管理 - ${currentVideo?.title || ''}`" width="700px">
      <div style="margin-bottom: 16px;">
        <el-form inline>
          <el-form-item label="脚本标题"><el-input v-model="scriptForm.title" placeholder="输入脚本标题" /></el-form-item>
          <el-form-item label="脚本描述"><el-input v-model="scriptForm.description" placeholder="输入描述" /></el-form-item>
          <el-form-item label="文件">
            <el-upload :show-file-list="false" :before-upload="uploadScriptFile" accept=".doc,.docx,.pdf,.txt">
              <el-button size="small">上传文件</el-button>
            </el-upload>
            <el-tag v-if="scriptForm.fileUrl" size="small" style="margin-left: 8px;">已上传</el-tag>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addScript" :disabled="!scriptForm.title || !scriptForm.fileUrl">添加</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="scripts" stripe size="small">
        <el-table-column prop="title" label="脚本标题" />
        <el-table-column prop="fileType" label="类型" width="80" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-popconfirm title="确认删除？" @confirm="deleteScript(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const videos = ref([])
const dialogVisible = ref(false)
const scriptDialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const currentVideo = ref(null)
const scripts = ref([])
const keyword = ref('')
const searchCategory = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)

const form = ref({ title: '', description: '', videoUrl: '', coverUrl: '', category: '实践记录', publishTime: '' })
const scriptForm = ref({ title: '', description: '', fileUrl: '', fileType: '' })

const formatDate = (dateStr) => dateStr ? dateStr.replace('T', ' ').substring(0, 10) : ''

const load = async () => {
  const params = {
    page: currentPage.value - 1,
    size: pageSize.value
  }
  if (keyword.value) params.keyword = keyword.value
  if (searchCategory.value) params.category = searchCategory.value

  const res = await adminApi.getVideos(params)
  videos.value = res.data?.content || []
  totalElements.value = res.data?.totalElements || 0
}

const openDialog = (row) => {
  if (row) {
    editId.value = row.id
    form.value = { ...row }
  } else {
    editId.value = null
    form.value = { title: '', description: '', videoUrl: '', coverUrl: '', category: '实践记录', publishTime: '' }
  }
  dialogVisible.value = true
}

const openScriptDialog = async (video) => {
  currentVideo.value = video
  const sRes = await fetch(`/api/public/videos/${video.id}/scripts`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('admin_token')}` }
  }).then(r => r.json())
  scripts.value = sRes.data || []
  scriptForm.value = { title: '', description: '', fileUrl: '', fileType: '' }
  scriptDialogVisible.value = true
}

const uploadVideo = async (file) => {
  const res = await adminApi.uploadFile(file, 'videos')
  if (res.code === 200) form.value.videoUrl = res.data
  return false
}

const uploadCover = async (file) => {
  const res = await adminApi.uploadFile(file, 'covers')
  if (res.code === 200) form.value.coverUrl = res.data
  return false
}

const uploadScriptFile = async (file) => {
  const ext = file.name.split('.').pop().toLowerCase()
  scriptForm.value.fileType = ext
  const res = await adminApi.uploadFile(file, 'files')
  if (res.code === 200) scriptForm.value.fileUrl = res.data
  return false
}

const save = async () => {
  saving.value = true
  try {
    const data = { ...form.value }
    if (editId.value) data.id = editId.value
    await adminApi.saveVideo(data)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const addScript = async () => {
  await adminApi.saveScript(currentVideo.value.id, scriptForm.value)
  ElMessage.success('脚本添加成功')
  openScriptDialog(currentVideo.value)
}

const deleteScript = async (id) => {
  await adminApi.deleteScript(id)
  ElMessage.success('删除成功')
  openScriptDialog(currentVideo.value)
}

const deleteItem = async (id) => {
  await adminApi.deleteVideo(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
