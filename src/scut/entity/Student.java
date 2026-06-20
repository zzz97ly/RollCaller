package scut.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 学生实体类
 * <p>
 * 记录学生的基本信息以及点名相关统计数据：
 * <ul>
 *   <li>唯一标识 id</li>
 *   <li>姓名 name</li>
 *   <li>被点名总次数 totalCalled</li>
 *   <li>回答正确次数 correctCount</li>
 * </ul>
 * 实现 Serializable 接口以支持文件序列化存储
 *
 * @author zzz97ly
 */
public class Student implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 未设置 ID 时的默认值 */
    private static final int DEFAULT_ID = -1;

    /** 新学生默认被点名次数 */
    private static final int DEFAULT_TOTAL_CALLED = 0;

    /** 新学生默认答对次数 */
    private static final int DEFAULT_CORRECT_COUNT = 0;

    /** 答对率显示精度（百分比小数位数） */
    private static final int RATE_DECIMAL_PLACES = 1;

    /** 数据库中的百分比乘数 */
    private static final double PERCENT_MULTIPLIER = 100.0;

    /** 学生唯一标识，-1 表示尚未存入数据库 */
    private int id;

    /** 学生姓名 */
    private String name;

    /** 被点名总次数 */
    private int totalCalled;

    /** 回答正确次数 */
    private int correctCount;

    /**
     * 默认构造函数（用于反序列化或手动设置属性）
     */
    public Student() {
        this.id = DEFAULT_ID;
        this.name = "";
        this.totalCalled = DEFAULT_TOTAL_CALLED;
        this.correctCount = DEFAULT_CORRECT_COUNT;
    }

    /**
     * 通过姓名创建学生，点名次数和答对次数默认为 0
     *
     * @param name 学生姓名
     */
    public Student(String name) {
        this.id = DEFAULT_ID;
        this.name = name;
        this.totalCalled = DEFAULT_TOTAL_CALLED;
        this.correctCount = DEFAULT_CORRECT_COUNT;
    }

    /**
     * 通过姓名和 ID 创建学生（通常用于从数据库加载）
     *
     * @param id   数据库中的唯一 ID
     * @param name 学生姓名
     */
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.totalCalled = DEFAULT_TOTAL_CALLED;
        this.correctCount = DEFAULT_CORRECT_COUNT;
    }

    /**
     * 全参数构造函数
     *
     * @param id           数据库 ID
     * @param name         姓名
     * @param totalCalled  被点名总次数
     * @param correctCount 答对次数
     */
    public Student(int id, String name, int totalCalled, int correctCount) {
        this.id = id;
        this.name = name;
        this.totalCalled = totalCalled;
        this.correctCount = correctCount;
    }

    // ======================== Getter / Setter ========================

    /**
     * 获取学生 ID
     * @return 学生 ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置学生 ID
     * @param id 学生 ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取学生姓名
     * @return 学生姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置学生姓名
     * @param name 学生姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取被点名总次数
     * @return 被点名总次数
     */
    public int getTotalCalled() {
        return totalCalled;
    }

    /**
     * 设置被点名总次数
     * @param totalCalled 被点名总次数
     */
    public void setTotalCalled(int totalCalled) {
        this.totalCalled = totalCalled;
    }

    /**
     * 获取答对次数
     * @return 答对次数
     */
    public int getCorrectCount() {
        return correctCount;
    }

    /**
     * 设置答对次数
     * @param correctCount 答对次数
     */
    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    // ======================== 业务方法 ========================

    /**
     * 计算该学生的答对率
     * <p>
     * 答对率 = 答对次数 / 被点名次数。
     * 若尚未被点名（totalCalled == 0），返回 0.0。
     *
     * @return 答对率，范围 [0.0, 1.0]
     */
    public double getAnswerRate() {
        if (totalCalled == 0) {
            return 0.0;
        }
        return (double) correctCount / totalCalled;
    }

    /**
     * 获取格式化的答对率字符串（百分比形式）
     *
     * @return 如 "75.0%"
     */
    public String getAnswerRateFormatted() {
        return String.format("%.1f%%", getAnswerRate() * PERCENT_MULTIPLIER);
    }

    /**
     * 被点名一次（totalCalled + 1），无论是否答对都调用
     */
    public void incrementCalled() {
        this.totalCalled++;
    }

    /**
     * 答对一次（correctCount + 1），仅在回答正确时调用
     * 注意：调用此方法前应确保已调用 incrementCalled()
     */
    public void incrementCorrect() {
        this.correctCount++;
    }

    // ======================== 重写方法 ========================

    /**
     * 基于 id 判断两个学生是否相等
     * <p>
     * 若 id 均为 -1（未入库），则比较姓名
     *
     * @param o 要比较的对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;

        // 两个都已入库：比较 ID
        if (this.id != DEFAULT_ID && student.id != DEFAULT_ID) {
            return this.id == student.id;
        }
        // 否则比较姓名
        return Objects.equals(this.name, student.name);
    }

    @Override
    public int hashCode() {
        if (id != DEFAULT_ID) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }

    /**
     * 格式：Student{id=1, name='张三', called=5, correct=3, rate=60.0%}
     */
    @Override
    public String toString() {
        return "Student{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", called=" + totalCalled
                + ", correct=" + correctCount
                + ", rate=" + getAnswerRateFormatted()
                + '}';
    }
}
