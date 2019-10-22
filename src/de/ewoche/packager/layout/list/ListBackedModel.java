package de.ewoche.packager.layout.list;

import javax.swing.*;
import java.util.List;

public class ListBackedModel<T> extends AbstractListModel<T> {
    private final List<T> list;

    public ListBackedModel(List<T> list) {
        this.list = list;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T getElementAt(int index) {
        return list.get(index);
    }
}
