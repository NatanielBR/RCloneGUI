package rclone.fx;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import rclone.Main;
import rclone.models.drive.Remote;

/**
 * Classe que é o controle do fxml "novoRemoto.fxml".
 * 
 * @author neoold
 *
 */
public class NovoRemotoController implements Initializable {
	/**
	 * Uma lista de Remotos
	 */
	private List<Remote> listRemotes;
	/**
	 * O combobox onde o usurario irá selecionar o Remoto ja configurado
	 * pelo RClone.
	 */
	@FXML
	public ComboBox<String> remotos;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		remotos.setEditable(false);
		listRemotes = (Main.wrapper.listRemotes());
		listRemotes.stream().map(a->a.getRemoteName()).forEach(remotos.getItems()::add);
		remotos.getSelectionModel().selectFirst();
	}
	/**
	 * Metodo para o Dialog poder obter o resultado.
	 * @return Um Remote filtrado da lista de remotos, ou null caso não exista.
	 */
	public Remote getResultado() {
		return listRemotes.stream().filter(a->a.getRemoteName().equals(remotos.getValue()))
				.findFirst().orElse(null);
	}
}
