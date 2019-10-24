package de.ewoche.packager.settings;

import de.ewoche.packager.discovery.DiscoveredRobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class RobotProperties {
    private static final String KEY_DESCRIPTION = "robot.description";
    private static final String KEY_WEB_PAGE = "robot.webpage";
    private static final String KEY_ROBOCODE_VERSION = "robocode.version";
    private static final String KEY_SOURCE_INCL = "robot.java.source.included";
    private static final String KEY_AUTHOR = "robot.author.name";
    private static final String KEY_ROBOT_CLASS = "robot.classname";
    private static final String KEY_ROBOT_NAME = "robot.name";
    private final Properties underlyingProps;
    private final DiscoveredRobot discoveredRobot;

    public RobotProperties(DiscoveredRobot robot, RoboPackagerConfig config) {
        if (Objects.requireNonNull(robot).getRobotProperties() == null) {
            robot = new DiscoveredRobot(robot.getName(), robot.getRobotFile(), Paths.get(robot.getRobotFile().toAbsolutePath().toString().replaceAll(".class", ".properties")));
        }
        this.discoveredRobot = robot;
        underlyingProps = new Properties();
        Path propertiesFile = robot.getRobotProperties();
        underlyingProps.setProperty(KEY_WEB_PAGE, config.getWebPage());
        underlyingProps.setProperty(KEY_AUTHOR, config.getAuthor("Max Mustermann"));
        underlyingProps.setProperty(KEY_ROBOCODE_VERSION, config.getRobocodeVersion());
        underlyingProps.setProperty(KEY_SOURCE_INCL, "false");
        underlyingProps.setProperty(KEY_ROBOT_CLASS, discoveredRobot.getName());
        underlyingProps.setProperty(KEY_ROBOT_NAME, discoveredRobot.getName());
        underlyingProps.setProperty(KEY_DESCRIPTION, "A robot build by someone which does something in someway.");
        if (! Files.exists(propertiesFile)) {
            try {
                Files.createFile(propertiesFile);
            } catch (IOException e) {
                System.err.println("Failed to create file " + propertiesFile + "! This may or may not cause errors further down the line!");
                e.printStackTrace();
            }
        }
        try (InputStream stream = Files.newInputStream(propertiesFile)) {
            underlyingProps.load(stream);
        } catch (IOException e) {
            System.err.println("Failed to read Robot-Properties from " + propertiesFile + "!");
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return underlyingProps.getProperty(KEY_DESCRIPTION, "");
    }

    public void setDescription(String description) {
        underlyingProps.setProperty(KEY_DESCRIPTION, description);
    }

    public String getWebPage() {
        return underlyingProps.getProperty(KEY_WEB_PAGE, "");
    }

    public void setWebPage(String webPage) {
        underlyingProps.setProperty(KEY_WEB_PAGE, webPage);
    }

    public String getRobocodeVersion() {
        return underlyingProps.getProperty(KEY_ROBOCODE_VERSION, "");
    }

    public void setRobocodeVersion(String robocodeVersion) {
        underlyingProps.setProperty(KEY_ROBOCODE_VERSION, robocodeVersion);
    }

    public String getSourceIncl() {
        return underlyingProps.getProperty(KEY_SOURCE_INCL, "false");
    }

    public void setSourceIncl(String sourceIncl) {
        underlyingProps.setProperty(KEY_SOURCE_INCL, sourceIncl);
    }

    public String getAuthor() {
        return underlyingProps.getProperty(KEY_AUTHOR, "");
    }

    public void setAuthor(String author) {
        underlyingProps.setProperty(KEY_AUTHOR, author);
    }

    public String getRobotClass() {
        return underlyingProps.getProperty(KEY_ROBOT_CLASS, "");
    }

    public void setRobotClass(String robotClass) {
        underlyingProps.setProperty(KEY_ROBOT_CLASS, robotClass);
    }

    public String getRobotName() {
        return underlyingProps.getProperty(KEY_ROBOT_NAME, "");
    }

    public void setRobotName(String robotName) {
        underlyingProps.setProperty(KEY_ROBOT_NAME, robotName);
    }

    public boolean save() {
        try (OutputStream stream = Files.newOutputStream(discoveredRobot.getRobotProperties())) {
            underlyingProps.store(stream, null);
            return true;
        } catch (IOException e) { //TODO notify user!
            System.err.println("Failed to save Robot-properties to " + discoveredRobot.getRobotProperties() + "! This will certainly cause unexpected behaviour later!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() {
        try {
            Files.deleteIfExists(discoveredRobot.getRobotProperties());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to delete Robot-Properties at " + discoveredRobot.getRobotProperties() + "! This may cause this Robot to be packaged even though it should have been skipped!");
            e.printStackTrace();
            return false;
        }
    }

    public Path getRobotPropertiesPath() {
        return discoveredRobot.getRobotProperties();
    }
}
