package de.ewoche.packager.discovery;

import java.nio.file.Path;
import java.util.Comparator;

public final class DiscoveredRobot {
    public static final Comparator<DiscoveredRobot> NAME_COMPARATOR = Comparator.comparing(DiscoveredRobot::getName);
    private final String name;
    private final Path robotFile;
    private final Path robotProperties;

    public DiscoveredRobot(String name, Path robotFile, Path robotProperties) {
        this.name = name;
        this.robotFile = robotFile;
        this.robotProperties = robotProperties;
    }

    public String getName() {
        return name;
    }

    public Path getRobotFile() {
        return robotFile;
    }

    public Path getRobotProperties() {
        return robotProperties;
    }
}
