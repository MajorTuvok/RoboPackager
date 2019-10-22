package de.ewoche.packager.layout;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

public class SizedFrame extends JFrame implements Constants, IManagedWindow {

    public SizedFrame(String title) throws HeadlessException {
        super(title);
        WindowAdapter adapter = createWindowAdapter();
        addWindowListener(adapter);
        addWindowFocusListener(adapter);
        addWindowStateListener(adapter);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.round(screenSize.width * RELATIVE_START_SIZE), Math.round(screenSize.height * RELATIVE_START_SIZE));
    }

    protected WindowAdapter createWindowAdapter() {
        return new PoppingWindowAdapter();
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
