# 附件3：API接口文档

## 一、接口说明

- 基础地址：`http://localhost:8080/api`
- 请求格式：JSON（Content-Type: application/json）
- 文件上传：multipart/form-data
- 认证方式：JWT Token（Header: Authorization: Bearer {token}）

---

## 二、公开接口（无需认证）

### 1. 获取Banner列表
- 接口地址：`/api/public/banners`
- 请求方式：GET
- 请求参数：无
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "助苗·助梦·护苗",
      "subtitle": "暑期社会实践专题展示",
      "imageUrl": "/uploads/images/xxx.jpg",
      "description": "泉州职业技术大学八方书院暑期社会实践活动",
      "sortOrder": 1,
      "active": true,
      "createTime": "2024-07-01T10:00:00"
    }
  ]
}
```
- 接口描述：获取所有启用的Banner列表，按排序字段升序排列

### 2. 获取网站设置
- 接口地址：`/api/public/settings`
- 请求方式：GET
- 请求参数：无
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "site_name": "助苗·助梦·护苗——暑期社会实践专题展示平台",
    "site_intro": "泉州职业技术大学八方书院...",
    "team_name": "助苗·助梦·护苗实践队"
  }
}
```
- 接口描述：获取网站公开配置信息

### 3. 获取视频列表
- 接口地址：`/api/public/videos`
- 请求方式：GET
- 请求参数：无
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "《助苗·助梦·护苗实践纪实》",
      "description": "记录大学生志愿者...",
      "videoUrl": "/uploads/videos/xxx.mp4",
      "coverUrl": "/uploads/covers/xxx.jpg",
      "category": "实践记录",
      "publishTime": "2024-07-15T14:00:00",
      "createTime": "2024-07-15T14:00:00"
    }
  ]
}
```
- 接口描述：获取所有视频列表，按发布时间倒序

### 4. 获取视频详情
- 接口地址：`/api/public/videos/{id}`
- 请求方式：GET
- 请求参数：路径参数 id（视频ID）
- 响应参数：单个视频对象
- 接口描述：根据ID获取视频详细信息

### 5. 获取视频脚本
- 接口地址：`/api/public/videos/{id}/scripts`
- 请求方式：GET
- 请求参数：路径参数 id（视频ID）
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "第一幕：团队介绍",
      "description": "介绍实践队成员",
      "fileUrl": "/uploads/files/script1.pdf",
      "fileType": "pdf",
      "video": null,
      "createTime": "2024-07-15T14:00:00"
    }
  ]
}
```
- 接口描述：获取指定视频的所有脚本文件

### 6. 获取活动图片
- 接口地址：`/api/public/images`
- 请求方式：GET
- 请求参数：无
- 响应参数：图片列表数组
- 接口描述：获取所有活动图片，按排序和时间排列

### 7. 获取留言列表
- 接口地址：`/api/public/messages`
- 请求方式：GET
- 请求参数：无
- 响应参数：留言列表数组
- 接口描述：获取所有留言，按时间倒序

### 8. 获取资料列表
- 接口地址：`/api/public/materials`
- 请求方式：GET
- 请求参数：无
- 响应参数：资料列表数组
- 接口描述：获取所有实践资料

### 9. 发表留言
- 接口地址：`/api/public/messages`
- 请求方式：POST
- 请求参数：
```json
{
  "username": "小明",
  "title": "很有意义的活动",
  "content": "这个实践活动很有意义，希望更多孩子能够得到帮助。"
}
```
- 响应参数：返回创建的留言对象
- 接口描述：游客发表留言

---

## 三、用户接口（需用户Token认证）

### 10. 用户注册
- 接口地址：`/api/user/register`
- 请求方式：POST
- 请求参数：
```json
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com"
}
```
- 响应参数：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "nickname": "测试用户"
  }
}
```
- 接口描述：新用户注册，验证通过后创建用户并返回JWT令牌

### 11. 用户登录
- 接口地址：`/api/user/login`
- 请求方式：POST
- 请求参数：
```json
{
  "username": "testuser",
  "password": "123456"
}
```
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "nickname": "测试用户"
  }
}
```
- 接口描述：用户登录，验证账号密码后返回JWT令牌

