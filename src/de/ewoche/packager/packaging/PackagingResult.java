package de.ewoche.packager.packaging;

import java.nio.file.Path;
import java.util.Deque;

public final class PackagingResult {
    private final Deque<Path> undiscoveredDirs;
    private final Deque<Path> undiscoveredFiles;
    private final Deque<Path> erroredFiles;
    private final boolean isSuccess;

    public PackagingResult(Deque<Path> undiscoveredDirs, Deque<Path> undiscoveredFiles, Deque<Path> erroredFiles) {
        this.undiscoveredDirs = undiscoveredDirs;
        this.undiscoveredFiles = undiscoveredFiles;
        this.erroredFiles = erroredFiles;
        this.isSuccess = undiscoveredDirs.isEmpty() && undiscoveredFiles.isEmpty() && erroredFiles.isEmpty();
    }

    public Deque<Path> getUndiscoveredDirs() {
        return undiscoveredDirs;
    }

    public Deque<Path> getUndiscoveredFiles() {
        return undiscoveredFiles;
    }

    public Deque<Path> getErroredFiles() {
        return erroredFiles;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
