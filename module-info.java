module RcloneGUI {
	requires transitive RcloneWrapper;
	requires transitive javafx.graphics;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires java.base;
	exports rclone;
	exports rclone.fx;
	exports rclone.wrapper.Utils;
	exports rclone.config;
}