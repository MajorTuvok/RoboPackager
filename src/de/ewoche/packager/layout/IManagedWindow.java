package de.ewoche.packager.layout;

import de.ewoche.packager.Main;

/**
 * A window which is managed by {@link Main}'s backstack. The lifecycle for such a window is as follows:
 * <ol>
 * <li>The window is shown and receives {@link #onOpenedAtop(IManagedWindow)}.</li>
 * <li>A child window is opened and this window becomes invisible. It will receive {@link #onHidden(IManagedWindow)}.</li>
 * <li>A child window is closed and this window becomes visible again. It will recieve {@link #onShown(IManagedWindow)}.</li>
 * <li>This window is closed and receives {@link #onClosingAtop(IManagedWindow)}.</li>
 * </ol>
 * Of course steps 2 and 3 may occur multiple times at once.
 */
public interface IManagedWindow {

    String getTitle();

    /**
     * @param visible whether or not this window should be visible or not
     */
    void setVisible(boolean visible);

    /**
     * Called when a child window has been closed, resulting in this window being shown again.
     *
     * @param closedChild The window that is now hidden/will be closed
     */
    void onShown(IManagedWindow closedChild);

    /**
     * Called when a window is hidden behind another child via {@link Main#pushWindow(IManagedWindow)}
     *
     * @param openingChild The new child that is now displayed.
     */
    void onHidden(IManagedWindow openingChild);


    /**
     * Called on a child window when it is displayed and replaces another window in the process.
     *
     * @param window The parent window which is now hidden behind this child. May be null if no parent exists yet.
     */
    void onOpenedAtop(IManagedWindow window);

    /**
     * Called on a closing child window, when it's parent has been requested to come into view again.
     *
     * @param window The parent window which will become visible again. May be null if this window had no parent.
     */
    void onClosingAtop(IManagedWindow window);
}
