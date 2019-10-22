package de.ewoche.packager.layout;

import de.ewoche.packager.Main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PoppingWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        Main.popWindow();
        e.getWindow().dispose();
    }
}