### 12. 获取用户信息
- 接口地址：`/api/user/info`
- 请求方式：GET
- 请求头：Authorization: Bearer {token}
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com"
  }
}
```
- 接口描述：获取当前登录用户信息

### 13. 用户登出
- 接口地址：`/api/user/logout`
- 请求方式：POST
- 请求头：Authorization: Bearer {token}
- 响应参数：
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```
- 接口描述：用户退出登录（前端清除Token即可）

---

## 四、管理员接口（需管理员Token认证）

### 14. 管理员登录
- 接口地址：`/api/admin/login`
- 请求方式：POST
- 请求参数：
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin"
  }
}
```
- 接口描述：管理员登录，验证账号密码后返回JWT令牌

### 15. 获取视频列表（分页）
- 接口地址：`/api/admin/videos`
- 请求方式：GET
- 请求参数：
  - page: 页码（默认0）
  - size: 每页条数（默认10）
  - keyword: 搜索关键词（可选，按标题模糊搜索）
  - category: 分类筛选（可选）
- 响应参数：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 25,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 10
  }
}
```
- 接口描述：分页获取视频列表，支持多条件组合查询

### 16. 添加视频
- 接口地址：`/api/admin/videos`
- 请求方式：POST
- 请求参数：视频对象（title, description, videoUrl, coverUrl, category, publishTime）
- 响应参数：返回创建的视频对象
- 接口描述：管理员添加新视频

### 17. 修改视频
- 接口地址：`/api/admin/videos/{id}`
- 请求方式：PUT
- 请求参数：视频对象（部分字段即可）
- 响应参数：返回修改后的视频对象
- 接口描述：管理员修改视频信息

### 18. 删除视频
- 接口地址：`/api/admin/videos/{id}`
- 请求方式：DELETE
- 响应参数：
```json
{ "code": 200, "message": "删除成功", "data": null }
```
- 接口描述：管理员删除视频（同时删除关联脚本）

### 19. 添加视频脚本
- 接口地址：`/api/admin/videos/{id}/scripts`
- 请求方式：POST
- 请求参数：脚本对象（title, description, fileUrl, fileType）
- 响应参数：返回创建的脚本对象
- 接口描述：为指定视频添加脚本文件

### 20. 删除视频脚本
- 接口地址：`/api/admin/scripts/{id}`
- 请求方式：DELETE
- 响应参数：删除成功提示
- 接口描述：删除指定脚本文件

### 21. 获取留言列表（分页）
- 接口地址：`/api/admin/messages`
- 请求方式：GET
- 请求参数：page, size, keyword（可选）
- 响应参数：分页结果，包含content、totalElements、totalPages等
- 接口描述：分页获取留言列表，支持关键词搜索

### 22. 回复留言
- 接口地址：`/api/admin/messages/{id}/reply`
- 请求方式：PUT
- 请求参数：
```json
{ "reply": "感谢您的留言，我们会继续努力！" }
```
- 响应参数：返回修改后的留言对象
- 接口描述：管理员回复留言

### 23. 删除留言
- 接口地址：`/api/admin/messages/{id}`
- 请求方式：DELETE
- 响应参数：删除成功提示
- 接口描述：管理员删除留言

### 24. 文件上传
- 接口地址：`/api/admin/file/upload`
- 请求方式：POST
- 请求参数：multipart/form-data
  - file: 文件
  - type: 文件类型（videos/images/covers/files）
- 响应参数：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "/uploads/videos/xxx.mp4"
}
```
- 接口描述：上传文件，返回文件访问地址

### 25. Banner管理
- 获取列表：GET `/api/admin/banners`
- 添加：POST `/api/admin/banners`
- 修改：PUT `/api/admin/banners/{id}`
- 删除：DELETE `/api/admin/banners/{id}`

### 26. 图片管理
- 获取列表：GET `/api/admin/images`
- 添加：POST `/api/admin/images`
- 修改：PUT `/api/admin/images/{id}`
- 删除：DELETE `/api/admin/images/{id}`

### 27. 资料管理
- 获取列表：GET `/api/admin/materials`
- 添加：POST `/api/admin/materials`
- 修改：PUT `/api/admin/materials/{id}`
- 删除：DELETE `/api/admin/materials/{id}`

### 28. 网站设置
- 获取设置：GET `/api/admin/settings`
- 保存设置：POST `/api/admin/settings`
