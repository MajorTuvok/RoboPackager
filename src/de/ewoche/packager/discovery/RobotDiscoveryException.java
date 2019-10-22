package de.ewoche.packager.discovery;

import java.io.IOException;
import java.nio.file.Path;

public class RobotDiscoveryException extends Exception {
    public RobotDiscoveryException(String message) {
        super(message);
    }

    public RobotDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RobotDiscoveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static final class VisitFailedException extends RobotDiscoveryException {
        private final Path dir;

        public VisitFailedException(IOException cause, Path dir) {
            super("Failed to visit Build directory " + dir + "!", cause);
            this.dir = dir;
        }

        public Path getDir() {
            return dir;
        }
    }

    public static final class RobotClassNotFoundException extends RobotDiscoveryException {
        public RobotClassNotFoundException(ClassNotFoundException cause) {
            super("Unable to load robocode.Robot class. This indicates a broken Classpath!", cause);
        }
    }
}
