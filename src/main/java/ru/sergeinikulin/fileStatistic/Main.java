package ru.sergeinikulin.fileStatistic;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            Config config = parseArgs(args);
            FileAnalyze fileAnalyze = new FileAnalyze(
                    Path.of(config.getPath()), config.isRecursive(), config.getMaxDepth(), config.getThreadCount(),
                    config.getIncludeExt(), config.getExcludeExt(), config.isUseGitIgnore(), config.getOutputFormat()
            );
            fileAnalyze.analyze();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java Main <path> [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --recursive              Recursive directory traversal");
        System.out.println("  --max-depth=<number>     Maximum recursion depth");
        System.out.println("  --thread=<number>        Number of threads to use");
        System.out.println("  --include-ext=<ext1,ext2,...>  Only process files with these extensions");
        System.out.println("  --exclude-ext=<ext1,ext2,...>  Exclude files with these extensions");
        System.out.println("  --git-ignore            Respect .gitignore files");
        System.out.println("  --output=<plain|xml|json> Output format (default: plain)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main /path/to/dir");
        System.out.println("  java Main /path/to/dir --recursive --output=json");
        System.out.println("  java Main /path/to/dir --include-ext=java,txt --thread=4");
    }

    private static Config parseArgs(String[] args) {
        Config config = new Config();
        config.setPath(args[0]);
        config.setRecursive(false);
        config.setMaxDepth(Integer.MAX_VALUE);
        config.setThreadCount(1);
        config.setIncludeExt(new HashSet<>());
        config.setExcludeExt(new HashSet<>());
        config.setUseGitIgnore(false);
        config.setOutputFormat(OutputFormat.PLAIN);

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--recursive")) {
                config.setRecursive(true);
            } else if (arg.startsWith("--max-depth=")) {
                config.setMaxDepth(Integer.parseInt(arg.substring(12)));
            } else if (arg.startsWith("--thread=")) {
                config.setThreadCount(Integer.parseInt(arg.substring(9)));
            } else if (arg.startsWith("--include-ext=")) {
                String exts = arg.substring(14);
                config.getIncludeExt().addAll(Arrays.asList(exts.split(",")));
            } else if (arg.startsWith("--exclude-ext=")) {
                String exts = arg.substring(14);
                config.getExcludeExt().addAll(Arrays.asList(exts.split(",")));
            } else if (arg.equals("--git-ignore")) {
                config.setUseGitIgnore(true);
            } else if (arg.startsWith("--output=")) {
                String format = arg.substring(9).toUpperCase();
                config.setOutputFormat(OutputFormat.valueOf(format));
            }
        }

        return config;
    }
}