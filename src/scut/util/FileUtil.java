package scut.util;

import scut.entity.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件存储工具类 — 以 JSON 格式将学生数据持久化到本地文件
 * <p>
 * 作为数据库存储的补充/备份方案，操作 data/ 目录下的文件。
 * 写入时覆盖整个文件，读取时返回完整列表。
 * <p>
 * JSON 格式示例：
 * <pre>
 * [
 *   {"id":1,"name":"张三","totalCalled":5,"correctCount":3},
 *   {"id":2,"name":"李四","totalCalled":2,"correctCount":1}
 * ]
 * </pre>
 *
 * @author zzz97ly
 */
public final class FileUtil {

    /** 默认数据文件路径 */
    public static final String DEFAULT_DATA_FILE = "data/students.json";

    /** 缩进空格数 */
    private static final String INDENT = "  ";

    /** 工具类禁止实例化 */
    private FileUtil() {
    }

    // ======================== JSON 序列化 ========================

    /**
     * 将学生列表保存为 JSON 文件
     *
     * @param students 学生列表
     * @param filePath 文件路径（相对于项目根目录）
     */
    public static void saveToFile(List<Student> students, String filePath) {
        String json = toJsonArray(students);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败: " + filePath, e);
        }
    }

    /**
     * 保存学生列表到默认文件 data/students.json
     *
     * @param students 学生列表
     */
    public static void saveToFile(List<Student> students) {
        saveToFile(students, DEFAULT_DATA_FILE);
    }

    // ======================== JSON 反序列化 ========================

    /**
     * 从 JSON 文件加载学生列表
     *
     * @param filePath 文件路径
     * @return 学生列表；若文件不存在则返回空列表
     */
    public static List<Student> loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        String json = readFileContent(file);
        return parseJsonArray(json);
    }

    /**
     * 从默认文件 data/students.json 加载
     *
     * @return 学生列表；文件不存在返回空列表
     */
    public static List<Student> loadFromFile() {
        return loadFromFile(DEFAULT_DATA_FILE);
    }

    // ======================== 文件基础操作 ========================

    /**
     * 读取文件全部内容为字符串
     */
    private static String readFileContent(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + file.getPath(), e);
        }
        return sb.toString();
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return true 文件存在
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 删除指定文件
     *
     * @param filePath 文件路径
     * @return true 删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    // ======================== JSON 生成（手写，避免引入第三方库） ========================

    /**
     * 将学生列表序列化为 JSON 数组字符串
     */
    private static String toJsonArray(List<Student> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < students.size(); i++) {
            sb.append(INDENT).append(toJsonObject(students.get(i)));
            if (i < students.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * 将单个 Student 序列化为 JSON 对象字符串
     */
    private static String toJsonObject(Student s) {
        return "{"
                + "\"id\":" + s.getId() + ","
                + "\"name\":\"" + escapeJson(s.getName()) + "\","
                + "\"totalCalled\":" + s.getTotalCalled() + ","
                + "\"correctCount\":" + s.getCorrectCount()
                + "}";
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     */
    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    // ======================== JSON 解析（手写，仅解析本项目固定结构） ========================

    /**
     * 解析 JSON 数组字符串为 Student 列表
     * <p>
     * 仅处理本项目的固定格式，不处理所有合法 JSON。
     */
    private static List<Student> parseJsonArray(String json) {
        List<Student> list = new ArrayList<>();
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return list;
        }
        // 去掉首尾的 [ 和 ]
        String content = json.substring(1, json.length() - 1).trim();
        if (content.isEmpty()) {
            return list;
        }

        // 按 "},{" 分割每个学生对象
        String[] parts = content.split("\\},\\s*\\{");
        for (String part : parts) {
            // 修复分割丢失的括号
            part = part.trim();
            if (!part.startsWith("{")) {
                part = "{" + part;
            }
            if (!part.endsWith("}")) {
                part = part + "}";
            }
            Student s = parseJsonObject(part);
            if (s != null) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 解析单个 JSON 对象为 Student
     */
    private static Student parseJsonObject(String json) {
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return null;
        }
        // 去掉 {}
        String content = json.substring(1, json.length() - 1);

        int id = -1;
        String name = "";
        int totalCalled = 0;
        int correctCount = 0;

        // 按 "," 分割键值对（注意值内部可能含逗号，这里我们的值不含逗号所以安全）
        String[] pairs = content.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length != 2) {
                continue;
            }
            String key = unquote(kv[0].trim());
            String val = kv[1].trim();

            switch (key) {
                case "id":
                    id = Integer.parseInt(val);
                    break;
                case "name":
                    name = unescapeJson(unquote(val));
                    break;
                case "totalCalled":
                    totalCalled = Integer.parseInt(val);
                    break;
                case "correctCount":
                    correctCount = Integer.parseInt(val);
                    break;
                default:
                    break;
            }
        }
        return new Student(id, name, totalCalled, correctCount);
    }

    /**
     * 去掉字符串两端的引号
     */
    private static String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * 反转义 JSON 字符串
     */
    private static String unescapeJson(String s) {
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
