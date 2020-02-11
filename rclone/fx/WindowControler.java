package rclone.fx;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rclone.Main;
import rclone.config.Configuracao.Tipos;
import rclone.models.drive.Remote;
import rclone.wrapper.Utils.RemotePane;

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
		List<String> remotos = (List<String>) Main.config.getValor(Tipos.REMOTOS_ABERTOS);
		newIcon = (new Image(Main.loadResource("plus-circle-outline.png"), 16, 16, true, true));
		novoRemoto.setGraphic(new ImageView(newIcon));
		novoRemoto.setTooltip(new Tooltip("Novo Remoto"));
		
		novoRemoto.setOnAction(a -> {
			FXMLLoader loader = new FXMLLoader();
			Dialog<Remote> dialog = new Dialog<>();
			try {
				dialog.setDialogPane(loader.load(Main.loadFXMLFile("novoRemoto.fxml")));
				NovoRemotoController controler = loader.getController();
				dialog.setResultConverter(b -> {
					if (b.equals(ButtonType.CLOSE)) {
						return null;
					} else {
						return controler.getResultado();
					}
				});
				dialog.showAndWait();
				var resultado = dialog.getResult();
				// No momento essa condição é impossivel de dar falha por
				// O controle sempre selecionar o primeiro rclone.
				if (resultado != null) {
					Tab tb = new Tab(resultado.getRemoteName());
					tb.setContent(new RemotePane(resultado));
					remotosTab.getTabs().add(tb);
					remotos.add(resultado.getRemoteName());
					Main.config.setChaveEValor(Tipos.REMOTOS_ABERTOS, remotos);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		//Carregar Remotos;
		remotos.stream().map(a-> Main.wrapper.getRemoteByName(a))
		.filter(a-> a != null)
		.map(a->{
			var tab = new Tab(a.getRemoteName());
			tab.setContent(new RemotePane(a));
			return tab;
		}).forEach(remotosTab.getTabs()::add);
	}

}
