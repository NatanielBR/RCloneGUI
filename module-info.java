module RcloneGUI {
    requires java.base;
    requires rwrapper;
    requires javafx.swt;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    exports rclone;
    exports rclone.fx;
    exports rclone.wrapper.Utils;
    exports rclone.config;
}
