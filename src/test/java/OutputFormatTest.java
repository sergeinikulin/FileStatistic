import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import ru.sergeinikulin.fileStatistic.FileAnalyze;
import ru.sergeinikulin.fileStatistic.OutputFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class OutputFormatTest {

    @TempDir
    Path tempDir;

    private FileAnalyze createTestFileStats(OutputFormat format) throws IOException {
        // Создаем тестовые файлы
        Files.writeString(tempDir.resolve("test.java"),
                "// Comment\npublic class Test {\n}\n");
        Files.writeString(tempDir.resolve("script.sh"),
                "# Script\necho 'test'\n");

        return new FileAnalyze(Path.of(tempDir.toString()), false, 1, 1,
                new HashSet<>(), new HashSet<>(), false, format);
    }

    @Test
    void testPlainOutput() throws Exception {
        FileAnalyze fileAnalyze = createTestFileStats(OutputFormat.PLAIN);

        // Перехватываем вывод
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileAnalyze.analyze();
            String output = outputStream.toString();

            assertTrue(output.contains("File Statistics"));
            assertTrue(output.contains("Extension: .java"));
            assertTrue(output.contains("Extension: .sh"));
            assertTrue(output.contains("Files:"));
            assertTrue(output.contains("Size:"));
            assertTrue(output.contains("Total lines:"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testXmlOutput() throws Exception {
        FileAnalyze fileAnalyze = createTestFileStats(OutputFormat.XML);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileAnalyze.analyze();
            String output = outputStream.toString();

            assertTrue(output.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            assertTrue(output.contains("<statistics>"));
            assertTrue(output.contains("<extension"));
            assertTrue(output.contains("<files>"));
            assertTrue(output.contains("<size>"));
            assertTrue(output.contains("</statistics>"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testJsonOutput() throws Exception {
        FileAnalyze fileAnalyze = createTestFileStats(OutputFormat.JSON);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            fileAnalyze.analyze();
            String output = outputStream.toString();

            assertTrue(output.contains("{"));
            assertTrue(output.contains("\"statistics\": ["));
            assertTrue(output.contains("\"extension\":"));
            assertTrue(output.contains("\"files\":"));
            assertTrue(output.contains("\"size\":"));
            assertTrue(output.contains("}"));
        } finally {
            System.setOut(originalOut);
        }
    }
}