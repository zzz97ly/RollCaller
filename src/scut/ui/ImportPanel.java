package scut.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 导入学生面板
 * <p>
 * 提供三种导入学生方式：
 * 1. 从 Excel 文件导入
 * 2. 从文本文件导入
 * 3. 手动批量添加
 * <p>
 * 底部显示当前已导入的学生列表预览
 *
 * @author zzz97ly
 */
public class ImportPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 按钮颜色 - 绿色系 */
    private static final Color COLOR_EXCEL_BTN = new Color(33, 150, 83);
    private static final Color COLOR_TEXT_BTN = new Color(66, 133, 244);
    private static final Color COLOR_MANUAL_BTN = new Color(251, 140, 0);

    /** 按钮圆角 */
    private static final int BUTTON_ARC = 10;

    /** 按钮宽度 */
    private static final int BUTTON_WIDTH = 180;

    /** 按钮高度 */
    private static final int BUTTON_HEIGHT = 80;

    /** 表格列名 */
    private static final String[] TABLE_COLUMNS = {"序号", "姓名", "导入方式"};

    /** 学生列表表格（预览用） */
    private final JTable studentTable;

    /** 表格模型 */
    private final DefaultTableModel tableModel;

    /**
     * 构造导入面板，布局分为上下两部分：
     * 上半部分为三个导入按钮，下半部分为学生列表预览
     */
    public ImportPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ========== 上半部分：导入按钮区 ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // 标题
        JLabel titleLabel = new JLabel("导入学生");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // 三个导入按钮 + 说明文字居中
        JPanel buttonGroup = new JPanel(new GridBagLayout());
        buttonGroup.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonGroup.add(createImportCard("📥", "从 Excel 导入", "支持 .xlsx 格式", COLOR_EXCEL_BTN), gbc);
        gbc.gridx = 1;
        buttonGroup.add(createImportCard("📄", "从文本文件导入", "每行一个学生姓名", COLOR_TEXT_BTN), gbc);
        gbc.gridx = 2;
        buttonGroup.add(createImportCard("✏️", "手动批量添加", "在输入框中添加", COLOR_MANUAL_BTN), gbc);

        topPanel.add(buttonGroup, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ========== 下半部分：学生列表预览 ==========
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);

        JLabel listTitle = new JLabel("已导入学生列表");
        listTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        listTitle.setForeground(new Color(100, 110, 120));
        bottomPanel.add(listTitle, BorderLayout.NORTH);

        // 表格
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        studentTable.setRowHeight(28);
        studentTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(240, 243, 248));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.CENTER);
    }

    /**
     * 创建一个导入方式卡片（图标 + 标题 + 说明）
     *
     * @param icon        图标文字
     * @param title       标题
     * @param description 说明文字
     * @param color       主题色
     * @return 组装好的面板
     */
    private JPanel createImportCard(String icon, String title, String description, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 228), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 图标
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 标题
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        titleLbl.setForeground(color);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 说明
        JLabel descLbl = new JLabel(description);
        descLbl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        descLbl.setForeground(new Color(140, 150, 165));
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 间距
        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);
        card.add(Box.createVerticalGlue());

        // TODO: 后续绑定点击事件，连接 ImportService

        return card;
    }

    /**
     * 获取表格模型，供外部添加学生预览数据
     *
     * @return DefaultTableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
