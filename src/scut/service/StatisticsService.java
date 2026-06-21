package scut.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scut.dao.StudentDAO;
import scut.entity.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务 — 提供点名数据的汇总统计
 * <p>
 * 功能：
 * <ul>
 *   <li>计算每个学生的答对率</li>
 *   <li>汇总全班统计数据（总人数、总点名次数、平均答对率等）</li>
 *   <li>提供排序后的排行榜</li>
 * </ul>
 *
 * @author zzz97ly
 */
@Service
public class StatisticsService {

    @Autowired
    private StudentDAO studentDAO;

    // ======================== 基础统计 ========================

    /**
     * 获取所有学生的统计数据
     *
     * @return 学生列表，每个学生含 called/correct/rate 完整信息
     */
    public List<Student> getAllStats() {
        return studentDAO.findAll();
    }

    /**
     * 计算单个学生的答对率
     * <p>
     * 答对率 = correctCount / totalCalled。
     * 未点过的学生答对率为 0.0。
     *
     * @param student 学生对象
     * @return 答对率 [0.0, 1.0]
     */
    public double getAnswerRate(Student student) {
        return student.getAnswerRate();
    }

    /**
     * 获取学生总数
     *
     * @return 学生人数
     */
    public int getTotalCount() {
        return studentDAO.getCount();
    }

    // ======================== 汇总统计 ========================

    /**
     * 计算班级汇总数据
     *
     * @return Summary 对象，包含总数、点名总次数、答对总次数、平均答对率
     */
    public Summary getSummary() {
        List<Student> all = studentDAO.findAll();
        int totalCalledSum = 0;
        int totalCorrectSum = 0;

        for (Student s : all) {
            totalCalledSum += s.getTotalCalled();
            totalCorrectSum += s.getCorrectCount();
        }

        int studentCount = all.size();
        double avgRate = (totalCalledSum == 0)
                ? 0.0
                : (double) totalCorrectSum / totalCalledSum;

        return new Summary(studentCount, totalCalledSum, totalCorrectSum, avgRate);
    }

    // ======================== 排行榜 ========================

    /**
     * 按被点名次数降序排列（谁被点最多）
     *
     * @return 排序后的学生列表
     */
    public List<Student> getRankByCalled() {
        List<Student> list = new ArrayList<>(studentDAO.findAll());
        list.sort((a, b) -> Integer.compare(b.getTotalCalled(), a.getTotalCalled()));
        return list;
    }

    /**
     * 按答对次数降序排列（学霸榜）
     *
     * @return 排序后的学生列表
     */
    public List<Student> getRankByCorrect() {
        List<Student> list = new ArrayList<>(studentDAO.findAll());
        list.sort((a, b) -> Integer.compare(b.getCorrectCount(), a.getCorrectCount()));
        return list;
    }

    /**
     * 按答对率降序排列
     *
     * @return 排序后的学生列表
     */
    public List<Student> getRankByRate() {
        List<Student> list = new ArrayList<>(studentDAO.findAll());
        list.sort((a, b) -> Double.compare(b.getAnswerRate(), a.getAnswerRate()));
        return list;
    }

    // ======================== 内部类 ========================

    /**
     * 班级统计汇总数据
     */
    public static class Summary {

        /** 学生总数 */
        public final int studentCount;

        /** 全班被点名总次数 */
        public final int totalCalled;

        /** 全班答对总次数 */
        public final int totalCorrect;

        /** 全班平均答对率 [0.0, 1.0] */
        public final double averageRate;

        /**
         * 构造班级统计汇总
         * @param studentCount 学生总数
         * @param totalCalled  全班被点名总次数
         * @param totalCorrect 全班答对总次数
         * @param averageRate  平均答对率 [0.0, 1.0]
         */
        public Summary(int studentCount, int totalCalled, int totalCorrect, double averageRate) {
            this.studentCount = studentCount;
            this.totalCalled = totalCalled;
            this.totalCorrect = totalCorrect;
            this.averageRate = averageRate;
        }

        /**
         * 获取格式化的平均答对率
         *
         * @return 如 "65.3%"
         */
        public String getAverageRateFormatted() {
            return String.format("%.1f%%", averageRate * 100.0);
        }

        @Override
        public String toString() {
            return String.format(
                    "Summary{学生=%d, 总点名=%d, 总答对=%d, 平均答对率=%s}",
                    studentCount, totalCalled, totalCorrect, getAverageRateFormatted()
            );
        }
    }
}
