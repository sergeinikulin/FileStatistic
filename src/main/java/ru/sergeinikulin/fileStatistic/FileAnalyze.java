package ru.sergeinikulin.fileStatistic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class FileAnalyze {

    private final Path rootPath;
    private final boolean recursive;
    private final int maxDepth;
    private final int threadCount;
    private final Set<String> includeExt;
    private final Set<String> excludeExt;
    private final boolean useGitIgnore;
    private final OutputFormat outputFormat;

    private final Set<Path> gitIgnorePaths = new HashSet<>();
    private final Map<String, FileStatistics> statistics = new ConcurrentHashMap<>();

    // Регулярные выражения для комментариев
    private static final Pattern JAVA_COMMENT = Pattern.compile("^\\s*//");
    private static final Pattern BASH_COMMENT = Pattern.compile("^\\s*#");

    public FileAnalyze(Path rootPath, boolean recursive, int maxDepth, int threadCount, Set<String> includeExt, Set<String> excludeExt, boolean useGitIgnore, OutputFormat outputFormat) {
        this.rootPath = rootPath;
        this.recursive = recursive;
        this.maxDepth = maxDepth;
        this.threadCount = threadCount;
        this.includeExt = includeExt;
        this.excludeExt = excludeExt;
        this.useGitIgnore = useGitIgnore;
        this.outputFormat = outputFormat;
    }

    public void analyze() throws IOException, InterruptedException {
        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("Path does not exist or is not a directory: " + rootPath);
        }

        if (useGitIgnore) {
            loadGitIgnore();
        }

        List<Path> files = collectFiles();
        processFiles(files);
        printStatistics();
    }

    private void loadGitIgnore() throws IOException {
        Path gitIgnoreFile = rootPath.resolve(".gitignore");
        if (Files.exists(gitIgnoreFile)) {
            List<String> lines = Files.readAllLines(gitIgnoreFile);
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    gitIgnorePaths.add(rootPath.resolve(line));
                }
            }
        }
    }

    private void printStatistics() {
        switch (outputFormat) {
            case PLAIN:
                printPlain();
                break;
            case XML:
                printXml();
                break;
            case JSON:
                printJson();
                break;
        }
    }

    private void printPlain() {
        System.out.println("File Statistics:");
        System.out.println("================\n");

        for (Map.Entry<String, FileStatistics> entry : statistics.entrySet()) {
            String ext = entry.getKey().isEmpty() ? "no extension" : entry.getKey();
            FileStatistics stats = entry.getValue();

            System.out.println("Extension: ." + ext);
            System.out.println("  Files: " + stats.getFileCount());
            System.out.println("  Size: " + stats.getTotalSize() + " bytes");
            System.out.println("  Total lines: " + stats.getTotalLines());
            System.out.println("  Non-empty lines: " + stats.getNonEmptyLines());
            System.out.println("  Comment lines: " + stats.getCommentLines());
            System.out.println();
        }
    }

    private void printXml() {
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        System.out.println("<statistics>");

        for (Map.Entry<String, FileStatistics> entry : statistics.entrySet()) {
            String ext = entry.getKey().isEmpty() ? "no extension" : entry.getKey();
            FileStatistics stats = entry.getValue();

            System.out.println("  <extension name=\"" + ext + "\">");
            System.out.println("    <files>" + stats.getFileCount() + "</files>");
            System.out.println("    <size>" + stats.getTotalSize() + "</size>");
            System.out.println("    <totalLines>" + stats.getTotalLines() + "</totalLines>");
            System.out.println("    <nonEmptyLines>" + stats.getNonEmptyLines() + "</nonEmptyLines>");
            System.out.println("    <commentLines>" + stats.getCommentLines() + "</commentLines>");
            System.out.println("  </extension>");
        }

        System.out.println("</statistics>");
    }

    private void printJson() {
        System.out.println("{");
        System.out.println("  \"statistics\": [");

        List<Map.Entry<String, FileStatistics>> entries = new ArrayList<>(statistics.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, FileStatistics> entry = entries.get(i);
            String ext = entry.getKey().isEmpty() ? "no extension" : entry.getKey();
            FileStatistics stats = entry.getValue();

            System.out.println("    {");
            System.out.println("      \"extension\": \"" + ext + "\",");
            System.out.println("      \"files\": " + stats.getFileCount() + ",");
            System.out.println("      \"size\": " + stats.getTotalSize() + ",");
            System.out.println("      \"totalLines\": " + stats.getTotalLines() + ",");
            System.out.println("      \"nonEmptyLines\": " + stats.getNonEmptyLines() + ",");
            System.out.println("      \"commentLines\": " + stats.getCommentLines());
            System.out.print("    }");

            if (i < entries.size() - 1) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }

        System.out.println("  ]");
        System.out.println("}");
    }


    private void processFiles(List<Path> files) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        for (Path file : files) {
            futures.add(executor.submit(() -> processFile(file)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                System.err.println("Error processing file: " + e.getCause().getMessage());
            }
        }

        executor.shutdown();
    }

    private void processFile(Path file) {
        try {
            String ext = getFileExtension(file.getFileName().toString()).toLowerCase();
            FileStatistics stats = analyzeFile(file, ext);

            statistics.merge(ext, stats, FileStatistics::merge);
        } catch (IOException e) {
            System.err.println("Error analyzing file " + file + ": " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private FileStatistics analyzeFile(Path file, String ext) throws IOException {
        long fileSize = Files.size(file);
        long totalLines = 0;
        long nonEmptyLines = 0;
        long commentLines = 0;

        Pattern commentPattern = getCommentPattern(ext);

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;

                if (!line.trim().isEmpty()) {
                    nonEmptyLines++;
                }

                if (commentPattern != null && commentPattern.matcher(line).find()) {
                    commentLines++;
                }
            }
        }

        return new FileStatistics(1, fileSize, totalLines, nonEmptyLines, commentLines);
    }

    private Pattern getCommentPattern(String ext) {
        switch (ext) {
            case "java":
            case "js":
            case "ts":
            case "cpp":
            case "c":
            case "cs":
                return JAVA_COMMENT;
            case "sh":
            case "bash":
            case "py":
            case "rb":
            case "pl":
                return BASH_COMMENT;
            default:
                return null;
        }
    }


    private List<Path> collectFiles() throws IOException {
        List<Path> files = new ArrayList<>();
        int depth = recursive ? (maxDepth > 0 ? maxDepth : Integer.MAX_VALUE) : 1;

        Files.walk(rootPath, depth)
                .filter(Files::isRegularFile)
                .filter(this::shouldProcessFile)
                .forEach(files::add);

        return files;
    }

    private boolean shouldProcessFile(Path file) {
        String fileName = file.getFileName().toString();
        String ext = getFileExtension(fileName).toLowerCase();

        // Проверка исключенных расширений
        if (!excludeExt.isEmpty() && excludeExt.contains(ext)) {
            return false;
        }

        // Проверка включенных расширений
        if (!includeExt.isEmpty() && !includeExt.contains(ext)) {
            return false;
        }

        // Проверка .gitignore
        if (useGitIgnore && isGitIgnored(file)) {
            return false;
        }

        return true;
    }

    private boolean isGitIgnored(Path file) {
        return gitIgnorePaths.stream().anyMatch(ignore -> file.startsWith(ignore));
    }

}
