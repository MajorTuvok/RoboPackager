package de.ewoche.packager.layout.gui;

import de.ewoche.packager.layout.SizedDialog;
import de.ewoche.packager.layout.list.NoSelectionModel;
import de.ewoche.packager.packaging.PackagingResult;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Collection;

public class UnpackedFilesDialog extends SizedDialog {
    private JPanel rootPanel;
    private JPanel undiscoveredDirsPanel;
    private JPanel undiscoveredFilesPanel;
    private JPanel erroredPanel;
    private JScrollPane undiscoveredDirsPane;
    private JScrollPane undiscoveredFilesPane;
    private JScrollPane erroredPane;

    public UnpackedFilesDialog(Frame owner, PackagingResult result) {
        super(owner, UNPACKED_FILES_TITLE);
        undiscoveredDirsPane.setViewportView(createListFor(result.getUndiscoveredDirs()));
        undiscoveredFilesPane.setViewportView(createListFor(result.getUndiscoveredFiles()));
        erroredPane.setViewportView(createListFor(result.getErroredFiles()));
    }

    private JList<String> createListFor(Collection<Path> items) {
        JList<String> list = new JList<>();
        list.setSelectionModel(new NoSelectionModel());
        list.setListData(items.stream().map(Path::toAbsolutePath).map(Path::toString).toArray(String[]::new));
        return list;
    }
}
