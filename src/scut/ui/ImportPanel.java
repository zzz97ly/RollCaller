package scut.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 导入学生面板
 * <p>
 * 提供三种导入学生方式：
 * 1. 从 Excel 文件导入
 * 2. 从文本文件导入
 * 3. 手动批量添加
 *
 * @author zzz97ly
 */
public class ImportPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 卡片背景色 */
    private static final Color COLOR_CARD_BG = Color.WHITE;

    /** 卡片圆角 */
    private static final int CARD_ARC = 16;

    /** 提示文字颜色 */
    private static final Color COLOR_HINT = new Color(140, 150, 165);

    /**
     * 构造导入面板
     */
    public ImportPanel() {
        setLayout(new GridBagLayout());
        setBackground(COLOR_BG);

        // 标题
        JLabel titleLabel = new JLabel("📥 导入学生");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));

        // 提示文字
        JLabel hintLabel = new JLabel("支持从 Excel、文本文件导入，或手动批量添加学生");
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

        // TODO: 后续完善导入功能的按钮和交互
    }
}
