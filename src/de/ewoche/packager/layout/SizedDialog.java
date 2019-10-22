package de.ewoche.packager.layout;


import javax.swing.*;
import java.awt.*;

public class SizedDialog extends JDialog implements Constants, IManagedWindow {
    public SizedDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }

    public SizedDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }

    private void init() {
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.round(screenSize.width * RELATIVE_DIALOG_START_SIZE), Math.round(screenSize.height * RELATIVE_DIALOG_START_SIZE));
    }

    @Override
    public void onShown(IManagedWindow closedChild) {

    }

    @Override
    public void onHidden(IManagedWindow openingChild) {

    }

    @Override
    public void onOpenedAtop(IManagedWindow window) {

    }

    @Override
    public void onClosingAtop(IManagedWindow window) {

    }
}
