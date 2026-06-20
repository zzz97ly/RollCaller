package scut.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 统计展示面板
 * <p>
 * 以表格形式展示每个学生的：
 * 序号、姓名、被点名次数、答对次数、答对率
 * <p>
 * 后续可选添加柱状图可视化
 *
 * @author zzz97ly
 */
public class StatisticsPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 表头背景色 */
    private static final Color COLOR_HEADER = new Color(70, 130, 180);

    /** 表格列名 */
    private static final String[] TABLE_COLUMNS = {
            "序号", "姓名", "被点名次数", "答对次数", "答对率"
    };

    /** 表格 */
    private final JTable statsTable;

    /** 表格数据模型 */
    private final DefaultTableModel tableModel;

    /** 学生总数标签 */
    private final JLabel totalLabel;

    /** 答对率标签 */
    private final JLabel avgRateLabel;

    /**
     * 构造统计面板
     */
    public StatisticsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ========== 顶部：标题 + 概览卡片 ==========
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // 标题
        JLabel titleLabel = new JLabel("统计展示");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // 三个概览卡片（总数、平均答对率等）
        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        summaryRow.setOpaque(false);

        totalLabel = createSummaryCard("👥", "学生总数", "0 人");
        avgRateLabel = createSummaryCard("📈", "平均答对率", "0.0%");
        summaryRow.add(totalLabel);
        summaryRow.add(avgRateLabel);

        topPanel.add(summaryRow, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ========== 中部：数据表格 ==========
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statsTable.setRowHeight(32);
        statsTable.setGridColor(new Color(225, 230, 238));
        statsTable.setShowVerticalLines(false);
        statsTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        statsTable.getTableHeader().setBackground(COLOR_HEADER);
        statsTable.getTableHeader().setForeground(Color.WHITE);
        statsTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // 居中对齐渲染器
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < TABLE_COLUMNS.length; i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));

        add(scrollPane, BorderLayout.CENTER);

        // TODO: 后续添加柱状图展示，连接 StatisticsService
    }

    /**
     * 创建一个概览统计卡片
     *
     * @param icon  图标
     * @param title 标题
     * @param value 数值
     * @return 包含卡片的 JLabel（用于放入父容器）
     */
    private JLabel createSummaryCard(String icon, String title, String value) {
        // 使用 HTML 在 JLabel 中渲染卡片布局
        String html = String.format(
                "<html><div style='background:#fff;border:1px solid #d0d6e0;"
                        + "border-radius:10px;padding:12px 20px;text-align:center;'>"
                        + "<span style='font-size:20px;'>%s</span>&nbsp;"
                        + "<span style='color:#3c4655;font-size:13px;'>%s</span><br>"
                        + "<span style='color:#4682b4;font-size:22px;font-weight:bold;'>%s</span>"
                        + "</div></html>",
                icon, title, value
        );
        JLabel label = new JLabel(html);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        return label;
    }

    /**
     * 获取表格数据模型，供外部更新统计数据
     *
     * @return DefaultTableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * 更新概览卡片数据
     *
     * @param totalCount 学生总数
     * @param avgRate    平均答对率
     */
    public void setSummaryData(int totalCount, double avgRate) {
        // 更新 totalLabel 和 avgRateLabel 的 HTML 内容
        // TODO: 后续实现动态更新概览卡片
    }
}
