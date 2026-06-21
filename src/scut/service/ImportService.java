package scut.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import scut.dao.StudentDAO;
import scut.entity.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入服务 — 负责将学生数据导入系统
 * <p>
 * 支持三种导入方式：
 * <ul>
 *   <li>从 Excel 文件导入（.xlsx，第一列为姓名）</li>
 *   <li>从文本文件导入（.txt，每行一个姓名）</li>
 *   <li>手动/批量添加</li>
 * </ul>
 * 所有导入操作通过 {@link StudentDAO} 完成双存储
 *
 * @author zzz97ly
 */
public class ImportService {

    /** Excel 支持的文件扩展名 */
    private static final String EXT_XLSX = ".xlsx";

    /** 文本文件支持的文件扩展名 */
    private static final String EXT_TXT = ".txt";

    /** Excel 中姓名所在的列索引（第 1 列 = 0） */
    private static final int NAME_COLUMN_INDEX = 0;

    /** Excel 表头行索引（跳过） */
    private static final int HEADER_ROW_INDEX = 0;

    /** POI 未找到单元格时返回 */
    private static final CellType DEFAULT_CELL_TYPE = CellType.STRING;

    private final StudentDAO studentDAO;

    /**
     * 构造导入服务，初始化数据访问层
     */
    public ImportService() {
        this.studentDAO = StudentDAO.getInstance();
    }

    // ======================== 文件导入 ========================

    /**
     * 从文件导入学生，自动识别 Excel 或文本格式
     *
     * @param filePath 文件路径
     * @return 成功导入的学生列表
     * @throws IllegalArgumentException 文件格式不支持
     */
    public List<Student> importFromFile(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(EXT_XLSX)) {
            return importFromExcel(filePath);
        } else if (lower.endsWith(EXT_TXT)) {
            return importFromText(filePath);
        }
        throw new IllegalArgumentException("不支持的文件格式，请使用 .xlsx 或 .txt 文件");
    }

    /**
     * 从 Excel 文件 (.xlsx) 导入学生
     * <p>
     * 读取第一个 Sheet，跳过表头行，取第一列作为学生姓名。
     * 遇到空行则停止读取。
     *
     * @param filePath Excel 文件路径
     * @return 成功导入的学生列表
     */
    public List<Student> importFromExcel(String filePath) {
        List<Student> imported = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel 文件没有找到 Sheet");
            }

            for (int i = HEADER_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Cell cell = row.getCell(NAME_COLUMN_INDEX, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    continue;
                }

                String name = cell.getStringCellValue().trim();
                if (name.isEmpty()) {
                    continue;
                }

                // 跳过表头行
                if (i == HEADER_ROW_INDEX && looksLikeHeader(name)) {
                    continue;
                }

                Student student = studentDAO.insert(new Student(name));
                imported.add(student);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("找不到 Excel 文件: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("读取 Excel 文件失败: " + filePath, e);
        }
        return imported;
    }

    /**
     * 从文本文件导入学生（每行一个姓名）
     * <p>
     * 自动跳过空行和首尾空白字符
     *
     * @param filePath 文本文件路径
     * @return 成功导入的学生列表
     */
    public List<Student> importFromText(String filePath) {
        List<Student> imported = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String name = line.trim();
                if (!name.isEmpty()) {
                    Student student = studentDAO.insert(new Student(name));
                    imported.add(student);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("找不到文本文件: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("读取文本文件失败: " + filePath, e);
        }

        if (imported.isEmpty()) {
            throw new RuntimeException("文本文件中没有找到有效的学生姓名");
        }
        return imported;
    }

    // ======================== 手动添加 ========================

    /**
     * 手动添加单个学生
     *
     * @param name 学生姓名
     * @return 添加后的学生对象
     * @throws IllegalArgumentException 姓名为空
     */
    public Student addStudent(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }

        // 检查重名
        Student existing = studentDAO.findByName(name.trim());
        if (existing != null) {
            throw new IllegalArgumentException("学生 '" + name + "' 已存在");
        }

        return studentDAO.insert(new Student(name.trim()));
    }

    /**
     * 批量添加学生
     *
     * @param names 姓名数组
     * @return 成功导入的学生列表
     */
    public List<Student> batchAddStudents(String[] names) {
        List<Student> imported = new ArrayList<>();
        for (String name : names) {
            if (name != null && !name.trim().isEmpty()) {
                Student existing = studentDAO.findByName(name.trim());
                if (existing == null) {
                    imported.add(studentDAO.insert(new Student(name.trim())));
                }
            }
        }
        return imported;
    }

    // ======================== 辅助方法 ========================

    /**
     * 判断单元格内容是否为表头文字
     * <p>
     * 常见表头：姓名、名字、学生姓名、Name 等
     */
    private boolean looksLikeHeader(String text) {
        String lower = text.toLowerCase();
        return lower.contains("姓名")
                || lower.contains("名字")
                || lower.contains("name")
                || lower.contains("学生")
                || "序号".equals(lower)
                || "编号".equals(lower)
                || "no".equals(lower)
                || "id".equals(lower);
    }
}
