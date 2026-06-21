package scut.dao;

import org.springframework.stereotype.Repository;
import scut.entity.Student;
import scut.util.FileUtil;
import scut.util.JDBCUtil;

import java.util.List;

/**
 * 学生数据访问对象
 * <p>
 * 遵循 DAO 模式，负责学生的所有数据存取操作。
 * 采用双存储策略：每次写操作同时更新数据库和文件，
 * 读取时优先从数据库加载，确保数据一致性和可靠性。
 *
 * @author zzz97ly
 */
@Repository
public class StudentDAO {

    /** 查询所有学生 */
    private static final String SQL_FIND_ALL =
            "SELECT id, name, total_called, correct_count FROM student ORDER BY id";

    /** 按 ID 查询 */
    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, total_called, correct_count FROM student WHERE id = ?";

    /** 按姓名查询 */
    private static final String SQL_FIND_BY_NAME =
            "SELECT id, name, total_called, correct_count FROM student WHERE name = ?";

    /** 插入新学生 */
    private static final String SQL_INSERT =
            "INSERT INTO student (name, total_called, correct_count) VALUES (?, ?, ?)";

    /** 更新学生数据 */
    private static final String SQL_UPDATE =
            "UPDATE student SET name = ?, total_called = ?, correct_count = ? WHERE id = ?";

    /** 按 ID 删除 */
    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM student WHERE id = ?";

    /** 清空表（重置数据用） */
    private static final String SQL_DELETE_ALL =
            "DELETE FROM student";

    /** 当前内存中的学生列表缓存 */
    private List<Student> cache;

    /** Spring 和 Swing 共用此构造 */
    public StudentDAO() {
    }

    /**
     * 获取 DAO 实例（供 Swing UI 等非 Spring 管理的类使用）
     * @return StudentDAO 实例
     */
    public static StudentDAO getInstance() {
        return new StudentDAO();
    }

    // ======================== 读取 ========================

    /**
     * 加载所有学生
     * <p>
     * 优先从数据库读取；若数据库为空则尝试从文件恢复。
     * 结果缓存到内存中。
     *
     * @return 所有学生列表
     */
    public List<Student> findAll() {
        cache = JDBCUtil.executeQuery(SQL_FIND_ALL, rs -> new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("total_called"),
                rs.getInt("correct_count")
        ));

        // 数据库为空时尝试从文件恢复
        if (cache.isEmpty()) {
            List<Student> fromFile = FileUtil.loadFromFile();
            if (!fromFile.isEmpty()) {
                for (Student s : fromFile) {
                    s.setId(-1); // 重置 ID，让数据库重新分配
                    insert(s);
                }
                cache = JDBCUtil.executeQuery(SQL_FIND_ALL, rs -> new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("total_called"),
                        rs.getInt("correct_count")
                ));
            }
        }
        return cache;
    }

    /**
     * 按 ID 查找学生
     *
     * @param id 学生 ID
     * @return 找到的学生，未找到返回 null
     */
    public Student findById(int id) {
        return JDBCUtil.executeQuerySingle(SQL_FIND_BY_ID, rs -> new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("total_called"),
                rs.getInt("correct_count")
        ), id);
    }

    /**
     * 按姓名查找学生
     *
     * @param name 学生姓名
     * @return 找到的学生，未找到返回 null
     */
    public Student findByName(String name) {
        return JDBCUtil.executeQuerySingle(SQL_FIND_BY_NAME, rs -> new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("total_called"),
                rs.getInt("correct_count")
        ), name);
    }

    /**
     * 获取学生总数
     *
     * @return 学生数量
     */
    public int getCount() {
        return JDBCUtil.count("student");
    }

    // ======================== 写入（双存储） ========================

    /**
     * 插入新学生
     * <p>
     * 同时写入数据库和文件。
     *
     * @param student 待插入的学生（id 会被忽略，由数据库分配）
     * @return 插入后的学生对象（含数据库分配的新 ID）
     */
    public Student insert(Student student) {
        int newId = JDBCUtil.executeInsert(SQL_INSERT,
                student.getName(),
                student.getTotalCalled(),
                student.getCorrectCount()
        );
        student.setId(newId);
        syncToFile();
        return student;
    }

    /**
     * 更新学生数据
     * <p>
     * 同时更新数据库和文件。
     *
     * @param student 待更新的学生对象
     */
    public void update(Student student) {
        JDBCUtil.executeUpdate(SQL_UPDATE,
                student.getName(),
                student.getTotalCalled(),
                student.getCorrectCount(),
                student.getId()
        );
        syncToFile();
    }

    /**
     * 按 ID 删除学生
     * <p>
     * 同时从数据库和文件中移除。
     *
     * @param id 学生 ID
     */
    public void delete(int id) {
        JDBCUtil.executeUpdate(SQL_DELETE_BY_ID, id);
        syncToFile();
    }

    /**
     * 清空所有学生数据
     */
    public void deleteAll() {
        JDBCUtil.executeUpdate(SQL_DELETE_ALL);
        syncToFile();
    }

    // ======================== 缓存与同步 ========================

    /**
     * 将数据库当前数据同步到文件
     * <p>
     * 每次写操作后自动调用，保证文件是数据库的完整镜像。
     */
    private void syncToFile() {
        List<Student> all = JDBCUtil.executeQuery(SQL_FIND_ALL, rs -> new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("total_called"),
                rs.getInt("correct_count")
        ));
        FileUtil.saveToFile(all);
        cache = all;
    }

    /**
     * 刷新内存缓存（从数据库重新加载）
     */
    public void refreshCache() {
        findAll();
    }

    /**
     * 获取缓存的学生列表（避免重复查库）
     *
     * @return 学生列表
     */
    public List<Student> getCache() {
        if (cache == null) {
            findAll();
        }
        return cache;
    }
}
