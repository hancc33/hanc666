---
name: windows-dev-environment
description: Windows-specific workarounds for PowerShell encoding, Chinese paths, MySQL imports, Java/Maven, and Node.js development
---

# Windows Development Environment Workarounds

## When to Use
When working on Windows with:
- PowerShell as the shell
- Chinese characters in file paths or database content
- MySQL database operations
- Java/Maven projects
- Node.js/Vue frontend development

## PowerShell Encoding Issues

### Problem
PowerShell defaults to GBK encoding on Windows, which corrupts Chinese characters when:
- Importing SQL files with Chinese text
- Running Java programs that output Chinese
- Passing Chinese strings to command-line tools

### Solutions

#### 1. MySQL Import with Chinese Characters
**Bad** (corrupts Chinese):
```powershell
Get-Content data.sql | mysql -u root -proot dbname
```

**Good** (preserves Chinese):
```powershell
cmd /c "chcp 65001 >nul && mysql --default-character-set=utf8mb4 -u root -proot dbname < data.sql"
```

**Alternative** (using Python):
```python
import pymysql
connection = pymysql.connect(host='localhost', user='root', password='root', database='dbname', charset='utf8mb4')
with open('data.sql', 'r', encoding='utf-8') as f:
    sql = f.read()
cursor = connection.cursor()
cursor.execute(sql)
connection.commit()
```

#### 2. Java Source Code with Chinese
**Best Practice**: Use Unicode escape sequences in Java source code:
```java
String message = "\u767b\u5f55\u6210\u529f";  // 登录成功
```

**If using Chinese directly**:
1. Save source file as UTF-8
2. Compile with: `javac -encoding UTF-8 File.java`
3. Run with: `java -Dfile.encoding=UTF-8 File`

**For Maven projects**:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

#### 3. PowerShell Script with Chinese
```powershell
# Set console to UTF-8
chcp 65001

# Or use Start-Process with UTF-8 encoding
Start-Process -FilePath "mysql" -ArgumentList "-u root -proot --default-character-set=utf8mb4 dbname" -RedirectStandardInput "data.sql"
```

## Chinese Path Issues

### Problem
Java/Maven tools may fail with Chinese characters in project paths:
- `ClassNotFoundException` with Maven Wrapper
- Build failures with unclear error messages

### Solutions

#### 1. Use ASCII Paths (Recommended)
Move projects to ASCII-only paths:
- `C:\projects\myapp\` instead of `C:\用户\我的项目\myapp\`

#### 2. Configure Maven Encoding
In `pom.xml`:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

#### 3. Set Environment Variables
```powershell
$env:MAVEN_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
```

#### 4. Use IDEA
IntelliJ IDEA handles Chinese paths natively. Use IDEA for builds when possible.

## MySQL Operations

### Connection String
```
jdbc:mysql://localhost:3306/dbname?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia%2FShanghai
```

**Important**: 
- Use `UTF-8` (not `utf8mb4`) in JDBC `characterEncoding` parameter
- `utf8mb4` in JDBC causes `UnsupportedEncodingException`
- `utf8mb4` support comes from database/table charset, not JDBC parameter

### Common Commands
```sql
-- Check database charset
SHOW CREATE DATABASE dbname;

-- Check table charset
SHOW CREATE TABLE tablename;

-- Set database charset
ALTER DATABASE dbname CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Set table charset
ALTER TABLE tablename CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Java/Maven Environment

### JDK Version Management
**Problem**: Multiple JDK versions installed, wrong version used.

**Solution**: Set `JAVA_HOME` explicitly:
```powershell
# For Spring Boot 3.x (requires Java 17+)
$env:JAVA_HOME="C:\Users\Lenovo\.jdks\ms-21.0.11"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

# For legacy projects (Java 8)
$env:JAVA_HOME="C:\Users\Lenovo\.jdks\ms-1.8.0_xxx"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

### Maven Not in PATH
Use Maven Wrapper (`mvnw`) or set PATH manually:
```powershell
$env:Path="C:\Users\Lenovo\maven\apache-maven-3.9.7\bin;$env:Path"
```

### Build Failures
1. **File lock**: Stop running Spring Boot app before `mvn package`
2. **Chinese paths**: Use ASCII paths or IDEA
3. **JDK version mismatch**: Ensure `JAVA_HOME` matches `pom.xml` java.version

## Node.js/Vue Development

### Starting Dev Servers
**Problem**: Bash tool kills background processes on timeout.

**Solution**: Use `Start-Process` for long-running servers:
```powershell
Start-Process cmd -ArgumentList '/c', 'cd /d "C:\path\to\frontend" & npm run dev' -WindowStyle Normal
```

### Port Conflicts
Check if port is in use:
```powershell
netstat -ano | findstr :5173
taskkill /PID <process_id> /F
```

### npm Install Failures
```powershell
# Clear npm cache
npm cache clean --force

# Use Chinese mirror
npm config set registry https://registry.npmmirror.com
```

## File Upload Testing

### Problem
PowerShell multipart file upload often fails with Spring Boot.

### Solutions

#### 1. Use curl.exe (not PowerShell alias)
```powershell
curl.exe -X POST http://localhost:8080/api/file/upload -F "file=@C:\path\to\image.jpg"
```

#### 2. Use Hoppscotch (Browser-based)
Visit https://hoppscotch.io for API testing without installation.

#### 3. Use Postman Desktop
Download Postman desktop app (web version cannot access localhost).

## Edge Browser Screenshots

### Full Page Screenshot
```powershell
# Old mode (truncates at ~860px)
& "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --headless --screenshot="output.png" --window-size=900,2000 "http://localhost:5173"

# New mode (captures full page)
& "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --headless=new --screenshot="output.png" --window-size=900,16000 "http://localhost:5173"
```

**Note**: Must serve files via HTTP, not `file://` protocol.

## Git Operations

### Chinese Commit Messages
```powershell
git commit -m "修复登录页面中文显示问题"
```

Works fine in modern Git versions. If issues occur:
```powershell
git config --global core.quotepath false
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```

## Quick Reference Table

| Problem | Solution |
|---------|----------|
| MySQL Chinese import | `cmd /c "chcp 65001 >nul && mysql --default-character-set=utf8mb4 ..."` |
| Java Chinese source | Use `\uXXXX` escapes or `-encoding UTF-8` |
| Maven Chinese path | Use ASCII paths or IDEA |
| Vue dev server killed | Use `Start-Process cmd` |
| File upload fails | Use `curl.exe` or Hoppscotch |
| JDK version wrong | Set `$env:JAVA_HOME` explicitly |
| PowerShell encoding | `chcp 65001` at start of script |