package scut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scut.entity.Student;
import scut.service.RollCallService;

/**
 * 点名控制器 — 处理点名相关的 Web 请求
 */
@Controller
public class RollCallController {

    @Autowired
    private RollCallService rollCallService;

    /** 点名页面 */
    @GetMapping("/rollcall")
    public String page(Model model) {
        Student current = rollCallService.getCurrentStudent();
        model.addAttribute("current", current);
        model.addAttribute("streak", rollCallService.getFailStreak());
        model.addAttribute("backup", rollCallService.isBackupMode());
        model.addAttribute("maxStreak", RollCallService.MAX_FAIL_STREAK);
        return "rollcall";
    }

    /** 开始点名 */
    @PostMapping("/rollcall/next")
    public String next(RedirectAttributes redirect) {
        try {
            Student s = rollCallService.selectNextStudent();
            redirect.addFlashAttribute("msg", "选中：" + s.getName());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rollcall";
    }

    /** 答对 */
    @PostMapping("/rollcall/correct")
    public String correct(RedirectAttributes redirect) {
        Student s = rollCallService.getCurrentStudent();
        if (s == null) {
            redirect.addFlashAttribute("error", "请先点名");
            return "redirect:/rollcall";
        }
        rollCallService.markCorrect();
        redirect.addFlashAttribute("msg", s.getName() + " 答对！");
        return "redirect:/rollcall";
    }

    /** 未答对 */
    @PostMapping("/rollcall/incorrect")
    public String incorrect(RedirectAttributes redirect) {
        Student s = rollCallService.getCurrentStudent();
        if (s == null) {
            redirect.addFlashAttribute("error", "请先点名");
            return "redirect:/rollcall";
        }
        rollCallService.markIncorrect();
        redirect.addFlashAttribute("msg", s.getName() + " 未答对");
        return "redirect:/rollcall";
    }
}
