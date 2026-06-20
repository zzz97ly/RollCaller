package scut.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 点名面板
 * <p>
 * 显示被点名的学生姓名，提供"答对"和"未答对"反馈按钮。
 * 核心点名算法由 {@link scut.service.RollCallService} 实现。
 *
 * @author zzz97ly
 */
public class RollCallPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 提示文字颜色 */
    private static final Color COLOR_HINT = new Color(140, 150, 165);

    /**
     * 构造点名面板
     */
    public RollCallPanel() {
        setLayout(new GridBagLayout());
        setBackground(COLOR_BG);

        // 标题
        JLabel titleLabel = new JLabel("🎯 点名");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));

        // 提示文字
        JLabel hintLabel = new JLabel("点击 [开始点名] 随机选择学生");
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        hintLabel.setForeground(COLOR_HINT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        add(titleLabel, gbc);
        gbc.gridy = 1;
        add(hintLabel, gbc);

        // TODO: 后续完善点名功能——开始点名按钮、学生姓名显示、答对/未答对按钮
    }
}
