package de.ewoche.packager.layout.gui;

import de.ewoche.packager.Main;
import de.ewoche.packager.Utils;
import de.ewoche.packager.discovery.BuildDirDiscoverer;
import de.ewoche.packager.layout.SizedFrame;
import de.ewoche.packager.settings.RoboPackagerConfig;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdvancedOptionsFrame extends SizedFrame {
    private JPanel rootPanel;
    private JTextArea helpTextArea;
    private JTextField buildDirTextField;
    private JButton reDetectButton;
    private JButton okButton;
    private JPanel buttonsPanel;
    private JPanel inputFieldsPanel;
    private JTextField additionalLibsTextField;
    private JLabel additionalLibsLabel;
    private JLabel buildDirLabel;
    private JTextField eclipseWorkspaceTextField;
    private JLabel eclipseWorkspaceLabel;
    private RoboPackagerConfig config;
    private BuildDirDiscoverer dirDiscoverer;

    public AdvancedOptionsFrame(RoboPackagerConfig config) {
        super(ADVANCED_OPTIONS_TITLE);
        this.config = Objects.requireNonNull(config);
        dirDiscoverer = new BuildDirDiscoverer(config);
        okButton.addActionListener(e -> {
            applyEclipseWorkspace();
            applyLibs();
            applyBuildDir();
            Main.popWindow();
            dispose();
        });
        reDetectButton.addActionListener(e -> {
            applyEclipseWorkspace();
            dirDiscoverer.findBuildDir().ifPresentOrElse(path -> {
                buildDirTextField.setText(path.toString());
                applyBuildDir();
            }, () -> {
                JOptionPane.showMessageDialog(this, WARNING_BUILD_DIR_NOT_DETECTED_MESSAGE, WARNING_BUILD_DIR_NOT_DETECTED, JOptionPane.WARNING_MESSAGE);
            });
        });
        buildDirTextField.setText(config.getBuildDir().toString());
        additionalLibsTextField.setText(Arrays.stream(config.getAdditionalLibs()).map(Path::toString).collect(Collectors.joining(";")));
        eclipseWorkspaceTextField.setText(config.getEclipseWorkspace().toString());
        setContentPane(rootPanel);
    }

    private void applyBuildDir() {
        String text = buildDirTextField.getText();
        Path path = Paths.get(text);
        if (! Files.exists(path)) {
            Utils.displayPathDoesNotExist(text, this);
            return;
        }
        if (Files.isDirectory(path))
            config.setBuildDir(path);
        else
            Utils.displayNotADir(text, this);
    }

    private boolean applyLibs() {
        String text = additionalLibsTextField.getText();
        String[] libs = text.split(";");
        for (String lib : libs) {
            if (lib.isEmpty())
                continue;
            Path path = Paths.get(lib);
            if (! Files.exists(path)) {
                Utils.displayPathDoesNotExist(path.toString(), this);
                return false;
            }
            if (Files.isDirectory(path)) {
                JOptionPane.showMessageDialog(this, String.format(ERROR_NOT_A_FILE_MESSAGE, lib), ERROR_NOT_A_FILE, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        config.setAdditionalLibs(Arrays.stream(libs).map(Paths::get).toArray(Path[]::new));
        return true;
    }

    private boolean applyEclipseWorkspace() {
        String text = eclipseWorkspaceTextField.getText();
        if (text.isEmpty())
            return true;
        Path path = Paths.get(text);
        if (! Utils.checkExistsAndIsDirectory(path, text, this))
            return false;
        config.setEclipseWorkspace(path);
        return true;
    }
}
