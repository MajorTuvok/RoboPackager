package de.ewoche.packager;

import de.ewoche.packager.layout.IManagedWindow;
import de.ewoche.packager.layout.gui.StartFrame;

import java.util.Deque;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        pushWindow(new StartFrame());
    }

    private static final Deque<IManagedWindow> backStack = new LinkedList<>();

    public static void pushWindow(IManagedWindow window) {
        IManagedWindow current = backStack.peekFirst();
        if (current != null) {
            System.out.println("Hiding \"" + current.getTitle() + "\"");
            current.setVisible(false);
            current.onHidden(window);
        }
        System.out.println("Opening \"" + window.getTitle() + "\"");
        backStack.push(window);
        window.setVisible(true);
        window.onOpenedAtop(current);
    }

    public static void popWindow() {
        IManagedWindow current = backStack.pollFirst();
        if (current != null) {
            System.out.println("Closing \"" + current.getTitle() + "\"");
            current.setVisible(false);
            IManagedWindow parent = backStack.peekFirst();
            current.onClosingAtop(parent);
            if (parent != null) {
                System.out.println("Re-Opening \"" + parent.getTitle() + "\"");
                parent.setVisible(true);
                parent.onShown(current);
            }
        }
    }

    public static void replaceWindow(IManagedWindow window) {
        IManagedWindow current = backStack.pollFirst();
        IManagedWindow parent = backStack.peekFirst();
        if (current != null) {
            System.out.println("Closing \"" + current.getTitle() + "\"");
            current.setVisible(false);
            current.onClosingAtop(parent);
        }
        System.out.println("Opening \"" + window.getTitle() + "\"");
        backStack.push(window);
        window.setVisible(true);
        window.onOpenedAtop(parent);
    }
}
