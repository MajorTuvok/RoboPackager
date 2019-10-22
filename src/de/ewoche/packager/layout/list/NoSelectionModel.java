package de.ewoche.packager.layout.list;

import javax.swing.*;

/**
 * copied from https://stackoverflow.com/questions/31669350/disable-jlist-cell-selection-property
 */
public class NoSelectionModel extends DefaultListSelectionModel {

    @Override
    public void setAnchorSelectionIndex(final int anchorIndex) {}

    @Override
    public void setLeadAnchorNotificationEnabled(final boolean flag) {}

    @Override
    public void setLeadSelectionIndex(final int leadIndex) {}

    @Override
    public void setSelectionInterval(final int index0, final int index1) { }
}
