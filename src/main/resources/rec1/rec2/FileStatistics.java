package ru.sergeinikulin.fileStatistic;

public class FileStatistics {
    private long fileCount;
    private long totalSize;
    private long totalLines;
    private long nonEmptyLines;
    private long commentLines;

    FileStatistics(long fileCount, long totalSize, long totalLines, long nonEmptyLines, long commentLines) {
        this.fileCount = fileCount;
        this.totalSize = totalSize;
        this.totalLines = totalLines;
        this.nonEmptyLines = nonEmptyLines;
        this.commentLines = commentLines;
    }

    FileStatistics merge(FileStatistics other) {
        return new FileStatistics(
                this.fileCount + other.fileCount,
                this.totalSize + other.totalSize,
                this.totalLines + other.totalLines,
                this.nonEmptyLines + other.nonEmptyLines,
                this.commentLines + other.commentLines
        );
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(long totalLines) {
        this.totalLines = totalLines;
    }

    public long getNonEmptyLines() {
        return nonEmptyLines;
    }

    public void setNonEmptyLines(long nonEmptyLines) {
        this.nonEmptyLines = nonEmptyLines;
    }

    public long getCommentLines() {
        return commentLines;
    }

    public void setCommentLines(long commentLines) {
        this.commentLines = commentLines;
    }
}
