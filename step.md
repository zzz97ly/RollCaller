[toc]
已更新并推送。下面是完整的开发步骤计划：

📋 课堂点名系统 — 分步开发计划
# 🔧 准备阶段
## 第 0 步：环境准备

下载配置 JDK 11+，设置 JAVA_HOME
下载 SQLite JDBC 驱动 jar 包，放入 lib/
下载 Apache POI jar 包，放入 lib/
在 IDE 中将 lib/ 下所有 jar 添加为项目依赖
# 🏗️ 第一阶段：GUI 框架
## 第 1 步：创建主界面框架（scut.ui.MainApp）

用 Swing 或 JavaFX 搭建主窗口（推荐 Swing 的 JFrame）
顶部放标题标签「课堂点名系统」
中间区域用 CardLayout 面板切换三个视图（导入、点名、统计）
底部放三个按钮：导入学生、点名、统计
点击按钮切换对应的视图面板
颜色搭配：建议浅蓝/浅灰背景，按钮用柔和的圆角色调
## 第 2 步：创建三个占位视图

ImportPanel — 导入界面（先放空白面板 + 按钮）
RollCallPanel — 点名界面（先放标签显示点名学生）
StatisticsPanel — 统计界面（先放 JTable 占位）
# 📦 第二阶段：实体类与数据层
## 第 3 步：创建学生实体类（scut.entity.Student）

属性：id, name, totalCalled (被点名总次数), correctCount (答对次数)
使用 Lombok 或手写 getter/setter
重写 toString() 方法
## 第 4 步：创建数据库工具类（scut.util.JDBCUtil）

使用 SQLite JDBC 连接数据库
连接 URL：jdbc:sqlite:data/rollcaller.db
提供 getConnection(), close(), executeUpdate(), executeQuery() 方法
首次运行时自动建表：

CREATE TABLE IF NOT EXISTS student (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  total_called INTEGER DEFAULT 0,
  correct_count INTEGER DEFAULT 0
);
## 第 5 步：创建文件存储工具类（scut.util.FileUtil）

saveToFile(List<Student>, String path) — 保存到文件（可用 JSON 或 Properties 格式）
loadFromFile(String path) — 从文件读取
## 第 6 步：创建 DAO 层（scut.dao.StudentDAO）

使用 JDBCUtil，遵循 DAO 模式
insert(Student), update(Student), delete(int id), findAll(), findById(int id)
每次操作同时更新文件和数据库（双存储）
# 🎯 第三阶段：业务逻辑
## 第 7 步：创建导入服务（scut.service.ImportService）

importFromExcel(String filePath) — 使用 POI 读取 .xlsx
importFromText(String filePath) — 逐行读取 .txt（每行一个学生名）
addStudent(String name) — 手动添加单个学生
batchAddStudents(String[] names) — 批量添加
##　第 8 步：创建点名服务（scut.service.RollCallService）

常量 MAX_FAIL_COUNT = 3（连续未答对次数阈值）
selectNextStudent() — 核心算法：
找出 totalCalled 最少的全部学生，随机选一个
如果连续 N 次未答对，改为从 correctCount 最高的前几名中随机选
markCorrect(Student) — 答对：totalCalled++, correctCount++
markIncorrect(Student) — 未答对：仅 totalCalled++
## 第 9 步：创建统计服务（scut.service.StatisticsService）

getAllStats() — 返回所有学生的点名次数、答对次数、答对率
getAnswerRate(Student) — 答对率 = 答对次数 / 被点名次数
# 🎨 第四阶段：完善界面
## 第 10 步：完善导入界面（scut.ui.ImportPanel）

「从 Excel 导入」按钮 → 文件选择器 → 调用 ImportService
「从文本导入」按钮 → 文件选择器 → 调用 ImportService
「手动添加」输入框 + 添加按钮
显示导入结果/已有学生列表
## 第 11 步：完善点名界面（scut.ui.RollCallPanel）

大面积显示当前被点名学生姓名
「开始点名」按钮 → 调用 RollCallService 选人
「答对 ✅」和「未答对 ❌」两个按钮
显示连续未答对计数
## 第 12 步：完善统计界面（scut.ui.StatisticsPanel）

用 JTable 展示：姓名 | 被点名次数 | 答对次数 | 答对率
可选：用 JFreeChart 或手绘柱状图展示排名
# 📦 第五阶段：打包与文档
## 第 13 步：打包成 JAR

编写 MANIFEST.MF，指定主类
编译所有 .java 到 classes/
用 jar 命令打包，包含依赖
写启动批处理文件 bin/run.bat
## 第 14 步：用工具生成代码文档

运行 javadoc -d doc -sourcepath src -subpackages scut
确保所有类和关键方法有 /** ... */ 注释

# 🚀 第六阶段：收尾
## 第 15 步：代码质量检查

IDE 中消除所有警告
运行阿里巴巴代码规约扫描插件，修复问题
检查命名是否符合规范（包名全小写、类名大驼峰、变量/方法小驼峰、常量全大写）

## 第 16 步：发布到互联网

将 Web 版部署到免费云服务器（如阿里云学生机 / Vercel / Railway）
如果用 Swing，可考虑使用 JWebAssembly 或改写为 Spring Boot Web 应用
📊 建议开发顺序总结

# 总结
```
第0步  →  第1步  →  第2步  →  第3步  →  第4步  →  第5步
(环境)   (主界面)  (占位视图) (实体类)  (JDBC工具) (文件工具)
   ↓
第6步  →  第7步  →  第8步  →  第9步  →  第10步 → 第11步
(DAO)   (导入服务) (点名服务) (统计服务) (导入界面) (点名界面)
   ↓
第12步 →  第13步 →  第14步 →  第15步 →  第16步
(统计界面) (打包)   (文档)    (质量检查) (发布)
关键原则：每一步完成并测试通过后再进入下一步，不要跳跃。
```

