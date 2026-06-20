package scut.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 课堂点名系统 - 主程序入口
 * <p>
 * 使用 CardLayout 管理三个功能视图：
 * 导入学生、点名、统计展示
 * 底部提供三个按钮进行视图切换
 *
 * @author zzz97ly
 */
public class MainApp extends JFrame {

    /** 窗口标题 */
    private static final String TITLE = "课堂点名系统";

    /** 窗口宽度 */
    private static final int WINDOW_WIDTH = 800;

    /** 窗口高度 */
    private static final int WINDOW_HEIGHT = 600;

    /** 底部按钮面板高度 */
    private static final int BUTTON_PANEL_HEIGHT = 50;

    /** 按钮圆角大小 */
    private static final int BUTTON_ARC = 12;

    /** 主题色 - 柔和蓝 */
    private static final Color COLOR_PRIMARY = new Color(70, 130, 180);

    /** 背景色 - 浅灰白 */
    private static final Color COLOR_BG = new Color(245, 248, 252);

    /** 卡片布局的面板，用于切换视图 */
    private final JPanel cardPanel;

    /** 卡片布局管理器 */
    private final CardLayout cardLayout;

    /** 三个功能面板 */
    private final ImportPanel importPanel;
    private final RollCallPanel rollCallPanel;
    private final StatisticsPanel statisticsPanel;

    /** 当前激活的按钮，用于高亮显示 */
    private final JButton btnImport;
    private final JButton btnRollCall;
    private final JButton btnStatistics;

    /**
     * 构造主窗口，初始化所有界面组件
     */
    public MainApp() {
        // ---- 窗口基本设置 ----
        setTitle(TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---- 创建功能面板 ----
        importPanel = new ImportPanel();
        rollCallPanel = new RollCallPanel();
        statisticsPanel = new StatisticsPanel();

        // ---- 中间区域：CardLayout 切换视图 ----
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BG);
        cardPanel.add(importPanel, "import");
        cardPanel.add(rollCallPanel, "rollcall");
        cardPanel.add(statisticsPanel, "statistics");
        add(cardPanel, BorderLayout.CENTER);

        // ---- 底部按钮栏 ----
        btnImport = createNavButton("📥 导入学生", "import");
        btnRollCall = createNavButton("🎯 点名", "rollcall");
        btnStatistics = createNavButton("📊 统计", "statistics");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BUTTON_PANEL_HEIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 8));
        buttonPanel.add(btnImport);
        buttonPanel.add(btnRollCall);
        buttonPanel.add(btnStatistics);
        add(buttonPanel, BorderLayout.SOUTH);

        // 默认显示导入界面，高亮对应按钮
        setActiveButton(btnImport);
    }

    /**
     * 创建底部导航按钮
     *
     * @param text  按钮文字
     * @param panelName 点击后切换到的面板名称
     * @return 配置好的按钮
     */
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(COLOR_PRIMARY.darker());
                } else if (getBackground() == COLOR_PRIMARY) {
                    g2d.setColor(COLOR_PRIMARY);
                } else {
                    g2d.setColor(new Color(235, 240, 245));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_ARC, BUTTON_ARC);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 34));

        button.addActionListener(e -> {
            cardLayout.show(cardPanel, panelName);
            setActiveButton(button);
        });

        return button;
    }

    /**
     * 设置当前选中的按钮高亮，并将其他按钮恢复默认样式
     *
     * @param activeButton 当前激活的按钮
     */
    private void setActiveButton(JButton activeButton) {
        JButton[] buttons = {btnImport, btnRollCall, btnStatistics};
        for (JButton btn : buttons) {
            if (btn == activeButton) {
                btn.setBackground(COLOR_PRIMARY);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(235, 240, 245));
                btn.setForeground(new Color(90, 100, 115));
            }
        }
    }

    /**
     * 程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // 若设置失败则使用默认外观
        }

        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
