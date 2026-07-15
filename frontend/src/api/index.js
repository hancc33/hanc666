import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      if (!window.location.pathname.startsWith('/admin/login')) {
        window.location.href = '/admin/login'
      }
    }
    return Promise.reject(error)
  }
)

export const publicApi = {
  getVideos: () => api.get('/public/videos'),
  getVideo: (id) => api.get(`/public/videos/${id}`),
  getVideoScripts: (id) => api.get(`/public/videos/${id}/scripts`),
  getImages: () => api.get('/public/images'),
  getMessages: () => api.get('/public/messages'),
  createMessage: (data) => api.post('/public/messages', data),
}

export const adminApi = {
  login: (data) => api.post('/admin/login', data),
  getInfo: () => api.get('/admin/info'),
  getVideos: (params = {}) => api.get('/admin/videos', { params }),
  saveVideo: (data) => data.id ? api.put(`/admin/videos/${data.id}`, data) : api.post('/admin/videos', data),
  deleteVideo: (id) => api.delete(`/admin/videos/${id}`),
  saveScript: (videoId, data) => api.post(`/admin/videos/${videoId}/scripts`, data),
  deleteScript: (id) => api.delete(`/admin/scripts/${id}`),
  getImages: () => api.get('/admin/images'),
  saveImage: (data) => data.id ? api.put(`/admin/images/${data.id}`, data) : api.post('/admin/images', data),
  deleteImage: (id) => api.delete(`/admin/images/${id}`),
  getMessages: (params = {}) => api.get('/admin/messages', { params }),
  replyMessage: (id, reply) => api.put(`/admin/messages/${id}/reply`, { reply }),
  deleteMessage: (id) => api.delete(`/admin/messages/${id}`),
  uploadFile: (file, type = 'files') => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return api.post('/admin/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default api
