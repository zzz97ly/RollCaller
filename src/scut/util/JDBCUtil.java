package scut.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个数据库工具类，封装了操作 SQLite 数据库的所有底层代码。之后 DAO 层（第 6 步）会调用它，不需要再写重复的 JDBC 样板代码。
 * JDBC 工具类 — 封装 SQLite 数据库的连接与基础操作
 * <p>
 * 职责：
 * <ul>
 *   <li>管理数据库连接（打开 / 关闭）</li>
 *   <li>提供通用的增删改查方法</li>
 *   <li>首次运行时自动创建表结构</li>
 * </ul>
 * <p>
 * 使用 SQLite 数据库，无需额外安装或启动服务。
 * 数据库文件位于 data/rollcaller.db
 *
 * @author zzz97ly
 */
public final class JDBCUtil {

    /** SQLite JDBC 驱动类名 */
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";

    /** 数据库连接 URL，data/rollcaller.db 相对于项目根目录 */
    private static final String DB_URL = "jdbc:sqlite:data/rollcaller.db";

    /** 建表 SQL —— 学生表 */
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS student ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "total_called INTEGER DEFAULT 0, "
                    + "correct_count INTEGER DEFAULT 0"
                    + ");";

    /** 数据库连接实例（单连接，够课堂点名场景使用） */
    private static Connection connection;

    /** 工具类不允许实例化 */
    private JDBCUtil() {
    }

    // ======================== 连接管理 ========================

    /**
     * 获取数据库连接
     * <p>
     * 首次调用时自动加载驱动、建立连接并初始化表结构。
     * 后续调用返回已存在的连接。
     *
     * @return 数据库连接对象
     * @throws RuntimeException 若加载驱动或建立连接失败
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(DB_URL);
                initTables();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("未找到 SQLite JDBC 驱动，请检查 lib/ 目录", e);
            } catch (SQLException e) {
                throw new RuntimeException("无法连接数据库: " + DB_URL, e);
            }
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     * <p>
     * 程序退出前应调用此方法释放资源
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时出错: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    /**
     * 首次运行时自动创建所需的数据库表
     */
    private static void initTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据库表失败", e);
        }
    }

    // ======================== 通用 DML 操作 ========================

    /**
     * 执行 INSERT / UPDATE / DELETE 语句
     *
     * @param sql    待执行的 SQL，可包含 ? 占位符
     * @param params 参数列表，按顺序替换占位符
     * @return 受影响的行数
     * @throws RuntimeException 若 SQL 执行失败
     */
    public static int executeUpdate(String sql, Object... params) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("执行 SQL 失败: " + sql, e);
        }
    }

    /**
     * 执行 INSERT 并返回自增主键
     *
     * @param sql    待执行的 INSERT 语句
     * @param params 参数列表
     * @return 生成的自增主键值，若未生成则返回 -1
     * @throws RuntimeException 若 SQL 执行失败
     */
    public static int executeInsert(String sql, Object... params) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(pstmt, params);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("执行 INSERT 失败: " + sql, e);
        }
        return -1;
    }

    // ======================== 通用 DQL 操作 ========================

    /**
     * 执行 SELECT 查询并返回结果集处理后的数据列表
     * <p>
     * 使用 RowMapper 回调，调用者自行将 ResultSet 的一行映射为对象
     *
     * @param sql      SELECT 查询语句
     * @param mapper   行映射器
     * @param params   查询参数
     * @param <T>      返回的数据类型
     * @return 查询结果列表
     * @throws RuntimeException 若查询执行失败
     */
    public static <T> List<T> executeQuery(String sql, RowMapper<T> mapper, Object... params) {
        List<T> resultList = new ArrayList<>();
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(mapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("执行查询失败: " + sql, e);
        }
        return resultList;
    }

    /**
     * 执行 SELECT 查询并返回单条记录
     *
     * @param sql    查询语句
     * @param mapper 行映射器
     * @param params 查询参数
     * @param <T>    返回类型
     * @return 查询到的一条记录，无结果返回 {@code null}
     */
    public static <T> T executeQuerySingle(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = executeQuery(sql, mapper, params);
        return results.isEmpty() ? null : results.get(0);
    }

    // ======================== 工具方法 ========================

    /**
     * 为 PreparedStatement 设置参数
     *
     * @param pstmt  PreparedStatement 对象
     * @param params 参数数组
     * @throws SQLException 若参数设置失败
     */
    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * 获取表中记录总数
     *
     * @param tableName 表名
     * @return 记录总数
     */
    public static int count(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询表记录数失败: " + tableName, e);
        }
        return 0;
    }

    // ======================== 内部接口 ========================

    /**
     * 行映射器 —— 将 ResultSet 的一行映射为实体对象
     *
     * @param <T> 目标实体类型
     */
    @FunctionalInterface
    public interface RowMapper<T> {

        /**
         * 将当前行映射为实体对象
         * <p>
         * 调用时 ResultSet 的游标已指向当前行，
         * 无需调用 rs.next()
         *
         * @param rs ResultSet 对象
         * @return 映射后的实体对象
         * @throws SQLException 若读取数据失败
         */
        T mapRow(ResultSet rs) throws SQLException;
    }
}
