/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rclone.wrapper.Utils;

import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import rclone.models.RemoteType;
import rclone.models.remotes.Remote;

/**
 *
 * @author neoold
 */
public class RemoteTypeTask extends Task<Remote> {

    private RemoteType type;

    private Dialog dialog;

    public RemoteTypeTask(RemoteType type) {
        dialog = new Dialog();
        DialogPane panel = new DialogPane();
        TextArea area = new TextArea();

        area.setEditable(false);
        panel.setContent(area);
        dialog.setDialogPane(panel);
        panel.getButtonTypes().add(ButtonType.CANCEL);
        dialog.showingProperty().addListener(a -> {
            if (!dialog.isShowing()) {
                type.finalizarProcesso();
                this.cancel();
            }
        });
        messageProperty().addListener(a -> {
            area.setText(getMessage());
        });
        setOnRunning(a -> {
            dialog.show();
        });
        setOnSucceeded(a -> {
            panel.getButtonTypes().add(ButtonType.OK);
            panel.getButtonTypes().remove(ButtonType.CANCEL);
        });
        setOnFailed(a -> {
            panel.getButtonTypes().add(ButtonType.CANCEL);
        });
        this.type = type;
    }

    @Override
    protected Remote call() throws Exception {
        type.create(a -> {
            this.updateMessage(a);
        });

        return type;
    }

}
