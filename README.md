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
├── src/scut/ui/
│   ├── MainApp.java          ← 主窗口 + CardLayout + 导航按钮
│   ├── ImportPanel.java      ← 导入面板（3种导入方式 + 学生列表）
│   ├── RollCallPanel.java    ← 点名面板（姓名展示 + 反馈按钮）
│   └── StatisticsPanel.java  ← 统计面板（JTable + 概览卡片）
├── classes/                  ← 编译后的 class 文件
├── lib/                      ← 第三方 jar（待放）
├── data/.gitkeep            ← 数据目录占位
├── README.md                ← 项目说明
├── step.md                  ← 开发步骤
├── .gitignore               ← Git 忽略规则
└── LICENSE                  ← MIT 许可证


```

## 核心功能

1. **导入学生** — 支持 Excel (.xlsx)、文本文件 (.txt)、或界面手动批量新建
2. **状态存储** — 记录每个学生的被点名总次数和答对次数，数据持久化保存
3. **智能点名** — 优先选择被点名次数少的同学；连续 N 次未答对时切换为优选答对多的同学
4. **统计展示** — 以表格/图表形式展示被点名次数、答对次数、答对率

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

## 技术栈

- Java Swing / JavaFX — GUI 界面
- Apache POI — Excel 文件读取
- SQLite + JDBC — 数据库存储
- 文件序列化 — 文件存储

## License

MIT License
