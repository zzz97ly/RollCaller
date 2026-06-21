package scut.ui;

import scut.dao.StudentDAO;
import scut.entity.Student;
import scut.service.ImportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入学生面板
 * <p>
 * 提供三种导入学生方式：
 * 1. 从 Excel 文件导入
 * 2. 从文本文件导入
 * 3. 手动批量添加（输入框 + 添加按钮）
 * <p>
 * 底部以表格展示已导入的学生列表
 *
 * @author zzz97ly
 */
public class ImportPanel extends JPanel {

    /** 背景色 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 手动添加输入框提示文字 */
    private static final String INPUT_PLACEHOLDER = "输入学生姓名，多个用逗号或换行分隔";

    /** 手动添加输入框列数 */
    private static final int INPUT_COLUMNS = 35;

    /** 手动添加按钮文字 */
    private static final String BTN_ADD_TEXT = "添加";

    /** 表格列名 */
    private static final String[] TABLE_COLUMNS = {"序号", "姓名"};

    /** 状态消息颜色（成功） */
    private static final Color COLOR_SUCCESS = new Color(46, 125, 50);
    /** 状态消息颜色（失败） */
    private static final Color COLOR_ERROR = new Color(198, 40, 40);

    /** 数据访问层 */
    private final StudentDAO studentDAO;
    /** 导入服务 */
    private final ImportService importService;
    /** 表格数据模型 */
    private final DefaultTableModel tableModel;
    /** 学生列表表格 */
    private final JTable studentTable;
    /** 状态消息标签 */
    private final JLabel statusLabel;
    /** 学生数量标签 */
    private final JLabel countLabel;
    /** 手动输入框 */
    private final JTextField manualInput;

    /** 每行对应的数据库 ID（与表格行一一对应） */
    private final List<Integer> studentIds;
    public ImportPanel() {
        this.studentDAO = StudentDAO.getInstance();
        this.importService = new ImportService();
        this.studentIds = new ArrayList<>();

        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // ===== 顶部：标题 =====
        JLabel titleLabel = new JLabel("导入学生");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 70, 85));
        add(titleLabel, BorderLayout.NORTH);

        // ===== 中间 =====
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // --- 导入按钮行 ---
        JPanel buttonGroup = new JPanel(new GridBagLayout());
        buttonGroup.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        buttonGroup.add(makeCard("📥", "从 Excel 导入", "支持 .xlsx 格式", new Color(33, 150, 83),
                e -> importFromFile("xlsx")), gbc);
        gbc.gridx = 1;
        buttonGroup.add(makeCard("📄", "从文本文件导入", "每行一个姓名", new Color(66, 133, 244),
                e -> importFromFile("txt")), gbc);
        gbc.gridx = 2;
        buttonGroup.add(makeCard("✏️", "手动添加学生", "弹窗输入姓名", new Color(251, 140, 0),
                e -> showManualDialog()), gbc);

        centerPanel.add(buttonGroup, BorderLayout.NORTH);

        // --- 手动输入区 ---
        JPanel manualPanel = new JPanel(new BorderLayout(8, 0));
        manualPanel.setOpaque(false);
        manualPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        manualInput = new JTextField(INPUT_COLUMNS);
        manualInput.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        manualInput.setForeground(new Color(50, 55, 65));
        manualInput.setBackground(Color.WHITE);
        manualInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 110, 125), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton addBtn = new JButton(BTN_ADD_TEXT);
        addBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        addBtn.setBackground(new Color(251, 140, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(90, 42));
        addBtn.addActionListener(e -> manualAdd());

        manualPanel.add(new JLabel("快捷输入（逗号或换行分隔多个姓名）："), BorderLayout.NORTH);
        manualPanel.add(manualInput, BorderLayout.CENTER);
        manualPanel.add(addBtn, BorderLayout.EAST);
        centerPanel.add(manualPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // ===== 底部：学生列表表格 =====
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);

        // 标题行 + 状态消息
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        countLabel = new JLabel("已导入学生列表（0 人）");
        countLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        countLabel.setForeground(new Color(100, 110, 120));
        headerRow.add(countLabel, BorderLayout.WEST);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        headerRow.add(statusLabel, BorderLayout.EAST);
        bottomPanel.add(headerRow, BorderLayout.NORTH);

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
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(640);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(700, 220));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225)));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // 删除选中按钮
        JButton deleteBtn = new JButton("删除选中");
        deleteBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        deleteBtn.addActionListener(e -> deleteSelected());
        bottomPanel.add(deleteBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // 初次加载
        refreshTable();
    }

    // ======================== 功能操作 ========================

    private void importFromFile(String type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "xlsx".equals(type) ? "Excel 文件" : "文本文件", type));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try {
            List<Student> imported = importService.importFromFile(file.getAbsolutePath());
            showStatus("成功导入 " + imported.size() + " 名学生", true);
            refreshTable();
        } catch (Exception ex) {
            showStatus("导入失败：" + ex.getMessage(), false);
        }
    }

    private void manualAdd() {
        String text = manualInput.getText().trim();
        if (text.isEmpty()) {
            showStatus("请输入学生姓名", false);
            return;
        }
        doAddStudents(text);
        manualInput.setText("");
    }

    /**
     * 弹出对话框，让用户输入多个学生姓名（每行一个）
     */
    private void showManualDialog() {
        JTextArea area = new JTextArea(10, 25);
        area.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(400, 220));

        int result = JOptionPane.showConfirmDialog(
                this, sp, "手动添加学生 — 每行一个姓名",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        String text = area.getText().trim();
        if (text.isEmpty()) {
            showStatus("未输入任何姓名", false);
            return;
        }
        doAddStudents(text);
    }

    /**
     * 执行添加学生（从文本解析姓名）
     */
    private void doAddStudents(String text) {
        String[] names = text.split("[，,\n]+");
        try {
            List<Student> added = importService.batchAddStudents(names);
            showStatus("成功添加 " + added.size() + " 人"
                    + (names.length - added.size() > 0
                        ? "（跳过 " + (names.length - added.size()) + " 个重复）" : ""),
                    true);
            refreshTable();
        } catch (Exception ex) {
            showStatus("添加失败：" + ex.getMessage(), false);
        }
    }

    private void deleteSelected() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            showStatus("请先选择要删除的学生", false);
            return;
        }
        int id = studentIds.get(row);
        studentDAO.delete(id);
        showStatus("已删除", true);
        refreshTable();
    }

    // ======================== UI 辅助 ========================

    private void refreshTable() {
        tableModel.setRowCount(0);
        studentIds.clear();
        List<Student> all = studentDAO.findAll();
        int index = 1;
        for (Student s : all) {
            tableModel.addRow(new Object[]{index++, s.getName()});
            studentIds.add(s.getId());
        }
        countLabel.setText("已导入学生列表（" + all.size() + " 人）");
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? COLOR_SUCCESS : COLOR_ERROR);
        // 3 秒后清除
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }

    private JPanel makeCard(String icon, String title, String desc, Color color,
                            java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 228), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setPreferredSize(new Dimension(180, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        titleLbl.setForeground(color);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        descLbl.setForeground(new Color(140, 150, 165));
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);
        card.add(Box.createVerticalGlue());

        return card;
    }
}
