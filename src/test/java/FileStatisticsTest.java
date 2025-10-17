import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import ru.sergeinikulin.fileStatistic.FileAnalyze;
import ru.sergeinikulin.fileStatistic.FileStatistics;
import ru.sergeinikulin.fileStatistic.OutputFormat;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

class FileStatisticsTest {

    @TempDir
    Path tempDir;

    private Path createTestFile(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return file;
    }

    private Path createSubDir(String dirName) throws IOException {
        Path subDir = tempDir.resolve(dirName);
        Files.createDirectory(subDir);
        return subDir;
    }

    @Test
    @DisplayName("Получение расширения файлов")
    void testFileExtensionExtraction() {
        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), new HashSet<>(), false,
                OutputFormat.PLAIN);

        assertEquals("java", fileAnalyze.getFileExtension("test.java"));
        assertEquals("txt", fileAnalyze.getFileExtension("file.txt"));
        assertEquals("", fileAnalyze.getFileExtension("noextension"));
        assertEquals("", fileAnalyze.getFileExtension("file."));
    }

    @Test
    @DisplayName("Получение паттерна для комментов")
    void testCommentPatternDetection() {
        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), new HashSet<>(), false,
                OutputFormat.PLAIN);

        assertNotNull(fileAnalyze.getCommentPattern("java"));
        assertNotNull(fileAnalyze.getCommentPattern("sh"));
        assertNotNull(fileAnalyze.getCommentPattern("py"));
        assertNull(fileAnalyze.getCommentPattern("txt"));
        assertNull(fileAnalyze.getCommentPattern("log"));
    }

    @Test
    @DisplayName("Анализ Java файла")
    void testFileAnalysis() throws IOException {
        // Создаем тестовый Java файл с комментариями
        String javaContent = "// This is a comment\n" +
            "public class Test {\n" +
            "    // Another comment\n" +
            "    public void method() {\n" +
            "        System.out.println(\"hello\");\n" +
            "    }\n" +
            "}\n" +
            "// Final comment\n" +
            "\n"; // Пустая строка в конце

        Path javaFile = createTestFile("Test.java", javaContent);

        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), new HashSet<>(), false,
                OutputFormat.PLAIN);

        FileStatistics stats = fileAnalyze.analyzeFile(javaFile, "java");

        assertEquals(1, stats.getFileCount());
        assertEquals(javaContent.getBytes().length, stats.getTotalSize());
        assertEquals(9, stats.getTotalLines()); // Всего строк
        assertEquals(8, stats.getNonEmptyLines()); // Не пустые строки (исключая последнюю пустую)
        assertEquals(3, stats.getCommentLines()); // Строки с комментариями
    }

    @Test
    @DisplayName("Анализ баш файла")
    void testBashFileAnalysis() throws IOException {
        String bashContent = "#!/bin/bash\n" +
                "# This is a bash comment\n" +
                "echo \"Hello World\"\n" +
                "# Another comment\n" +
                "ls -la\n" +
                "\n"; // Пустая строка

        Path bashFile = createTestFile("script.sh", bashContent);

        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), new HashSet<>(), false,
                OutputFormat.PLAIN);

        FileStatistics stats = fileAnalyze.analyzeFile(bashFile, "sh");

        assertEquals(1, stats.getFileCount());
        assertEquals(6, stats.getTotalLines());
        assertEquals(5, stats.getNonEmptyLines());
        assertEquals(3, stats.getCommentLines()); // #!/bin/bash тоже считается комментарием
    }

    @Test
    @DisplayName("Проверка суммирования статистики по файлам")
    void testStatisticsMerge() {
        FileStatistics stats1 = new FileStatistics(2, 1000, 50, 40, 10);
        FileStatistics stats2 = new FileStatistics(3, 2000, 70, 60, 15);

        FileStatistics merged = stats1.merge(stats2);

        assertEquals(5, merged.getFileCount());
        assertEquals(3000, merged.getTotalSize());
        assertEquals(120, merged.getTotalLines());
        assertEquals(100, merged.getNonEmptyLines());
        assertEquals(25, merged.getCommentLines());
    }

    @Test
    @DisplayName("Проверка определенных расширений, java и sh")
    void testFileFilteringByExtension() throws IOException {
        createTestFile("test.java", "public class Test {}");
        createTestFile("script.sh", "echo 'hello'");
        createTestFile("data.txt", "some data");
        createTestFile("config.xml", "<config></config>");

        Set<String> includeExt = new HashSet<>(Arrays.asList("java", "sh"));
        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                includeExt, new HashSet<>(), false,
                OutputFormat.PLAIN);

        List<Path> files = fileAnalyze.collectFiles();

        assertEquals(2, files.size());
        assertTrue(files.stream().anyMatch(f -> f.toString().endsWith(".java")));
        assertTrue(files.stream().anyMatch(f -> f.toString().endsWith(".sh")));
        assertFalse(files.stream().anyMatch(f -> f.toString().endsWith(".txt"))); //а тут False!
    }

    @Test
    @DisplayName("Исключение определенных расширений, txt и log")
    void testExcludeExtensions() throws IOException {
        createTestFile("test.java", "public class Test {}");
        createTestFile("script.sh", "echo 'hello'");
        createTestFile("data.txt", "some data");

        Set<String> excludeExt = new HashSet<>(Arrays.asList("txt", "log"));
        FileAnalyze fileAnalyze = new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), excludeExt, false,
                OutputFormat.PLAIN);

        List<Path> files = fileAnalyze.collectFiles();

        assertEquals(2, files.size());
        assertTrue(files.stream().anyMatch(f -> f.toString().endsWith(".java")));
        assertTrue(files.stream().anyMatch(f -> f.toString().endsWith(".sh")));
        assertFalse(files.stream().anyMatch(f -> f.toString().endsWith(".txt")));
    }
}