package de.ewoche.packager.settings;

import de.ewoche.packager.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class RoboPackagerConfig {
    private static final String KEY_ROBOCODE_INSTALL_DIR = "robocode.install_dir";
    private static final String KEY_ROBOCODE_VERSION = "robocode.version";
    private static final String KEY_ROBOCODE_ADDITIONAL_LIBS = "robocode.additional_libs";
    private static final String KEY_BUILD_DIR = "build_dir";
    private static final String KEY_TARGET_FILE = "target_dir";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_WEB_PAGE = "web_page";
    private static final String KEY_ECLIPSE_WORKSPACE = "eclipse_workspace";

    private static final String CONFIG_NAME = "config.properties";
    private static String DEFAULT_ROBOCODE_PATH = null;
    private static Path RUN_DIR;

    static {
        try {
            RUN_DIR = Paths.get(RoboPackagerConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            RUN_DIR = null;
            System.err.println("Failed to resolve Run dir");
        }

    }

    private final Properties underlyingProps;

    public RoboPackagerConfig() {
        Properties defaultConfig = new Properties();
        try {
            defaultConfig.load(getClass().getResourceAsStream("/" + CONFIG_NAME));
        } catch (IOException e) {
            System.err.println("Could not load default config from " + CONFIG_NAME + "!");
            e.printStackTrace();
        }
        underlyingProps = new Properties(defaultConfig);
        Path configPath = getConfigPath();
        if (configPath != null && Files.exists(configPath)) {
            try (InputStream stream = Files.newInputStream(configPath)) {
                underlyingProps.load(stream);
            } catch (IOException e) {
                System.err.println("Could not load config from " + configPath + "!");
                e.printStackTrace();
            }
        } else {
            System.out.println("No config found at " + configPath);
        }
    }

    public Path getRobocodeInstallDir() {
        String value = underlyingProps.getProperty(KEY_ROBOCODE_INSTALL_DIR, "");
        if (value == null || value.isEmpty())
            value = getDefaultRobocodePath();
        return Paths.get(value);
    }

    public void setRobocodeInstallDir(Path installDir) {
        underlyingProps.put(KEY_ROBOCODE_INSTALL_DIR, Objects.requireNonNull(installDir, "Illegal (null) Robocode-Install-Dir!").toString());
    }

    public Path[] getAdditionalLibs() {
        return Arrays.stream(underlyingProps.getProperty(KEY_ROBOCODE_ADDITIONAL_LIBS, "").split(";")).filter(s -> ! s.isEmpty()).map(Paths::get).toArray(Path[]::new);
    }

    public void setAdditionalLibs(Path[] libs) {
        underlyingProps.setProperty(KEY_ROBOCODE_ADDITIONAL_LIBS, Arrays.stream(libs).map(Path::toString).collect(Collectors.joining(";")));
    }

    public boolean hasBuildDir() {
        return underlyingProps.containsKey(KEY_BUILD_DIR);
    }

    public Path getBuildDir() {
        return Paths.get(underlyingProps.getProperty(KEY_BUILD_DIR, ""));
    }

    public void setBuildDir(Path buildDir) {
        underlyingProps.setProperty(KEY_BUILD_DIR, Objects.requireNonNull(buildDir, "Illegal (null) Build-Dir!").toString());
    }

    public void setEclipseWorkspace(Path workspace) {
        underlyingProps.setProperty(KEY_ECLIPSE_WORKSPACE, Objects.requireNonNull(workspace, "Illegal (null) Workspace!").toString());
    }

    public Path getEclipseWorkspace() {
        return Paths.get(underlyingProps.getProperty(KEY_ECLIPSE_WORKSPACE, ""));
    }

    public Path getTargetFile() {
        return Paths.get(underlyingProps.getProperty(KEY_TARGET_FILE, ""));
    }

    public void setTargetFile(Path targetFile) {
        underlyingProps.setProperty(KEY_TARGET_FILE, Objects.requireNonNull(targetFile, "Illegal (null) Target jar file!").toString());
    }

    public String getAuthor(String defaultValue) {
        return underlyingProps.getProperty(KEY_AUTHOR, defaultValue);
    }

    public String getAuthor() {
        return getAuthor("");
    }

    public void setAuthor(String author) {
        underlyingProps.setProperty(KEY_AUTHOR, author);
    }

    public String getRobocodeVersion() {
        return underlyingProps.getProperty(KEY_ROBOCODE_VERSION, "");
    }

    public void setRobocodeVersion(String robocodeVersion) {
        underlyingProps.setProperty(KEY_ROBOCODE_VERSION, robocodeVersion);
    }

    public String getWebPage() {
        return underlyingProps.getProperty(KEY_WEB_PAGE, "");
    }

    public void setWebPage(String webPage) {
        underlyingProps.setProperty(KEY_WEB_PAGE, webPage);
    }

    public void save() {
        Path configPath = getConfigPath();
        if (configPath != null) {
            if (! Files.exists(configPath)) {
                try {
                    Files.createFile(configPath);
                } catch (IOException e) {
                    System.err.println("Unable to create config save File at " + configPath + "! Aborting save");
                    e.printStackTrace();
                    return;
                }
            }
            try (OutputStream stream = Files.newOutputStream(configPath)) {
                underlyingProps.store(stream, null);
            } catch (IOException e) {
                System.err.println("Unable to write config save File at " + configPath + "!");
                e.printStackTrace();
            }
        }
    }

    private Path getConfigPath() {
        return RUN_DIR != null ? RUN_DIR.resolve(CONFIG_NAME) : null;
    }

    private static String getDefaultRobocodePath() {
        if (DEFAULT_ROBOCODE_PATH == null) {
            if (System.getProperty("os.name").contains("Windows")) { //contains, in order to hopefully catch 8, 7, Vista or even XP
                DEFAULT_ROBOCODE_PATH = "C:/Robocode";
            } else {
                String home = System.getenv().getOrDefault("HOME", "/home/");
                if (Utils.getHostName().contains("tux")) {
                    String robocodeContainer = home + "/.local/share/robocode/";
                    DEFAULT_ROBOCODE_PATH = findLatestVersion(robocodeContainer);
                } else
                    DEFAULT_ROBOCODE_PATH = home + "/robocode";
            }
        }
        return DEFAULT_ROBOCODE_PATH;
    }

    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9]+[.])*[0-9]+");

    private static String findLatestVersion(String versionDir) {
        Path dir = Paths.get(versionDir).toAbsolutePath();
        if (! Files.exists(dir) || ! Files.isDirectory(dir))
            return versionDir;
        Deque<Path> subDirs = new LinkedList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    subDirs.add(dir);
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to scan " + versionDir + " for robocode installations!");
            e.printStackTrace();
        }
        return subDirs.stream()
                .map(Path::toAbsolutePath)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(s -> VERSION_PATTERN.matcher(s).matches())
                .map(ComparableRobocodeVersion::new)
                .max(Comparator.naturalOrder())
                .map(ComparableRobocodeVersion::toString)
                .map(s -> versionDir + File.pathSeparator + s)
                .orElse(versionDir);
    }
}
