package de.ewoche.packager.discovery;

import de.ewoche.packager.discovery.RobotDiscoveryException.RobotClassNotFoundException;
import de.ewoche.packager.discovery.RobotDiscoveryException.VisitFailedException;
import de.ewoche.packager.layout.Constants;
import de.ewoche.packager.settings.RoboPackagerConfig;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class RobotDiscoverer {
    private static final String DISCOVER_LOADER_NAME = "Robot-Discovery-Classloader";
    private final RoboPackagerConfig config;

    public RobotDiscoverer(RoboPackagerConfig config) {
        this.config = config;
    }

    public List<DiscoveredRobot> discover() throws RobotDiscoveryException {
        Path buildDir = config.getBuildDir();
        URLClassLoader robotLoader = createRobotLoader();
        try {
            Class<?> robotClass = robotLoader.loadClass("robocode.Robot"); ;
            RobotTestingDirectoryVisitor visitor = new RobotTestingDirectoryVisitor(robotLoader, buildDir, robotClass);
            Files.walkFileTree(buildDir, visitor);
            return visitor.getRobots();
        } catch (IOException e) {
            throw new VisitFailedException(e, buildDir);
        } catch (ClassNotFoundException e) {
            throw new RobotClassNotFoundException(e);
        }
    }

    private URLClassLoader createRobotLoader() {
        Path buildDir = config.getBuildDir();
        Path robocodeLib = config.getRobocodeInstallDir().resolve(Constants.ROBOCODE_REL_LIB);
        Path[] otherLibs = config.getAdditionalLibs();
        URL[] urls = Stream.concat(Arrays.stream(otherLibs), Stream.of(robocodeLib, buildDir)).map(Path::toAbsolutePath).map(Path::toUri).map((URI uri) -> {
            try {
                return uri.toURL();
            } catch (MalformedURLException e) {
                System.err.println("Failed to construct URL for " + uri + "! This will not be added to the Classpath and may cause Problems further down the line!");
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toArray(URL[]::new);
        return new URLClassLoader(DISCOVER_LOADER_NAME, urls, getClass().getClassLoader());
    }

    private static final class RobotTestingDirectoryVisitor extends SimpleFileVisitor<Path> {
        private final ClassLoader robotLoader;
        private final Path searchRoot;
        private final Class<?> robotClass;
        private final List<DiscoveredRobot> robots;

        public RobotTestingDirectoryVisitor(ClassLoader robotLoader, Path searchRoot, Class<?> robotClass) throws ClassNotFoundException {
            this.robotLoader = robotLoader;
            this.searchRoot = searchRoot;
            this.robotClass = robotClass;
            this.robots = new ArrayList<>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (attrs.isDirectory())
                throw new IllegalArgumentException("Expected only files!");
            if (file.toString().endsWith(".class")) {
                String binaryName = getBinaryClassName(file);
                Class<?> clazz;
                try {
                    clazz = robotLoader.loadClass(binaryName);
                } catch (ClassNotFoundException e) {
                    clazz = null;
                }
                if (clazz == null) {
                    System.err.println("Failed to load class " + binaryName + " from file " + file + " in " + searchRoot + "! Skipping!");
                    return FileVisitResult.CONTINUE;
                }
                if (robotClass.isAssignableFrom(clazz) && ! Modifier.isAbstract(clazz.getModifiers())) {
                    System.out.println("Found Robot: " + clazz.getName());
                    robots.add(createDiscovered(file, clazz.getName()));
                } else {
                    System.out.println("Skipping non Robot-Class: " + clazz.getName());
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        private String getBinaryClassName(Path file) {
            StringBuilder builder = new StringBuilder();
            builder.append(file.getFileName().toString().replace(".class", ""));
            while (! file.getParent().equals(searchRoot)) {
                builder.insert(0, file.getParent().getFileName() + ".");
                file = file.getParent();
            }
            return builder.toString();
        }

        private DiscoveredRobot createDiscovered(Path file, String name) {
            Path properties = file.getParent().resolve(file.getFileName().toString().replaceAll(".class", ".properties"));
            if (Files.exists(properties) && ! Files.isDirectory(properties))
                return new DiscoveredRobot(name, file, properties);
            return new DiscoveredRobot(name, file, null);
        }

        private List<DiscoveredRobot> getRobots() {
            return robots;
        }
    }
}
