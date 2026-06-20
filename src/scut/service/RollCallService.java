package scut.service;

import scut.dao.StudentDAO;
import scut.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 点名服务 — 实现智能点名算法
 * <p>
 * 正常模式：优先选择被点名次数最少的学生中随机抽取
 * 备用模式：连续 N 次未答对后，改为从答对次数最多的学生中随机抽取
 * <p>
 * 每次点名后，通过 {@link #markCorrect()} 或 {@link #markIncorrect()} 反馈结果，
 * 系统自动更新学生数据和连续未答对计数。
 *
 * @author zzz97ly
 */
public class RollCallService {

    /** 连续未答对次数阈值，达到后切换到备用模式 */
    public static final int MAX_FAIL_STREAK = 3;

    /** 备用模式下从前几名中随机选取的人数 */
    private static final int TOP_N_IN_BACKUP_MODE = 3;

    /** 随机数生成器 */
    private final Random random;

    /** 数据访问层 */
    private final StudentDAO studentDAO;

    /** 当前被点名的学生 */
    private Student currentStudent;

    /** 连续未答对次数 */
    private int failStreak;

    /** 是否处于备用模式 */
    private boolean backupMode;

    public RollCallService() {
        this.random = new Random();
        this.studentDAO = StudentDAO.getInstance();
        this.failStreak = 0;
        this.backupMode = false;
    }

    // ======================== 点名 ========================

    /**
     * 执行点名，根据算法选出下一个学生
     * <p>
     * 算法逻辑：
     * <ol>
     *   <li>从 DAO 加载所有学生</li>
     *   <li>正常模式：找出被点名次数最少的一批，随机选一个</li>
     *   <li>若连续未答对次数 ≥ {@value #MAX_FAIL_STREAK}，
     *       切换备用模式：从答对次数最高的前几名中随机选</li>
     * </ol>
     *
     * @return 被选中的学生
     * @throws IllegalStateException 学生列表为空时抛出
     */
    public Student selectNextStudent() {
        List<Student> all = studentDAO.findAll();
        if (all.isEmpty()) {
            throw new IllegalStateException("没有学生，请先导入学生名单");
        }

        // 判断是否触发备用模式
        if (failStreak >= MAX_FAIL_STREAK) {
            backupMode = true;
        }

        Student selected;
        if (backupMode) {
            selected = selectFromTopCorrect(all);
        } else {
            selected = selectFromLeastCalled(all);
        }

        currentStudent = selected;
        return selected;
    }

    // ======================== 反馈 ========================

    /**
     * 标记当前学生回答正确
     * <p>
     * totalCalled +1, correctCount +1，重置连续未答对计数，
     * 退出备用模式，数据持久化到数据库和文件
     */
    public void markCorrect() {
        if (currentStudent == null) {
            return;
        }
        currentStudent.incrementCalled();
        currentStudent.incrementCorrect();
        studentDAO.update(currentStudent);

        failStreak = 0;
        backupMode = false;
    }

    /**
     * 标记当前学生未回答出问题
     * <p>
     * totalCalled +1, correctCount 不变，连续未答对计数 +1，
     * 数据持久化到数据库和文件
     */
    public void markIncorrect() {
        if (currentStudent == null) {
            return;
        }
        currentStudent.incrementCalled();
        studentDAO.update(currentStudent);

        failStreak++;
    }

    // ======================== 算法核心 ========================

    /**
     * 正常模式：从被点名次数最少的学生中随机选一个
     *
     * @param all 所有学生
     * @return 选中的学生
     */
    private Student selectFromLeastCalled(List<Student> all) {
        // 找最小 totalCalled
        int minCalled = Integer.MAX_VALUE;
        for (Student s : all) {
            if (s.getTotalCalled() < minCalled) {
                minCalled = s.getTotalCalled();
            }
        }

        // 收集所有达到最小值的学生
        List<Student> candidates = new ArrayList<>();
        for (Student s : all) {
            if (s.getTotalCalled() == minCalled) {
                candidates.add(s);
            }
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * 备用模式：从答对次数最高的前 N 名学生中随机选一个
     *
     * @param all 所有学生
     * @return 选中的学生
     */
    private Student selectFromTopCorrect(List<Student> all) {
        // 按 correctCount 降序排列，取前 TOP_N
        List<Student> sorted = new ArrayList<>(all);
        sorted.sort((a, b) -> Integer.compare(b.getCorrectCount(), a.getCorrectCount()));

        int topCount = Math.min(TOP_N_IN_BACKUP_MODE, sorted.size());
        List<Student> topN = sorted.subList(0, topCount);

        return topN.get(random.nextInt(topN.size()));
    }

    // ======================== 状态查询 ========================

    /**
     * 获取当前被点名的学生
     *
     * @return 当前学生，未点名时返回 null
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }

    /**
     * 获取连续未答对次数
     *
     * @return 连续未答对次数
     */
    public int getFailStreak() {
        return failStreak;
    }

    /**
     * 当前是否处于备用模式
     *
     * @return true 表示备用模式（从答对多的选）
     */
    public boolean isBackupMode() {
        return backupMode;
    }

    /**
     * 重置状态（用于重新开始点名）
     */
    public void reset() {
        currentStudent = null;
        failStreak = 0;
        backupMode = false;
    }
}
