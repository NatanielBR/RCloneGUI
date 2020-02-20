package rclone.fx;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rclone.Main;
import rclone.config.Configuracao.Tipos;
import rclone.models.RemoteType;
import rclone.models.remotes.Remote;
import rclone.wrapper.Utils.RemotePane;
import rclone.wrapper.Utils.RemoteTypeTask;

public class WindowControler implements Initializable {

    @FXML
    public TabPane remotosTab;
    @FXML
    public Button novoRemoto;
    /**
     * Icone de adicionar um novo Remoto
     */
    private Image newIcon;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        @SuppressWarnings("unchecked")
        List<String> remotos = Main.config.getValorAsList(Tipos.REMOTOS_ABERTOS);
        newIcon = (new Image(Main.loadResource("plus-circle-outline.png"), 16, 16, true, true));
        novoRemoto.setGraphic(new ImageView(newIcon));
        novoRemoto.setTooltip(new Tooltip("Novo Remoto"));

        novoRemoto.setOnAction(a -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                DialogPane pane = new DialogPane();
                pane.setContent(loader.load(Main.loadFXMLFile("novoRemoto.fxml")));
                pane.getButtonTypes().addAll(ButtonType.CLOSE, ButtonType.OK);
                NovoRemotoController controle = loader.getController();
                ((Button) pane.lookupButton(ButtonType.OK))
                        .addEventFilter(ActionEvent.ACTION, b -> {
                            if (!controle.validarMap()) {
                                Alert ale = new Alert(Alert.AlertType.ERROR);
                                ale.setContentText("Os campos obrigatorios devem ser preenchidos.");
                                ale.setHeaderText("Alguns campos não foram preenchidos.");
                                ale.setTitle("Erro ao criar o remoto");
                                ale.showAndWait();
                                b.consume();
                            }
                        });
                Dialog<Object> dialog = new Dialog<>();
                dialog.setDialogPane(pane);
                dialog.setResultConverter(b -> {
                    if (b == ButtonType.OK) {
                        return controle.getResult();
                    }
                    return null;
                });
                controle.configWindow(dialog);
                dialog.showingProperty().addListener(c -> {
                    if (!dialog.isShowing()) {
                        return;
                    }
                    var result = dialog.getResult();
                    if (result != null) {
                        if (result instanceof RemoteType) {
                            RemoteType type = (RemoteType) result;

                            RemoteTypeTask task = new RemoteTypeTask(type);
                            task.valueProperty().addListener(b -> {
                                var value = task.getValue();
                                if (value != null) {
                                    adicionarRemoto(value);
                                }
                            });
                            new Thread(task).start();
                        } else if (result instanceof Remote) {
                            Remote remoto = (Remote) result;
                            adicionarRemoto(remoto);
                        }
                    }
                });
                dialog.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //Carregar Remotos;
        remotos.stream().map(a -> Main.wrapper.getRemoteByName(a))
                .filter(a -> a != null)
                .map(a -> {
                    var tab = new Tab(a.getRemoteName());
                    tab.setContent(new RemotePane(a));
                    return tab;
                }).forEach(remotosTab.getTabs()::add);
    }

    private void adicionarRemoto(Remote remoto) {
        var tab = new Tab(remoto.getRemoteName());
        tab.setContent(new RemotePane(remoto));
        remotosTab.getTabs().add(tab);
    }
}
