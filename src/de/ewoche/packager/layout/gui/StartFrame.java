package de.ewoche.packager.layout.gui;

import de.ewoche.packager.Main;
import de.ewoche.packager.Utils;
import de.ewoche.packager.discovery.BuildDirDiscoverer;
import de.ewoche.packager.discovery.DiscoveredRobot;
import de.ewoche.packager.discovery.RobotDiscoverer;
import de.ewoche.packager.discovery.RobotDiscoveryException.RobotClassNotFoundException;
import de.ewoche.packager.discovery.RobotDiscoveryException.VisitFailedException;
import de.ewoche.packager.layout.IManagedWindow;
import de.ewoche.packager.layout.SizedFrame;
import de.ewoche.packager.settings.RoboPackagerConfig;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class StartFrame extends SizedFrame {
    private static final String JAR_FILE_ENDING = ".jar";
    private JPanel rootPanel;
    private JTextArea mainHelpTextArea;
    private JTextField installDirTextField;
    private JButton simpleContinueButton;
    private JButton advancedOptionsButton;
    private JPanel inputFieldsPanel;
    private JLabel installDirLabel;
    private JPanel buttonsPanel;
    private JTextField targetFileTextField;
    private JLabel targetFileLabel;
    private RoboPackagerConfig config;

    public StartFrame() {
        super(START_FRAME_TITLE);
        this.config = new RoboPackagerConfig();
        advancedOptionsButton.addActionListener(e -> {
            if (applyInstallDir())
                Main.pushWindow(new AdvancedOptionsFrame(config));
        });
        simpleContinueButton.addActionListener(e -> {
            if (applyInputs()) {
                try {
                    List<DiscoveredRobot> discoveredRobots = new RobotDiscoverer(config).discover();
                    if (discoveredRobots.isEmpty() &&
                            JOptionPane.showConfirmDialog(this, ERROR_NO_ROBOT_DETECTED_MESSAGE, ERROR_NO_ROBOT_DETECTED, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                        return;
                    Main.pushWindow(new SelectRobotsFrame(config, discoveredRobots));
                } catch (VisitFailedException ex) {
                    System.err.println("Failed to discover Robots!");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ERROR_ROBOT_DISCOVER_DIR_VISIT_MESSAGE, ERROR_ROBOT_DISCOVER_DIR_VISIT, JOptionPane.ERROR_MESSAGE);
                } catch (RobotClassNotFoundException ex) {
                    System.err.println("Failed to discover Robots!");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, String.format(ERROR_ROBOT_DISCOVER_NO_ROBOT_MESSAGE, config.getRobocodeInstallDir().toAbsolutePath().toString()),
                            ERROR_ROBOT_DISCOVER_NO_ROBOT, JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Failed to discover Robots!");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ERROR_GENERIC_ROBOT_DISCOVER_MESSAGE, ERROR_GENERIC_ROBOT_DISCOVER, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        installDirTextField.setText(config.getRobocodeInstallDir().toString());
        targetFileTextField.setText(config.getTargetFile().toString());
        setContentPane(rootPanel);
    }

    @Override
    public void onClosingAtop(IManagedWindow window) {
        config.save();
    }

    private boolean applyInputs() {
        if (! applyInstallDir() || ! applyTargetFile())
            return false;
        if (! detectBuildDir()) {
            JOptionPane.showMessageDialog(this, ERROR_BUILD_DIRECTORY_NOT_FOUND_MESSAGE, ERROR_BUILD_DIRECTORY_NOT_FOUND, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean applyInstallDir() {
        String text = installDirTextField.getText();
        Path path = Paths.get(text);
        if (! Utils.checkExistsAndIsDirectory(path, text, this))
            return false;
        Path robocodeLib = path.resolve(ROBOCODE_REL_LIB);
        if (! Files.exists(robocodeLib) || Files.isDirectory(robocodeLib)) {
            JOptionPane.showMessageDialog(this, String.format(ERROR_ROBOCODE_INVALID_INSTALL_MESSAGE, text), ERROR_ROBOCODE_INVALID_INSTALL, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        config.setRobocodeInstallDir(path);
        return true;
    }

    private boolean applyTargetFile() {
        String text = targetFileTextField.getText();
        if (! text.endsWith(JAR_FILE_ENDING)) {
            text += JAR_FILE_ENDING;
            targetFileTextField.setText(text);
        }
        Path path = Paths.get(text);
        if (text.equals(JAR_FILE_ENDING))
            if (JOptionPane.showConfirmDialog(this, String.format(WARNING_NO_TARGET_SPECIFIED_MESSAGE, path.toAbsolutePath().toString()),
                    WARNING_NO_TARGET_SPECIFIED, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                return false;
            else {
                path = path.toAbsolutePath(); //ensure it's written to the config
                targetFileTextField.setText(path.toString()); //and display that
            }

        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
        } catch (IOException e) {
            System.err.println("Failed to create parent directories of " + path + "!");
            e.printStackTrace();
        }
        if (Files.exists(path) &&
                JOptionPane.showConfirmDialog(this, String.format(WARNING_FILE_ALREADY_EXISTS_MESSAGE, text),
                        WARNING_FILE_ALREADY_EXISTS, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return false;
        }
        config.setTargetFile(path);
        return true;
    }

    private boolean detectBuildDir() {
        Path buildDir = config.getBuildDir();
        if (! config.hasBuildDir() || ! Files.exists(buildDir) || ! Files.isDirectory(buildDir)) {
            return new BuildDirDiscoverer(config).findBuildDir().map(path -> {
                if (Files.exists(path) && Files.isDirectory(path)) {
                    config.setBuildDir(path);
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return true;
    }
}
