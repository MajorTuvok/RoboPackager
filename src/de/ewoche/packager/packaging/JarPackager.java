package de.ewoche.packager.packaging;

import de.ewoche.packager.packaging.PackingException.DirectoryCannotBeScannedException;
import de.ewoche.packager.packaging.PackingException.FileAccessDeniedException;
import de.ewoche.packager.packaging.PackingException.IllegalTargetFileException;
import de.ewoche.packager.packaging.PackingException.JarCannotBeWrittenException;
import de.ewoche.packager.settings.RoboPackagerConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Deque;
import java.util.LinkedList;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public final class JarPackager {
    private final RoboPackagerConfig config;

    public JarPackager(RoboPackagerConfig config) {
        this.config = config;
    }

    /**
     * @return A {@link PackagingResult} describing failure or signaling success
     * @throws PackingException if any error occurs
     */
    public PackagingResult packBuildDir() throws PackingException {
        Path buildDir = config.getBuildDir();
        Path targetFile = config.getTargetFile();
        if (! Files.exists(targetFile)) {
            try {
                Files.createFile(targetFile);
            } catch (IOException e) {
                throw new IllegalTargetFileException(e, targetFile);
            } catch (SecurityException e) {
                throw new FileAccessDeniedException(e, targetFile);
            }
        }
        JarDirectoryScanner scanner = scanDir(buildDir);
        Manifest mf = createManifest(scanner.getManifestFile());
        Deque<Path> erroredFiles = writeJar(buildDir, targetFile, mf, scanner.getFoundFiles());
        Deque<Path> undiscoveredFiles = scanner.getErroredFiles();
        Deque<Path> undiscoveredDirectories = scanner.getErroredDirs();
        return new PackagingResult(undiscoveredDirectories, undiscoveredFiles, erroredFiles);
    }

    private JarDirectoryScanner scanDir(Path buildDir) throws PackingException {
        JarDirectoryScanner scanner = new JarDirectoryScanner();
        try {
            Files.walkFileTree(buildDir, scanner);
        } catch (IOException e) {
            System.err.println("Failed to walk Build-Dir (" + buildDir + ")! Aborting jar creation!");
            throw new DirectoryCannotBeScannedException(e, buildDir);
        } catch (SecurityException e) {
            throw new FileAccessDeniedException(e, buildDir);
        }
        return scanner;
    }

    private Manifest createManifest(Path manifestFile) {
        Manifest mf = null;
        if (manifestFile != null)
            try (InputStream inStream = Files.newInputStream(manifestFile)) {
                mf = new Manifest(inStream);
            } catch (IOException | SecurityException e) {
                System.err.println("Failed to read Manifest file!");
                e.printStackTrace();
            }
        if (mf == null)
            mf = new Manifest();
        applyGlobalAttributes(mf.getMainAttributes());
        return mf;
    }

    private void applyGlobalAttributes(Attributes globalManifestAttrs) {
        if (! globalManifestAttrs.containsKey(Name.MANIFEST_VERSION))
            globalManifestAttrs.put(Name.MANIFEST_VERSION, "1.0");
        if (! globalManifestAttrs.containsKey(Name.SPECIFICATION_TITLE))
            globalManifestAttrs.put(Name.SPECIFICATION_TITLE, "robocode");
        if (! globalManifestAttrs.containsKey(Name.SPECIFICATION_VERSION))
            globalManifestAttrs.put(Name.SPECIFICATION_VERSION, config.getRobocodeVersion());
    }

    private Deque<Path> writeJar(Path sourceDir, Path targetFile, Manifest mf, Iterable<Path> foundFiles) throws PackingException {
        Deque<Path> erroredFiles = new LinkedList<>();
        try (JarOutputStream jarStream = new JarOutputStream(Files.newOutputStream(targetFile), mf)) {
            for (Path file : foundFiles) {
                try {
                    jarStream.putNextEntry(new JarEntry(getRelativeDirName(file, sourceDir)));
                    Files.copy(file, jarStream);
                } catch (IOException | SecurityException e) {
                    System.err.println("Could not copy " + file + " to the output jar.");
                    e.printStackTrace();
                    erroredFiles.addLast(file);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to create or close jar stream.");
            e.printStackTrace();
            throw new JarCannotBeWrittenException(e, sourceDir, targetFile, mf, foundFiles);
        } catch (SecurityException e) {
            throw new FileAccessDeniedException(e, targetFile);
        }
        return erroredFiles;
    }

    private String getRelativeDirName(Path file, Path searchRoot) {
        //replacement needs to happen, as zip specs use '/' for their path separators!
        return searchRoot.relativize(file).toString().replace(File.separator, "/");
    }

    private static final class JarDirectoryScanner extends SimpleFileVisitor<Path> {
        private static final String MANIFEST_FILE_NAME = "MANIFEST.MF";
        private static final String META_INF_DIR_NAME = "META-INF";
        private Path manifestFile;
        private Deque<Path> foundFiles;
        private Deque<Path> erroredDirs;
        private Deque<Path> erroredFiles;

        public JarDirectoryScanner() {
            this.manifestFile = null;
            this.foundFiles = new LinkedList<>();
            this.erroredDirs = new LinkedList<>();
            this.erroredFiles = new LinkedList<>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.getFileName().toString().equals(MANIFEST_FILE_NAME) &&
                    file.getParent().getFileName().toString().equals(META_INF_DIR_NAME)) {
                if (manifestFile != null)
                    System.err.println("Found duplicate Manifest file! Replacing " + manifestFile + " with " + file + "!");
                manifestFile = file;
            } else if (! Files.isDirectory(file)) {
                foundFiles.add(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            System.err.println("Failed to visit file " + file);
            exc.printStackTrace();
            erroredFiles.addLast(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            if (exc != null) {
                System.err.println("Failed to visit directory " + dir);
                exc.printStackTrace();
                erroredFiles.addLast(dir);
            }
            return FileVisitResult.CONTINUE;
        }

        public Path getManifestFile() {
            return manifestFile;
        }

        public Deque<Path> getFoundFiles() {
            return foundFiles;
        }

        public Deque<Path> getErroredDirs() {
            return erroredDirs;
        }

        public Deque<Path> getErroredFiles() {
            return erroredFiles;
        }
    }
}
