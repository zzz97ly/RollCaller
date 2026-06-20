package scut.ui;

import scut.entity.Student;
import scut.service.StatisticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 统计展示面板
 * <p>
 * 以表格和柱状图形式展示：
 * 序号、姓名、被点名次数、答对次数、答对率
 * 顶部汇总卡片展示学生总数和平均答对率
 *
 * @author zzz97ly
 */
public class StatisticsPanel extends JPanel {

    private static final Color COLOR_BG = new Color(245, 248, 252);
    private static final Color COLOR_HEADER = new Color(70, 130, 180);
    private static final Color COLOR_BAR = new Color(100, 180, 130);
    private static final Color COLOR_BAR_LOW = new Color(240, 180, 100);

    private static final String[] TABLE_COLUMNS = {"序号", "姓名", "被点名次数", "答对次数", "答对率"};

    /** 柱状图区域高度 */
    private static final int CHART_HEIGHT = 180;

    /** 柱状图每柱最大高度 */
    private static final int MAX_BAR_HEIGHT = 140;

    /** 柱状图每柱宽度 */
    private static final int BAR_WIDTH = 36;

    /** 柱间距 */
    private static final int BAR_GAP = 12;

    private final StatisticsService statsService;
    private final DefaultTableModel tableModel;
    private final JTable statsTable;
    private final JLabel cardStudentCount;
    private final JLabel cardAvgRate;
    private final JLabel cardTotalCalls;
    private final BarChartPanel barChart;

    public StatisticsPanel() {
        this.statsService = new StatisticsService();

        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ===== 顶部：标题 + 概览卡片 =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("统计展示");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        summaryRow.setOpaque(false);

        cardStudentCount = createSummaryCard("👥", "学生总数");
        cardAvgRate = createSummaryCard("📈", "平均答对率");
        cardTotalCalls = createSummaryCard("📋", "总点名次数");
        summaryRow.add(cardStudentCount);
        summaryRow.add(cardAvgRate);
        summaryRow.add(cardTotalCalls);

        topPanel.add(summaryRow, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== 中间：表格 + 柱状图 =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(480);
        splitPane.setOpaque(false);

        // --- 左侧：表格 ---
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statsTable.setRowHeight(30);
        statsTable.setGridColor(new Color(225, 230, 238));
        statsTable.setShowVerticalLines(false);
        statsTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        statsTable.getTableHeader().setBackground(COLOR_HEADER);
        statsTable.getTableHeader().setForeground(Color.WHITE);
        statsTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < TABLE_COLUMNS.length; i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane tableScroll = new JScrollPane(statsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        splitPane.setLeftComponent(tableScroll);

        // --- 右侧：柱状图 ---
        barChart = new BarChartPanel();
        barChart.setBackground(Color.WHITE);
        barChart.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        splitPane.setRightComponent(barChart);

        add(splitPane, BorderLayout.CENTER);

        // ===== 底部：刷新按钮 =====
        JButton refreshBtn = new JButton("🔄 刷新数据");
        refreshBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> refreshData());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setOpaque(false);
        btnRow.add(refreshBtn);
        add(btnRow, BorderLayout.SOUTH);

        // 初次加载
        refreshData();
    }

    // ======================== 数据刷新 ========================

    /**
     * 从 StatisticsService 加载最新数据并刷新界面
     */
    public void refreshData() {
        // 表格
        tableModel.setRowCount(0);
        List<Student> all = statsService.getAllStats();
        int index = 1;
        for (Student s : all) {
            tableModel.addRow(new Object[]{
                    index++,
                    s.getName(),
                    s.getTotalCalled(),
                    s.getCorrectCount(),
                    s.getAnswerRateFormatted()
            });
        }

        // 汇总卡片
        StatisticsService.Summary sum = statsService.getSummary();
        setCardValue(cardStudentCount, sum.studentCount + " 人");
        setCardValue(cardAvgRate, sum.getAverageRateFormatted());
        setCardValue(cardTotalCalls, sum.totalCalled + " 次");

        // 柱状图
        barChart.setData(all);
    }

    // ======================== 卡片更新 ========================

    private JLabel createSummaryCard(String icon, String title) {
        JLabel label = new JLabel();
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setText(buildCardHtml(icon, title, "—"));
        return label;
    }

    private void setCardValue(JLabel card, String value) {
        String html = card.getText();
        // 替换最后一个 bold span 的值为新数据
        html = html.replaceAll(
                "<span style='color:#4682b4;font-size:22px;font-weight:bold;'>[^<]*</span>",
                "<span style='color:#4682b4;font-size:22px;font-weight:bold;'>" + value + "</span>");
        card.setText(html);
    }

    private String buildCardHtml(String icon, String title, String value) {
        return "<html><div style='background:#fff;border:1px solid #d0d6e0;"
                + "border-radius:10px;padding:10px 18px;text-align:center;'>"
                + "<span style='font-size:20px;'>" + icon + "</span>&nbsp;"
                + "<span style='color:#3c4655;font-size:13px;'>" + title + "</span><br>"
                + "<span style='color:#4682b4;font-size:22px;font-weight:bold;'>" + value + "</span>"
                + "</div></html>";
    }

    // ======================== 柱状图组件 ========================

    /**
     * 答对率柱状图 — 手绘组件，无需第三方库
     */
    private static class BarChartPanel extends JPanel {

        private List<Student> students;

        public BarChartPanel() {
            setPreferredSize(new Dimension(260, CHART_HEIGHT));
        }

        public void setData(List<Student> students) {
            this.students = students;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (students == null || students.isEmpty()) {
                g.setColor(new Color(160, 170, 180));
                g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
                g.drawString("暂无数据", getWidth() / 2 - 30, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int chartW = students.size() * (BAR_WIDTH + BAR_GAP) + BAR_GAP;
            int startX = Math.max(10, (getWidth() - chartW) / 2);
            int baselineY = getHeight() - 35;

            // 标题
            g2.setColor(new Color(80, 90, 110));
            g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            g2.drawString("答对率 (%)", startX, 16);

            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                double rate = s.getAnswerRate();
                int barH = (int) (rate * MAX_BAR_HEIGHT);
                int x = startX + i * (BAR_WIDTH + BAR_GAP);
                int y = baselineY - barH;

                // 柱
                Color barColor = rate >= 0.5 ? COLOR_BAR : COLOR_BAR_LOW;
                g2.setColor(barColor);
                g2.fillRoundRect(x, y, BAR_WIDTH, barH, 6, 6);

                // 百分比数字
                g2.setColor(new Color(60, 70, 85));
                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
                String pct = String.format("%.0f%%", rate * 100);
                int sw = g2.getFontMetrics().stringWidth(pct);
                g2.drawString(pct, x + (BAR_WIDTH - sw) / 2, y - 4);

                // 姓名（截断）
                g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
                String name = s.getName().length() > 3
                        ? s.getName().substring(0, 3) + ".." : s.getName();
                int nw = g2.getFontMetrics().stringWidth(name);
                g2.drawString(name, x + (BAR_WIDTH - nw) / 2, baselineY + 16);
            }
        }
    }
}
