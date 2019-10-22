package de.ewoche.packager.packaging;

import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.Manifest;

public class PackingException extends Exception {
    public PackingException() {
    }

    public PackingException(String message) {
        super(message);
    }

    public PackingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackingException(Throwable cause) {
        super(cause);
    }

    public PackingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static final class FileAccessDeniedException extends PackingException {
        private final Path path;

        public FileAccessDeniedException(SecurityException cause, Path path) {
            super("Failed to access " + path + "! Cannot complete without required permissions by the installed SecurityManager!", cause);
            this.path = path;
        }

        public Path getPath() {
            return path;
        }
    }

    public static final class IllegalTargetFileException extends PackingException {
        private final Path targetFile;

        public IllegalTargetFileException(IOException cause, Path targetFile) {
            super("Failed to create target file " + targetFile + "! Cannot create a jar to a non existing target File!", cause);
            this.targetFile = targetFile;
        }

        public Path getTargetFile() {
            return targetFile;
        }
    }

    public static final class DirectoryCannotBeScannedException extends PackingException {
        private final Path sourceDirectory;

        public DirectoryCannotBeScannedException(IOException cause, Path sourceDirectory) {
            super("The Directory " + sourceDirectory + " cannot be scanned for files, therefore it is impossible to pack it into a jar-file!", cause);
            this.sourceDirectory = sourceDirectory;
        }

        public Path getSourceDirectory() {
            return sourceDirectory;
        }
    }

    public static final class JarCannotBeWrittenException extends PackingException {
        private final Path sourceDir;
        private final Path targetFile;
        private final Manifest mf;
        private final Iterable<Path> foundFiles;

        public JarCannotBeWrittenException(Throwable cause, Path sourceDir, Path targetFile, Manifest mf, Iterable<Path> foundFiles) {
            super("Failed to create jar from " + sourceDir + " to " + targetFile + "!", cause);
            this.sourceDir = sourceDir;
            this.targetFile = targetFile;
            this.mf = mf;
            this.foundFiles = foundFiles;
        }

        public Path getSourceDir() {
            return sourceDir;
        }

        public Path getTargetFile() {
            return targetFile;
        }

        public Manifest getMf() {
            return mf;
        }

        public Iterable<Path> getFoundFiles() {
            return foundFiles;
        }
    }
}
