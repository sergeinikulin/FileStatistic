package ru.sergeinikulin.fileStatistic;

import java.util.Set;

public class Config {

    private String path;
    private boolean recursive;
    private int maxDepth;
    private int threadCount;
    private Set<String> includeExt;
    private Set<String> excludeExt;
    private boolean useGitIgnore;
    private OutputFormat outputFormat;

    public Config(String path, boolean recursive, int maxDepth, int threadCount, Set<String> includeExt, Set<String> excludeExt, boolean useGitIgnore, OutputFormat outputFormat) {
        this.path = path;
        this.recursive = recursive;
        this.maxDepth = maxDepth;
        this.threadCount = threadCount;
        this.includeExt = includeExt;
        this.excludeExt = excludeExt;
        this.useGitIgnore = useGitIgnore;
        this.outputFormat = outputFormat;
    }

    public Config() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public Set<String> getIncludeExt() {
        return includeExt;
    }

    public void setIncludeExt(Set<String> includeExt) {
        this.includeExt = includeExt;
    }

    public Set<String> getExcludeExt() {
        return excludeExt;
    }

    public void setExcludeExt(Set<String> excludeExt) {
        this.excludeExt = excludeExt;
    }

    public boolean isUseGitIgnore() {
        return useGitIgnore;
    }

    public void setUseGitIgnore(boolean useGitIgnore) {
        this.useGitIgnore = useGitIgnore;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }
}
