# 助苗·助梦·护苗 — 暑期社会实践专题展示平台

泉州职业技术大学八方书院暑期社会实践成果展示系统。

## 技术栈

- **前端**: Vue3 + Vite + Element Plus
- **后端**: Spring Boot 3.4 + Spring Data JPA
- **数据库**: H2 (开发环境) / MySQL (生产环境)
- **认证**: JWT

## 快速启动

### 前提条件
- Java 17+
- Node.js 18+
- Maven 3.8+

### 启动方式

**方式一：使用启动脚本**
```
双击 start.bat
```

**方式二：手动启动**

启动后端：
```bash
cd backend
mvn spring-boot:run
```

启动前端：
```bash
cd frontend
npm install
npm run dev
```

### 访问地址
- 前台首页: http://localhost:5173
- 管理后台: http://localhost:5173/admin/login
- 后端API: http://localhost:8080

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

## 功能模块

### 前台展示
1. **首页** - Banner轮播、活动统计、视频/图片预览、留言墙
2. **实践视频** - 视频列表、分类筛选、视频播放
3. **视频详情** - 视频播放、脚本查看/下载
4. **活动相册** - 图片墙、分类筛选、大图预览
5. **实践成果** - 实践介绍、活动内容、创新成果、资料下载
6. **留言互动** - 游客留言、管理员回复

### 管理后台
1. **控制台** - 数据统计、快速操作
2. **Banner管理** - 首页轮播管理
3. **视频管理** - 视频CRUD、封面上传、脚本管理
4. **图片管理** - 活动图片CRUD
5. **资料管理** - 实践报告、成果材料上传
6. **留言管理** - 查看、回复、删除留言
7. **网站设置** - 网站名称、介绍、实践信息等

## 项目结构

```
筑梦护苗/
├── backend/                    # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/zhumengmiao/
│       ├── config/            # 配置类
│       ├── controller/        # REST控制器
│       ├── dto/               # 数据传输对象
│       ├── entity/            # 实体类
│       ├── repository/        # 数据访问层
│       └── security/          # JWT安全认证
├── frontend/                   # Vue3 前端
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   └── views/
│   │       ├── public/        # 前台页面
│   │       └── admin/         # 后台页面
│   ├── package.json
│   └── vite.config.js
├── start.bat                   # 一键启动脚本
└── README.md
```

## 默认数据

系统首次启动会自动初始化：
- 管理员账号: admin / admin123
- 网站基本设置
- 默认Banner
