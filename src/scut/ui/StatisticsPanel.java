package scut.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 统计展示面板
 * <p>
 * 以表格形式展示每个学生的：
 * 被点名次数、答对次数、答对率
 *
 * @author zzz97ly
 */
public class StatisticsPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 提示文字颜色 */
    private static final Color COLOR_HINT = new Color(140, 150, 165);

    /**
     * 构造统计面板
     */
    public StatisticsPanel() {
        setLayout(new GridBagLayout());
        setBackground(COLOR_BG);

        // 标题
        JLabel titleLabel = new JLabel("📊 统计展示");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));

        // 提示文字
        JLabel hintLabel = new JLabel("查看每个学生的被点名次数、答对次数、答对率");
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

        // TODO: 后续完善统计功能——JTable 展示数据、柱状图
    }
}
