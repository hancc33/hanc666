# CLAUDE.md — 助苗·助梦·护苗

给 Claude / 后续会话的项目上下文。详细部署与联调见 **[docs/部署与联调说明.md](docs/部署与联调说明.md)**。

## 项目

- 全栈：Vue3 前端 + Spring Boot 后端（暑期社会实践展示）
- GitHub：https://github.com/hancc33/hanc666
- 前端 Vercel：https://hanx33.vercel.app （Root Directory = **`frontend`**）
- 默认管理员：`admin` / `admin123`（`DataInitializer`，勿擅自改）

## 关键约定

1. **Vercel 不能跑 Java**；后端需 Render / Railway / 自建等
2. 线上生效配置在 **`frontend/vercel.json`**（不是仓库根，除非改 Root Directory）
3. `vercel.json` rewrite 顺序：先 `/api`、`/uploads`，再 SPA `/(.*)` → `/index.html`
4. 前端 API：`baseURL: '/api'`（相对路径），生产靠 Vercel 代理到后端
5. 占位符 **`YOUR_BACKEND_URL`** 仍待替换为真实后端主机名 → 登录才能通
6. 最小改动优先；登录失败时先查后端是否在线 + 代理是否还是占位符

## 继续工作时先读

- [docs/部署与联调说明.md](docs/部署与联调说明.md) — 进度、配置、部署步骤、验证清单
- [README.md](README.md) — 本地启动
