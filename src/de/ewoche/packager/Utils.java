package de.ewoche.packager;

import javax.swing.*;
import java.awt.*;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static de.ewoche.packager.layout.Constants.*;

public final class Utils {
    private static String HOST_NAME = null;
    private Utils() {}

    public static void displayAccessDenied(Path file, Component parent) {
        JOptionPane.showMessageDialog(parent, String.format(ERROR_ACCESS_DENIED_MESSAGE, file.toAbsolutePath().toString()), ERROR_ACCESS_DENIED, JOptionPane.ERROR_MESSAGE);
    }

    public static void displayNotADir(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, String.format(ERROR_NOT_A_DIR_MESSAGE, text), ERROR_NOT_A_DIR, JOptionPane.ERROR_MESSAGE);
    }

    public static void displayPathDoesNotExist(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, String.format(ERROR_PATH_DOES_NOT_EXIST_MESSAGE, text), ERROR_PATH_DOES_NOT_EXIST, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean checkExistsAndIsDirectory(String text, Component parent) {
        return checkExistsAndIsDirectory(Paths.get(text), text, parent);
    }

    public static boolean checkExistsAndIsDirectory(Path path, String text, Component parent) {
        if (! Files.exists(path)) {
            Utils.displayPathDoesNotExist(text, parent);
            return false;
        }
        if (! Files.isDirectory(path)) {
            Utils.displayNotADir(text, parent);
            return false;
        }
        return true;
    }

    //required for safe splitting of Strings which contain the \ character
    public static List<String> splitNoRegex(String toSplit, char splitChar) {
        int start = 0;
        List<String> res = new ArrayList<>();
        for (int i = 0; i < toSplit.length(); i++) {
            char cur = toSplit.charAt(i);
            if (cur == splitChar) {
                if (cur == start)
                    res.add("");
                else
                    res.add(toSplit.substring(start, i));
                start = i + 1;
            }
        }
        if (toSplit.length() == start)
            res.add("");
        else
            res.add(toSplit.substring(start));
        return res;
    }

    public static String getHostName() {
        if (HOST_NAME == null) {
            try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream()))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                HOST_NAME = builder.toString();
            } catch (Exception e) {
                System.err.println("Unable to resolve Host Name! Assuming tux1");
                e.printStackTrace();
                return "tux1";
            }
        }
        return HOST_NAME;
    }
}
