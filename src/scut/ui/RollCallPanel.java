package scut.ui;

import scut.entity.Student;
import scut.service.RollCallService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 点名面板
 * <p>
 * 核心交互：
 * - 大面积显示被点名学生姓名
 * - 「开始点名」按钮触发算法选择学生
 * - 「答对」和「未答对」按钮收集反馈
 * - 显示连续未答对计数和备用模式提示
 *
 * @author zzz97ly
 */
public class RollCallPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 卡片背景 */
    private static final Color COLOR_CARD = Color.WHITE;

    private static final Color COLOR_CORRECT = new Color(76, 175, 80);
    private static final Color COLOR_INCORRECT = new Color(244, 67, 54);
    private static final Color COLOR_START = new Color(33, 150, 243);
    private static final Color COLOR_WARN = new Color(255, 152, 0);

    private static final int NAME_CARD_WIDTH = 380;
    private static final int NAME_CARD_HEIGHT = 180;

    /** 点名服务 */
    private final RollCallService rollCallService;

    /** 学生姓名标签 */
    private final JLabel studentNameLabel;
    /** 连续未答对计数标签 */
    private final JLabel streakLabel;
    /** 模式提示标签 */
    private final JLabel modeLabel;
    /** 开始点名按钮 */
    private final JButton btnStart;
    /** 答对按钮 */
    private final JButton btnCorrect;
    /** 未答对按钮 */
    private final JButton btnIncorrect;

    /**
     * 构造点名面板，初始化界面和事件绑定
     */
    public RollCallPanel() {
        this.rollCallService = new RollCallService();

        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ===== 顶部 =====
        JLabel titleLabel = new JLabel("课堂点名");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        add(titleLabel, BorderLayout.NORTH);

        // ===== 中间：姓名展示区 =====
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel nameCard = new JPanel(new GridBagLayout());
        nameCard.setBackground(COLOR_CARD);
        nameCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                new EmptyBorder(20, 40, 20, 40)));
        nameCard.setPreferredSize(new Dimension(NAME_CARD_WIDTH, NAME_CARD_HEIGHT));

        studentNameLabel = new JLabel("准备点名");
        studentNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
        studentNameLabel.setForeground(new Color(80, 90, 110));
        nameCard.add(studentNameLabel);

        // 模式提示
        modeLabel = new JLabel("正常模式");
        modeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        modeLabel.setForeground(new Color(160, 170, 180));

        // 连续未答对
        streakLabel = new JLabel("连续未答对：0 / " + RollCallService.MAX_FAIL_STREAK);
        streakLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        streakLabel.setForeground(new Color(160, 170, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridy = 0; centerPanel.add(nameCard, gbc);
        gbc.gridy = 1; centerPanel.add(modeLabel, gbc);
        gbc.gridy = 2; centerPanel.add(streakLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // ===== 底部按钮 =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);

        btnStart = createStyledButton("🎯 开始点名", COLOR_START, 160, 46);
        btnStart.addActionListener(e -> doRollCall());
        bottomPanel.add(btnStart);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 40));
        bottomPanel.add(sep);

        btnCorrect = createStyledButton("✅ 答对", COLOR_CORRECT, 130, 46);
        btnCorrect.setEnabled(false);
        btnCorrect.addActionListener(e -> doMarkCorrect());
        bottomPanel.add(btnCorrect);

        btnIncorrect = createStyledButton("❌ 未答对", COLOR_INCORRECT, 130, 46);
        btnIncorrect.setEnabled(false);
        btnIncorrect.addActionListener(e -> doMarkIncorrect());
        bottomPanel.add(btnIncorrect);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ======================== 按钮逻辑 ========================

    private void doRollCall() {
        try {
            Student s = rollCallService.selectNextStudent();
            studentNameLabel.setText(s.getName());
            studentNameLabel.setForeground(new Color(40, 50, 65));
            setFeedbackEnabled(true);

            // 更新状态
            updateStatusDisplay();

            // 动画提示
            studentNameLabel.setForeground(COLOR_START);
            Timer timer = new Timer(600, e ->
                    studentNameLabel.setForeground(new Color(40, 50, 65)));
            timer.setRepeats(false);
            timer.start();

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doMarkCorrect() {
        rollCallService.markCorrect();
        studentNameLabel.setText(studentNameLabel.getText() + " ✅");
        studentNameLabel.setForeground(COLOR_CORRECT);
        finishFeedback();
    }

    private void doMarkIncorrect() {
        rollCallService.markIncorrect();
        studentNameLabel.setText(studentNameLabel.getText() + " ❌");
        studentNameLabel.setForeground(COLOR_INCORRECT);
        finishFeedback();
    }

    private void finishFeedback() {
        setFeedbackEnabled(false);
        updateStatusDisplay();
        // 1.5 秒后恢复显示，方便看清结果
        Timer timer = new Timer(1500, e -> {
            Student s = rollCallService.getCurrentStudent();
            if (s != null) {
                studentNameLabel.setText(s.getName());
                studentNameLabel.setForeground(new Color(40, 50, 65));
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ======================== UI 更新 ========================

    private void updateStatusDisplay() {
        int streak = rollCallService.getFailStreak();
        boolean backup = rollCallService.isBackupMode();

        streakLabel.setText("连续未答对：" + streak + " / " + RollCallService.MAX_FAIL_STREAK);
        streakLabel.setForeground(streak >= RollCallService.MAX_FAIL_STREAK
                ? COLOR_INCORRECT : new Color(160, 170, 180));

        if (backup) {
            modeLabel.setText("⚠ 备用模式 — 从答对多的同学中抽取");
            modeLabel.setForeground(COLOR_WARN);
        } else {
            modeLabel.setText("正常模式 — 优先点名次数少的同学");
            modeLabel.setForeground(new Color(160, 170, 180));
        }
    }

    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setPreferredSize(new Dimension(width, height));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setFeedbackEnabled(boolean enabled) {
        btnCorrect.setEnabled(enabled);
        btnIncorrect.setEnabled(enabled);
        btnStart.setEnabled(!enabled);
    }
}
