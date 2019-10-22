package de.ewoche.packager.layout.gui;

import de.ewoche.packager.Main;
import de.ewoche.packager.discovery.DiscoveredRobot;
import de.ewoche.packager.layout.SizedFrame;
import de.ewoche.packager.layout.list.DiscoveredRobotRenderer;
import de.ewoche.packager.layout.list.ListBackedModel;
import de.ewoche.packager.settings.RoboPackagerConfig;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class SelectRobotsFrame extends SizedFrame {
    private JTextArea helpTextArea;
    private JPanel rootPanel;
    private JScrollPane scrollPane;
    private JButton nextButton;
    private JButton cancelButton;
    private JPanel buttonsPanel;
    private RoboPackagerConfig config;

    public SelectRobotsFrame(RoboPackagerConfig config, List<DiscoveredRobot> discoveredRobotList) {
        super(SELECT_ROBOTS_TITLE);
        this.config = config;
        discoveredRobotList.sort(DiscoveredRobot.NAME_COMPARATOR);
        JList<DiscoveredRobot> scrollList = new JList<>();
        scrollList.setModel(new ListBackedModel<>(discoveredRobotList));
        scrollList.setCellRenderer(new DiscoveredRobotRenderer());
        scrollList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane.setViewportView(scrollList);
        nextButton.addActionListener(e -> {
            LinkedList<DiscoveredRobot> selectedIndices = new LinkedList<>();
            for (int i : scrollList.getSelectedIndices()) {
                selectedIndices.add(discoveredRobotList.get(i));
            }
            if (! selectedIndices.isEmpty()) {
                Main.replaceWindow(new ConfigureRobotFrame(config, new LinkedList<>(selectedIndices)));
                dispose();
            } else {
                JOptionPane.showConfirmDialog(this, ERROR_NO_ROBOT_SELECTED_MESSAGE, ERROR_NO_ROBOT_SELECTED, JOptionPane.YES_NO_OPTION);
            }
        });
        cancelButton.addActionListener(e -> {
            Main.popWindow();
            dispose();
        });
        setContentPane(rootPanel);
    }
}
