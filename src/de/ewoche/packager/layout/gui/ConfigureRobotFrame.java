package de.ewoche.packager.layout.gui;

import de.ewoche.packager.Main;
import de.ewoche.packager.Utils;
import de.ewoche.packager.discovery.DiscoveredRobot;
import de.ewoche.packager.layout.SizedFrame;
import de.ewoche.packager.packaging.JarPackager;
import de.ewoche.packager.packaging.PackagingResult;
import de.ewoche.packager.packaging.PackingException.DirectoryCannotBeScannedException;
import de.ewoche.packager.packaging.PackingException.FileAccessDeniedException;
import de.ewoche.packager.packaging.PackingException.IllegalTargetFileException;
import de.ewoche.packager.packaging.PackingException.JarCannotBeWrittenException;
import de.ewoche.packager.settings.RoboPackagerConfig;
import de.ewoche.packager.settings.RobotProperties;

import javax.swing.*;
import java.util.Deque;

public class ConfigureRobotFrame extends SizedFrame {
    private RoboPackagerConfig config;
    private Deque<DiscoveredRobot> remaining;
    private RobotProperties displayed;
    private JPanel rootPanel;
    private JTextArea descriptionTextArea;
    private JTextField nameTextField;
    private JTextField authorTextField;
    private JLabel authorLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JTextField webPageTextField;
    private JLabel webPageLabel;
    private JButton nextButton;
    private JButton skipButton;
    private JPanel buttonsPanel;

    public ConfigureRobotFrame(RoboPackagerConfig config, Deque<DiscoveredRobot> remaining) {
        super(String.format(CONFIGURE_ROBOT_TITLE, remaining.peekFirst() != null ? remaining.peekFirst().getName() : "Error"));
        if (remaining.isEmpty())
            throw new IllegalArgumentException("Attempted to configure Robot even though all robots have been configured already");
        this.config = config;
        this.remaining = remaining;
        this.displayed = new RobotProperties(remaining.removeFirst(), config);
        this.skipButton.addActionListener(ev -> skipRobot());
        this.nextButton.addActionListener(ev -> onNext());
        setContent();
        setContentPane(rootPanel);
    }

    private void onNext() {
        readContent();
        if (! displayed.save() && ! Utils.continueNotSaved(displayed.getRobotPropertiesPath().toString(), this))
            return;
        if (! remaining.isEmpty())
            displayNext();
        else
            packRobots();

    }

    private void displayPackagingFailure(PackagingResult result) {
        Main.pushWindow(new UnpackedFilesDialog(this, result));
    }

    private void setContent() {
        this.nameTextField.setText(displayed.getRobotName());
        this.authorTextField.setText(displayed.getAuthor());
        this.webPageTextField.setText(displayed.getWebPage());
        this.descriptionTextArea.setText(displayed.getDescription());
    }

    private void readContent() {
        displayed.setRobotName(nameTextField.getText());
        config.setAuthor(authorTextField.getText());
        displayed.setAuthor(authorTextField.getText());
        config.setWebPage(webPageTextField.getText());
        displayed.setWebPage(webPageTextField.getText());
        displayed.setDescription(descriptionTextArea.getText());
    }

    private void skipRobot() {
        if (! displayed.delete() && ! Utils.continueNotDeleted(displayed.getRobotPropertiesPath().toString(), this))
            return;
        if (! remaining.isEmpty())
            displayNext();
        else
            packRobots();
    }

    private void displayNext() {
        Main.replaceWindow(new ConfigureRobotFrame(config, remaining));
        dispose();
    }

    private void displayPackingSuccess() {
        JOptionPane.showMessageDialog(this, PACKING_SUCCESS_MESSAGE, PACKING_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
    }

    private void packRobots() {
        JarPackager packager = new JarPackager(config);
        try {
            PackagingResult result = packager.packBuildDir();
            if (! result.isSuccess()) {
                displayPackagingFailure(result);
                return;
            }
            displayPackingSuccess();
            Main.popWindow();
            dispose();
        } catch (FileAccessDeniedException e) {
            e.printStackTrace();
            Utils.displayAccessDenied(e.getPath(), this);
        } catch (IllegalTargetFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, String.format(ERROR_TARGET_FILE_INVALID_MESSAGE, e.getTargetFile()),
                    ERROR_TARGET_FILE_INVALID, JOptionPane.ERROR_MESSAGE);
        } catch (DirectoryCannotBeScannedException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, String.format(ERROR_JAR_DIR_SCAN_MESSAGE, e.getSourceDirectory()),
                    ERROR_JAR_DIR_SCAN, JOptionPane.ERROR_MESSAGE);
        } catch (JarCannotBeWrittenException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, String.format(ERROR_JAR_WRITE_MESSAGE, e.getTargetFile()),
                    ERROR_JAR_WRITE, JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, ERROR_GENERIC_PACK_FAILED_MESSAGE, ERROR_GENERIC_PACK_FAILED, JOptionPane.ERROR_MESSAGE);
        }
    }
}
