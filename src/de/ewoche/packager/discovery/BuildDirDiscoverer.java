package de.ewoche.packager.discovery;

import de.ewoche.packager.Utils;
import de.ewoche.packager.settings.RoboPackagerConfig;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class BuildDirDiscoverer {
    private static final String ECLIPSE_CLASSPATH_FILE = ".classpath";
    private static final String ECLIPSE_DEFAULT_BUILD_DIR = "bin";

    private static final String ROBOCODE_CONFIG = "config";
    private static final String ROBOCODE_SETTINGS = "robocode.properties";

    private static final String KEY_ROBOCODE_DEV_PATH = "robocode.options.development.path";
    private static final String KEY_ROBOCODE_DEV_PATH_EXCLUDED = "robocode.options.development.path.excluded";

    private final RoboPackagerConfig config;

    public BuildDirDiscoverer(RoboPackagerConfig config) {
        this.config = config;
    }

    public Optional<Path> findBuildDir() {
        Path robocodeInstallDir = config.getRobocodeInstallDir();
        Path robocodeConfig = robocodeInstallDir.resolve(ROBOCODE_CONFIG).resolve(ROBOCODE_SETTINGS);
        Optional<Path> res = Optional.empty();
        if (Files.exists(robocodeConfig) && ! Files.isDirectory(robocodeConfig)) {
            Properties robocodeSettings = new Properties();
            try (InputStream stream = Files.newInputStream(robocodeConfig)) {
                robocodeSettings.load(stream);
                res = findFromRobocodeConfig(robocodeSettings);
            } catch (IOException e) {
                System.err.println("Failed to read Robocode settings!");
                e.printStackTrace();
            }
            if (res.isPresent())
                return res;
        }
        return res;
    }

    private Optional<Path> findFromRobocodeConfig(Properties robocodeConfig) {
        Deque<String> validPaths = getPotentialPaths(robocodeConfig);
        return validPaths.stream()
                .map(Paths::get)
                .map(this::getBuildDirFromPotentialEclipseWorkspace)
                //it's backed by LinkedList, which reports ordered - therefore this behaves as expected
                .dropWhile(Optional::isEmpty)
                .findFirst()
                .orElseGet(Optional::empty);
    }

    private Deque<String> getPotentialPaths(Properties robocodeConfig) {
        String devPath = robocodeConfig.getProperty(KEY_ROBOCODE_DEV_PATH, "");
        if (devPath.isEmpty())
            return new LinkedList<>();
        String excluded = robocodeConfig.getProperty(KEY_ROBOCODE_DEV_PATH_EXCLUDED, "");
        List<String> devPaths = Utils.splitNoRegex(devPath, ',');
        //dump into hashset, for expected constant time contains checks
        Set<String> excludedPaths = new HashSet<>(Utils.splitNoRegex(excluded, ','));
        Deque<String> validPaths = new LinkedList<>();
        for (String s : devPaths) {
            if (! excludedPaths.contains(s))
                validPaths.addLast(s);
        }
        return validPaths;
    }

    private Optional<Path> getBuildDirFromPotentialEclipseWorkspace(Path workspace) {
        if (! Files.exists(workspace) || ! Files.isDirectory(workspace))
            return Optional.empty();
        Path buildDir = getBuildDir(workspace);
        if (Files.exists(buildDir) && Files.isDirectory(buildDir))
            return Optional.of(buildDir);
        return Optional.empty();
    }

    private Path getBuildDir(Path workspace) {
        Path defaultBuildDir = workspace.resolve(ECLIPSE_DEFAULT_BUILD_DIR);
        Path cpFile = workspace.resolve(ECLIPSE_CLASSPATH_FILE);
        if (! Files.exists(cpFile) || Files.isDirectory(cpFile))
            return defaultBuildDir;
        try {
            SAXParser parser = SAXParserFactory.newDefaultInstance().newSAXParser();
            ClasspathXMLHandler handler = new ClasspathXMLHandler(defaultBuildDir);
            parser.parse(cpFile.toFile(), handler);
            return handler.getOutDir(workspace);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Failed to parse Eclipse " + ECLIPSE_CLASSPATH_FILE + " File. Assuming default " + ECLIPSE_DEFAULT_BUILD_DIR + " output dir.");
            e.printStackTrace();
            return defaultBuildDir;
        }
    }

    private static final class ClasspathXMLHandler extends DefaultHandler {
        private static final String ENTRY_ELEMENT = "classpathentry";
        private static final String ENTRY_TYPE = "kind";
        private static final String ENTRY_OUTPUT_TYPE = "output";
        private static final String ENTRY_PATH_ATTR = "path";
        private Path outDir;

        public ClasspathXMLHandler(Path outDir) {
            this.outDir = outDir;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals(ENTRY_ELEMENT) &&
                    attributes.getValue(ENTRY_TYPE) != null &&
                    attributes.getValue(ENTRY_TYPE).equals(ENTRY_OUTPUT_TYPE) && attributes.getValue(ENTRY_PATH_ATTR) != null)
                outDir = Paths.get(attributes.getValue(ENTRY_PATH_ATTR));
        }

        public Path getOutDir(Path workspace) {
            if (! outDir.isAbsolute())
                outDir = workspace.resolve(outDir);
            return outDir;
        }
    }
}
