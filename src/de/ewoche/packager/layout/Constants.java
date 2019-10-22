package de.ewoche.packager.layout;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public interface Constants {
    public static final float RELATIVE_START_SIZE = 0.4f;
    public static final float RELATIVE_DIALOG_START_SIZE = 0.25f;

    public static final Path ROBOCODE_REL_LIB = Paths.get("libs/robocode.jar");

    public static final ResourceBundle LANG_RESOURCES = ResourceBundle.getBundle("lang");

    public static final String START_FRAME_TITLE = LANG_RESOURCES.getString("start_frame.title");
    public static final String ADVANCED_OPTIONS_TITLE = LANG_RESOURCES.getString("adv_opt.title");
    public static final String SELECT_ROBOTS_TITLE = LANG_RESOURCES.getString("select_robots.title");
    public static final String CONFIGURE_ROBOT_TITLE = LANG_RESOURCES.getString("cfg_robot.title");
    public static final String UNPACKED_FILES_TITLE = LANG_RESOURCES.getString("unpacked_dialog.title");

    public static final String ERROR_ACCESS_DENIED = LANG_RESOURCES.getString("error.access_denied");
    public static final String ERROR_ACCESS_DENIED_MESSAGE = LANG_RESOURCES.getString("error.access_denied.message");
    public static final String ERROR_BUILD_DIRECTORY_NOT_FOUND = LANG_RESOURCES.getString("error.no_build_dir");
    public static final String ERROR_BUILD_DIRECTORY_NOT_FOUND_MESSAGE = LANG_RESOURCES.getString("error.no_build_dir.message");
    public static final String ERROR_JAR_DIR_SCAN = LANG_RESOURCES.getString("error.jar_dir_scan");
    public static final String ERROR_JAR_DIR_SCAN_MESSAGE = LANG_RESOURCES.getString("error.jar_dir_scan.message");
    public static final String ERROR_JAR_WRITE = LANG_RESOURCES.getString("error.jar_write");
    public static final String ERROR_JAR_WRITE_MESSAGE = LANG_RESOURCES.getString("error.jar_write.message");
    public static final String ERROR_NOT_A_DIR = LANG_RESOURCES.getString("error.not_a_dir");
    public static final String ERROR_NOT_A_DIR_MESSAGE = LANG_RESOURCES.getString("error.not_a_dir.message");
    public static final String ERROR_NOT_A_FILE = LANG_RESOURCES.getString("error.not_a_file");
    public static final String ERROR_NOT_A_FILE_MESSAGE = LANG_RESOURCES.getString("error.not_a_file.message");
    public static final String ERROR_NO_ROBOT_SELECTED = LANG_RESOURCES.getString("error.no_robots_selected");
    public static final String ERROR_NO_ROBOT_SELECTED_MESSAGE = LANG_RESOURCES.getString("error.no_robots_selected.message");
    public static final String ERROR_NO_ROBOT_DETECTED = LANG_RESOURCES.getString("error.no_robots_detected");
    public static final String ERROR_NO_ROBOT_DETECTED_MESSAGE = LANG_RESOURCES.getString("error.no_robots_detected.message");
    public static final String ERROR_GENERIC_PACK_FAILED = LANG_RESOURCES.getString("error.packing_failed");
    public static final String ERROR_GENERIC_PACK_FAILED_MESSAGE = LANG_RESOURCES.getString("error.packing_failed.message");
    public static final String ERROR_GENERIC_ROBOT_DISCOVER = LANG_RESOURCES.getString("error.robot_discover");
    public static final String ERROR_GENERIC_ROBOT_DISCOVER_MESSAGE = LANG_RESOURCES.getString("error.robot_discover.message");
    public static final String ERROR_PATH_DOES_NOT_EXIST = LANG_RESOURCES.getString("error.path_does_not_exist");
    public static final String ERROR_PATH_DOES_NOT_EXIST_MESSAGE = LANG_RESOURCES.getString("error.path_does_not_exist.message");
    public static final String ERROR_ROBOCODE_INVALID_INSTALL = LANG_RESOURCES.getString("error.robocode.invalid_install");
    public static final String ERROR_ROBOCODE_INVALID_INSTALL_MESSAGE = LANG_RESOURCES.getString("error.robocode.invalid_install.message");
    public static final String ERROR_ROBOT_DISCOVER_DIR_VISIT = LANG_RESOURCES.getString("error.robot_discover.directory_visit");
    public static final String ERROR_ROBOT_DISCOVER_DIR_VISIT_MESSAGE = LANG_RESOURCES.getString("error.robot_discover.directory_visit.message");
    public static final String ERROR_ROBOT_DISCOVER_NO_ROBOT = LANG_RESOURCES.getString("error.robot_discover.no_robot");
    public static final String ERROR_ROBOT_DISCOVER_NO_ROBOT_MESSAGE = LANG_RESOURCES.getString("error.robot_discover.no_robot.message");
    public static final String ERROR_TARGET_FILE_INVALID = LANG_RESOURCES.getString("error.target_file_invalid");
    public static final String ERROR_TARGET_FILE_INVALID_MESSAGE = LANG_RESOURCES.getString("error.target_file_invalid.message");

    public static final String WARNING_BUILD_DIR_NOT_DETECTED = LANG_RESOURCES.getString("warning.build_dir_not_resolved");
    public static final String WARNING_BUILD_DIR_NOT_DETECTED_MESSAGE = LANG_RESOURCES.getString("warning.build_dir_not_resolved.message");
    public static final String WARNING_FILE_ALREADY_EXISTS = LANG_RESOURCES.getString("warning.jar_already_exists");
    public static final String WARNING_FILE_ALREADY_EXISTS_MESSAGE = LANG_RESOURCES.getString("warning.jar_already_exists.message");
    public static final String WARNING_NO_TARGET_SPECIFIED = LANG_RESOURCES.getString("warning.no_target_specified");
    public static final String WARNING_NO_TARGET_SPECIFIED_MESSAGE = LANG_RESOURCES.getString("warning.no_target_specified.message");
}
