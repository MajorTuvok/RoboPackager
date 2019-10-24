package de.ewoche.packager.settings;

import de.ewoche.packager.Utils;

import java.util.List;
import java.util.stream.Collectors;

final class ComparableRobocodeVersion implements Comparable<ComparableRobocodeVersion> {
    private final List<Integer> versionIndices;

    public ComparableRobocodeVersion(String versionString) {
        versionIndices = Utils.splitNoRegex(versionString, '.')
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(ComparableRobocodeVersion o) {
        int compare = 0;
        int index = 0;
        while (index < versionIndices.size() && index < o.versionIndices.size() && (compare = versionIndices.get(index).compareTo(o.versionIndices.get(index))) == 0)
            ++ index;
        return compare != 0 ? compare : Integer.compare(versionIndices.size(), o.versionIndices.size());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < versionIndices.size() - 1; i++) {
            builder.append(versionIndices.get(i));
            builder.append('.');
        }
        if (! versionIndices.isEmpty())
            builder.append(versionIndices.get(versionIndices.size() - 1));
        return builder.toString();
    }
}
