<template>
  <div style="background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h3>图片管理</h3>
      <el-button type="primary" @click="openDialog()">添加图片</el-button>
    </div>

    <el-table :data="images" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 80px; height: 60px;" fit="cover" />
          <span v-else style="color: var(--text-light);">无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确认删除？" @confirm="deleteItem(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑图片' : '添加图片'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="图片">
          <div style="display: flex; gap: 12px; align-items: flex-start;">
            <el-image v-if="form.imageUrl" :src="form.imageUrl" style="width: 120px; height: 80px; border-radius: 8px;" fit="cover" />
            <el-upload :show-file-list="false" :before-upload="uploadImage" accept="image/*">
              <el-button size="small">上传图片</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="选择分类">
            <el-option label="志愿服务" value="志愿服务" />
            <el-option label="课堂活动" value="课堂活动" />
            <el-option label="团队合影" value="团队合影" />
            <el-option label="实践过程" value="实践过程" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const images = ref([])
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const form = ref({ title: '', description: '', imageUrl: '', category: '志愿服务', sortOrder: 0 })

const load = async () => {
  const res = await adminApi.getImages()
  images.value = res.data || []
}

const openDialog = (row) => {
  if (row) {
    editId.value = row.id
    form.value = { ...row }
  } else {
    editId.value = null
    form.value = { title: '', description: '', imageUrl: '', category: '志愿服务', sortOrder: 0 }
  }
  dialogVisible.value = true
}

const uploadImage = async (file) => {
  const res = await adminApi.uploadFile(file, 'images')
  if (res.code === 200) form.value.imageUrl = res.data
  return false
}

const save = async () => {
  saving.value = true
  try {
    const data = { ...form.value }
    if (editId.value) data.id = editId.value
    await adminApi.saveImage(data)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const deleteItem = async (id) => {
  await adminApi.deleteImage(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
