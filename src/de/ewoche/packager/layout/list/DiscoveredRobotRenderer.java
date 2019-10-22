package de.ewoche.packager.layout.list;

import de.ewoche.packager.discovery.DiscoveredRobot;

import javax.swing.*;
import java.awt.*;

public class DiscoveredRobotRenderer implements ListCellRenderer<DiscoveredRobot> {
    private final DefaultListCellRenderer defaultRenderer;

    public DiscoveredRobotRenderer() {
        defaultRenderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DiscoveredRobot> list, DiscoveredRobot value, int index, boolean isSelected, boolean cellHasFocus) {
        return defaultRenderer.getListCellRendererComponent(list, value.getName(), index, isSelected, cellHasFocus);
    }
}
