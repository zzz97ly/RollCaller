package scut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import scut.service.StatisticsService;

/**
 * 统计控制器 — 处理统计展示相关的 Web 请求
 */
@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statsService;

    /** 统计页面 */
    @GetMapping("/stats")
    public String page(Model model) {
        model.addAttribute("students", statsService.getAllStats());
        model.addAttribute("summary", statsService.getSummary());
        model.addAttribute("rankCorrect", statsService.getRankByCorrect());
        return "stats";
    }
}
