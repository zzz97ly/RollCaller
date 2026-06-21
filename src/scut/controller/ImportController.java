package scut.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scut.dao.StudentDAO;
import scut.entity.Student;
import scut.service.ImportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 导入控制器 — 处理学生导入相关的 Web 请求
 */
@Controller
public class ImportController {

    @Autowired
    private ImportService importService;

    @Autowired
    private StudentDAO studentDAO;

    /** 导入页面 */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("students", studentDAO.findAll());
        return "import";
    }

    /** 上传文件导入 */
    @PostMapping("/import/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         RedirectAttributes redirect) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error", "请选择文件");
            return "redirect:/";
        }
        try {
            Path tmp = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
            file.transferTo(tmp.toFile());
            List<Student> result = importService.importFromFile(tmp.toString());
            tmp.toFile().delete();
            redirect.addFlashAttribute("msg", "成功导入 " + result.size() + " 人");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "导入失败：" + e.getMessage());
        }
        return "redirect:/";
    }

    /** 手动添加 */
    @PostMapping("/import/manual")
    public String manual(@RequestParam("names") String names,
                         RedirectAttributes redirect) {
        try {
            String[] arr = names.split("[，,\n]+");
            List<Student> added = importService.batchAddStudents(arr);
            redirect.addFlashAttribute("msg", "成功添加 " + added.size() + " 人");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    /** 删除学生 */
    @GetMapping("/import/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirect) {
        studentDAO.delete(id);
        redirect.addFlashAttribute("msg", "已删除");
        return "redirect:/";
    }
}
