---
name: spring-boot-vue3-init
description: Initialize a Spring Boot 3.x + Vue3 + Element Plus full-stack project with JWT auth, file upload, and MySQL/H2 database
---

# Spring Boot + Vue3 Full-Stack Project Initialization

## When to Use
When starting a new full-stack web application project that requires:
- Spring Boot 3.x backend with REST APIs
- Vue3 + Vite frontend with Element Plus UI components
- JWT-based authentication for admin routes
- File upload capabilities
- Database integration (MySQL or H2)

## Prerequisites
- Java JDK 17+ (recommend JDK 21 for Spring Boot 3.x)
- Node.js v18+ and npm
- Maven (or Maven Wrapper)
- MySQL 8.0 (optional, can use H2 for development)

## Backend Setup Steps

### 1. Create Maven Project Structure
```
backend/
├── pom.xml
├── mvnw, mvnw.cmd (Maven Wrapper)
├── .mvn/wrapper/maven-wrapper.properties
└── src/main/
    ├── java/com/{project}/
    │   ├── {Project}Application.java
    │   ├── entity/
    │   ├── repository/
    │   ├── controller/
    │   ├── service/
    │   ├── config/
    │   ├── security/
    │   └── dto/
    └── resources/
        ├── application.yml
        └── data.sql (optional initial data)
```

### 2. pom.xml Dependencies
Include these key dependencies:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-redis` (optional, for caching)
- `spring-boot-starter-webflux` (for WebClient if calling external APIs)
- `io.jsonwebtoken:jjwt-api` + `jjwt-impl` + `jjwt-jackson` (0.12.x)
- `com.h2database:h2` (runtime scope)
- `com.mysql:mysql-connector-j` (runtime scope)
- `org.projectlombok:lombok` (provided scope)
- `io.springfox:springfox-boot-starter:3.0.0` or `springdoc-openapi-starter-webmvc-ui:2.3.0` (API docs)

### 3. Application Configuration (application.yml)
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:file:./data/{dbname}
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

# File upload
file:
  upload:
    dir: ./uploads/
    max-size: 10MB
    allowed-types: image/jpeg,image/png,image/gif,video/mp4,application/pdf

# JWT
jwt:
  secret: {generate-random-64-char-string}
  expiration: 86400000  # 24 hours
```

### 4. Core Components to Create

**Entity Base Class** (optional, for audit fields):
```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
```

**Security Configuration**:
- `SecurityConfig.java` - WebSecurityConfigurerAdapter or SecurityFilterChain bean
- `JwtUtil.java` - Token generation, validation, extraction
- `JwtAuthenticationFilter.java` - OncePerRequestFilter
- `CustomUserDetailsService.java` - Load user by username

**File Upload Controller**:
- `FileController.java` with MultipartFile handling
- Return relative URL paths under `/uploads/`
- Validate file type and size

**CORS Configuration**:
- Allow all origins in development
- Configure allowed methods and headers
- Handle preflight requests

**Data Initializer** (optional):
- `CommandLineRunner` bean to seed default admin user
- Hash passwords with BCryptPasswordEncoder

### 5. Default Admin Account
Always seed a default admin:
- Username: `admin`
- Password: `admin123` (BCrypt hashed)
- Role: `ADMIN`

## Frontend Setup Steps

### 1. Create Vue3 + Vite Project
```bash
npm create vite@latest frontend -- --template vue
cd frontend
npm install
npm install element-plus @element-plus/icons-vue
npm install vue-router@4 axios pinia
```

### 2. Project Structure
```
frontend/
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/index.js
│   ├── stores/ (Pinia)
│   ├── api/ (Axios instances)
│   ├── views/
│   │   ├── public/ (Public pages)
│   │   └── admin/ (Admin dashboard)
│   ├── components/
│   ├── layouts/
│   └── assets/
├── vite.config.js
├── index.html
└── package.json
```

### 3. Vite Configuration (vite.config.js)
```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### 4. Axios Instance (api/index.js)
```javascript
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// Request interceptor - add JWT token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor - handle 401
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/admin/login')
    }
    return Promise.reject(error)
  }
)

export default request
```

### 5. Router Setup (router/index.js)
```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // Public routes
  { path: '/', component: () => import('@/views/public/Home.vue') },
  
  // Admin routes
  { path: '/admin/login', component: () => import('@/views/admin/Login.vue') },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      // ... other admin pages
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for auth
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
```

### 6. Element Plus Admin Layout
Use `el-container` with:
- `el-aside` for sidebar menu
- `el-header` for top bar with user info
- `el-main` for content area

### 7. Key Admin Pages to Create
- Login page with JWT token handling
- Dashboard with statistics
- CRUD list pages for each entity
- File upload components (el-upload)
- Image/video preview components

## Common Patterns

### JWT Authentication Flow
1. Login: POST `/auth/login` → receive token
2. Store token in localStorage
3. Add token to Authorization header on all requests
4. Backend validates token in filter, sets SecurityContext
5. Logout: Clear token from localStorage

### File Upload Pattern
1. Frontend: `el-upload` with `action="/api/file/upload"`
2. Backend: `FileController` receives MultipartFile
3. Save to disk, return relative URL
4. Store URL in entity field

### CRUD Pattern
For each entity:
1. Entity class with JPA annotations
2. Repository interface extends JpaRepository
3. Service class with business logic
4. Controller with REST endpoints (GET list, GET by id, POST, PUT, DELETE)
5. Frontend list page with el-table
6. Frontend form dialog for create/edit

## Validation Checklist
- [ ] Backend starts without errors
- [ ] H2 console accessible at `/h2-console`
- [ ] Admin login works with default credentials
- [ ] JWT token generated and validated
- [ ] File upload works and files accessible
- [ ] CORS configured for frontend dev server
- [ ] Frontend connects to backend via proxy
- [ ] CRUD operations work for all entities
- [ ] Admin layout responsive

## Troubleshooting

### Common Issues
1. **CORS errors**: Ensure CorsConfig allows frontend origin
2. **403 Forbidden**: Check JWT token is sent in Authorization header
3. **File not found**: Verify upload directory exists and is writable
4. **Database connection**: Check H2 file path or MySQL credentials
5. **Lombok not working**: Ensure annotation processor configured in IDE

### PowerShell Specific
- Use `New-Item -ItemType Directory -Force` instead of `mkdir -p`
- For MySQL imports: `cmd /c "chcp 65001 >nul && mysql --default-character-set=utf8mb4 db < file.sql"`
- Chinese paths may break Maven - use ASCII paths or configure encoding