package scut.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 点名面板
 * <p>
 * 核心功能：
 * - 大面积显示被点名学生姓名
 * - 开始点名按钮（触发算法选择学生）
 * - 答对 / 未答对 反馈按钮
 * - 显示连续未答对计数
 * <p>
 * 点名算法由 {@link scut.service.RollCallService} 实现
 *
 * @author zzz97ly
 */
public class RollCallPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 卡片背景 */
    private static final Color COLOR_CARD = Color.WHITE;

    /** 答对按钮颜色 */
    private static final Color COLOR_CORRECT = new Color(76, 175, 80);

    /** 未答对按钮颜色 */
    private static final Color COLOR_INCORRECT = new Color(244, 67, 54);

    /** 开始点名按钮颜色 */
    private static final Color COLOR_START = new Color(33, 150, 243);

    /** 姓名显示区尺寸 */
    private static final int NAME_CARD_WIDTH = 350;
    private static final int NAME_CARD_HEIGHT = 180;

    /** 学生姓名标签（最大号字体） */
    private final JLabel studentNameLabel;

    /** 连续未答对计数标签 */
    private final JLabel streakLabel;

    /** 答对按钮 */
    private final JButton btnCorrect;

    /** 未答对按钮 */
    private final JButton btnIncorrect;

    /** 开始点名按钮 */
    private final JButton btnStart;

    /**
     * 构造点名面板
     */
    public RollCallPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ========== 顶部标题 ==========
        JLabel titleLabel = new JLabel("课堂点名");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        add(titleLabel, BorderLayout.NORTH);

        // ========== 中间：大面积显示学生姓名 ==========
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // 姓名卡片背景
        JPanel nameCard = new JPanel(new GridBagLayout());
        nameCard.setBackground(COLOR_CARD);
        nameCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 225), 1),
                new EmptyBorder(20, 40, 20, 40)
        ));
        nameCard.setPreferredSize(new Dimension(NAME_CARD_WIDTH, NAME_CARD_HEIGHT));

        studentNameLabel = new JLabel("准备点名");
        studentNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
        studentNameLabel.setForeground(new Color(80, 90, 110));
        nameCard.add(studentNameLabel);

        // 连续未答对提示
        streakLabel = new JLabel("连续未答对：0 次");
        streakLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        streakLabel.setForeground(new Color(160, 170, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridy = 0;
        centerPanel.add(nameCard, gbc);
        gbc.gridy = 1;
        centerPanel.add(streakLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // ========== 底部：操作按钮 ==========
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);

        // 开始点名按钮
        btnStart = createStyledButton("🎯 开始点名", COLOR_START, 160, 46);
        bottomPanel.add(btnStart);

        // 分隔线
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 40));
        sep.setForeground(new Color(210, 215, 225));
        bottomPanel.add(sep);

        // 答对按钮
        btnCorrect = createStyledButton("✅ 答对", COLOR_CORRECT, 130, 46);
        btnCorrect.setEnabled(false);
        bottomPanel.add(btnCorrect);

        // 未答对按钮
        btnIncorrect = createStyledButton("❌ 未答对", COLOR_INCORRECT, 130, 46);
        btnIncorrect.setEnabled(false);
        bottomPanel.add(btnIncorrect);

        add(bottomPanel, BorderLayout.SOUTH);

        // TODO: 后续绑定按钮事件，连接 RollCallService
    }

    /**
     * 创建统一样式的按钮
     *
     * @param text   按钮文字
     * @param color  背景颜色
     * @param width  宽度
     * @param height 高度
     * @return 配置好的 JButton
     */
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

    /**
     * 设置界面上显示的当前学生姓名
     *
     * @param name 学生姓名
     */
    public void setStudentName(String name) {
        studentNameLabel.setText(name);
        studentNameLabel.setForeground(new Color(40, 50, 65));
    }

    /**
     * 设置答对/未答对按钮的可用状态
     *
     * @param enabled true 表示点名后可以反馈
     */
    public void setFeedbackEnabled(boolean enabled) {
        btnCorrect.setEnabled(enabled);
        btnIncorrect.setEnabled(enabled);
    }

    /**
     * 更新连续未答对计数显示
     *
     * @param streak 连续未答对次数
     */
    public void setFailStreak(int streak) {
        streakLabel.setText("连续未答对：" + streak + " 次");
        if (streak >= 3) {
            streakLabel.setForeground(new Color(244, 67, 54));
        } else {
            streakLabel.setForeground(new Color(160, 170, 180));
        }
    }
}
