# 基于有状态的课堂点名系统

## 项目简介

一个基于 Java 的课堂点名系统，支持从 Excel 或文本文件导入学生名单，根据历史点名数据智能选择学生，并提供统计展示。采用 GUI 界面，数据同时支持文件存储和 SQLite 数据库存储。

## 开发环境要求

| 项目 | 版本要求 |
|------|----------|
| JDK | 11 或以上 |
| IDE | IntelliJ IDEA / Eclipse / VS Code |
| 数据库 | SQLite（无需额外安装） |
| 依赖库 | Apache POI（读取 Excel）、SQLite JDBC |

## 项目目录结构

```
RollCaller/
│
├── src/scut/                          ← 所有源码，按职责分层
│   ├── entity/   Student.java         6.4K  实体层：学生数据模型
│   ├── dao/      StudentDAO.java      6.6K  数据层：DB + 文件双存储
│   ├── service/                       业务层：三大核心服务
│   │   ├── ImportService.java         7.1K  导入（Excel/文本/手动）
│   │   ├── RollCallService.java       5.8K  点名算法（正常+备用模式）
│   │   └── StatisticsService.java     4.8K  统计（排名/汇总/答对率）
│   ├── ui/                            表现层：Swing GUI
│   │   ├── MainApp.java               5.9K  主窗口 + 导航切换
│   │   ├── ImportPanel.java          12.6K  导入面板（对话框+表格）
│   │   ├── RollCallPanel.java         7.9K  点名面板（姓名+反馈）
│   │   └── StatisticsPanel.java      10.2K  统计面板（表格+柱状图）
│   └── util/                          工具层
│       ├── JDBCUtil.java              8.1K  SQLite 连接 + 通用 CRUD
│       └── FileUtil.java              8.7K  JSON 读写（手写解析器）
│
├── lib/                               第三方依赖（12个jar，共33MB）
│   ├── sqlite-jdbc-3.46.0.0.jar       SQLite 驱动
│   ├── slf4j-api / slf4j-nop          日志框架
│   ├── poi / poi-ooxml / poi-ooxml-lite  Excel 读写
│   ├── commons-io / commons-compress / commons-collections4  POI 依赖
│   ├── xmlbeans                       XML 解析
│   └── log4j-api / log4j-core         日志实现
│
├── data/                              运行时生成数据
│   ├── .gitkeep                       保持空目录被 git 追踪
│   └── students.json                  学生数据 JSON 备份
│
├── bin/run.bat                        Windows 一键启动脚本
├── classes/                           编译产物（gitignore）
├── doc/                               JavaDoc 文档（javadoc 生成）
├── img/                               图片资源（预留）
│
├── RollCaller.jar                     可执行 JAR 包（41KB）
├── MANIFEST.MF                        JAR 清单（入口类+类路径）
├── README.md                          项目说明文档
├── step.md                            开发步骤记录
├── LICENSE                            MIT 开源协议
└── .gitignore                         Git 忽略规则



```

## 核心功能

1. **导入学生** — 支持 Excel (.xlsx)、文本文件 (.txt)、或界面手动批量新建
2. **状态存储** — 记录每个学生的被点名总次数和答对次数，数据持久化保存
3. **智能点名** — 优先选择被点名次数少的同学；连续 N 次未答对时切换为优选答对多的同学
4. **统计展示** — 以表格/图表形式展示被点名次数、答对次数、答对率

## 功能演示

[▶ 点击观看功能演示视频](doc/功能演示.mp4)

<video src="doc/功能演示.mp4" controls width="600"></video>

## 数据库配置

使用 SQLite 数据库，无需额外安装或配置。首次运行时程序会自动创建数据库文件 `data/rollcaller.db`。

## 编译与运行

### 方式一：在 IDE 中运行

1. 将项目导入 IDE
2. 配置 lib 目录下的 jar 包为依赖
3. 运行主类 `scut.ui.MainApp`

### 方式二：打包为 JAR 运行

```bash
# 编译
javac -d classes -cp "lib/*" -sourcepath src src/scut/ui/MainApp.java

# 打包
jar cvfm RollCaller.jar MANIFEST.MF -C classes/ .

# 运行
java -jar RollCaller.jar
```

## 默认管理员账号

暂无登录功能，程序启动即可使用。

## 使用步骤

1. 启动程序，进入主界面
2. 点击「导入学生」按钮，选择 Excel 或文本文件导入学生名单，或手动添加
3. 点击「点名」按钮，系统根据算法智能选择学生
4. 点名后选择「答对」或「未答对」，系统自动更新统计数据
5. 点击「统计」按钮，查看所有学生的点名次数、答对次数、答对率

## 包结构

| 包名 | 说明 |
|------|------|
| `scut.entity` | 实体类（Student 等） |
| `scut.ui` | GUI 界面类 |
| `scut.service` | 业务逻辑（点名服务、统计服务、导入服务） |
| `scut.dao` | 数据访问层 |
| `scut.util` | 工具类（JDBCUtil、文件工具等） |

## Web 版部署（Railway 免费云服务器）

### 一键部署

1. 注册 [Railway](https://railway.app) 账号（GitHub 直接登录）
2. 点击 **New Project → Deploy from GitHub repo**
3. 选择 `RollCaller` 仓库的 `web` 分支
4. Railway 自动检测 Dockerfile → 构建 → 部署 → 分配公网 URL
5. 浏览器打开分配的公网 URL 即可使用

### 本地启动 Web 版

```bash
# 前提：项目根目录下有 apache-maven-3.9.6/
apache-maven-3.9.6/bin/mvn spring-boot:run
# 浏览器打开 http://localhost:8080
```

### 目录说明（Web 版）

| 目录/文件 | 说明 |
|-----------|------|
| `pom.xml` | Maven 依赖配置 |
| `Dockerfile` | 云部署 Docker 镜像定义 |
| `src/scut/RollCallerApplication.java` | Spring Boot 入口 |
| `src/scut/controller/` | Web 控制器（替代 Swing Panel） |
| `src/main/resources/templates/` | Thymeleaf HTML 页面 |

## 技术栈

- Java Swing — 桌面 GUI 界面
- Spring Boot + Thymeleaf — Web 界面
- Apache POI — Excel 文件读取
- SQLite + JDBC — 数据库存储
- 文件序列化（JSON）— 文件存储

## License

MIT License
