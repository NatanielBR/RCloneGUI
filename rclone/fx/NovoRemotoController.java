package rclone.fx;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rclone.Main;
import rclone.models.Builder;
import rclone.models.ConfigParametros;
import rclone.models.RemoteType;
import rclone.models.drive.DriveBuilder;
import rclone.models.mega.MegaBuilder;
import rclone.models.remotes.Remote;

/**
 * Classe que é o controle do fxml "novoRemoto.fxml".
 *
 * @author neoold
 *
 */
public class NovoRemotoController implements Initializable {

    private LinkedHashMap<ConfigParametros, HBox> original;
    private List<Remote> remotos;
    private ChoiceBox tipoChoice;

    /**
     * O combobox onde o usurario irá selecionar o Remoto ja configurado pelo
     * RClone.
     */
    @FXML
    public VBox propVB;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        List<HBox> hboxs = propVB.getChildren().stream().map(a -> (HBox) a)
                .collect(Collectors.toList());
        original = new LinkedHashMap<>();
        remotos = Main.wrapper.listRemotes();
        var coPa = ConfigParametros.values();
        if (hboxs.size() != coPa.length) {
            System.err.println("Tamanho dos atributos entre GUI e Wrapper é diferente.");
            System.exit(-3);
        }
        addKeyAndValueInMap(hboxs, coPa, original);
        configTipo();
        configNome();
        configAllField();
        apagar(getTudoMenosNomeETipo());
    }

    private void configAllField() {
        var list = getTudoMenosNomeETipo();
        list.stream().map(a -> original.get(a).getChildren().get(1))
                .map(a -> (TextField) a)
                .forEach(a -> {

                });
    }

    private void configNome() {
        HBox nomeBox = original.get(ConfigParametros.NOME);
        TextField field = (TextField) nomeBox.getChildren().get(1);
        field.textProperty().addListener(a -> {
            var text = field.getText();
            var remoto = remotos.stream()
                    .filter(b -> b.getRemoteName().equals(text))
                    .findFirst().orElse(null);
            if (remoto != null) {
                tipoChoice.getSelectionModel()
                        .select(primeiraLetraMaiuscula(remoto.getType()));
                apagar(getTudoMenosNomeETipo());
            } else {
                var antigo = tipoChoice.getSelectionModel().getSelectedIndex();
                tipoChoice.getSelectionModel().clearSelection();
                tipoChoice.getSelectionModel().select(antigo);
            }
        });
    }

    private void configTipo() {
        HBox tipoBox = original.get(ConfigParametros.TIPO);
        tipoChoice = (ChoiceBox) tipoBox.getChildren().get(1);
        String[] tipos = {"Drive", "Mega"};
        tipoChoice.getItems().addAll(tipos);
        tipoChoice.valueProperty().addListener(a -> {
            var value = tipoChoice.getValue();
            if (value == null) {
                restoreOrder();
                return;
            }
            Builder bu = choiceToBuilder(value.toString());
            if (bu != null) {
                var list = apagarContrario(bu.parametrosBuild());
                apagar(list);
                propVB.getScene().getWindow().sizeToScene();
            }
        });
    }

    private void apagar(List<ConfigParametros> oqApagar) {
        var children = propVB.getChildren();
        restoreOrder();
        oqApagar.stream().filter((item) -> !(item.equals(ConfigParametros.NOME)
                || item.equals(ConfigParametros.TIPO))).map((item) -> original.get(item)).forEachOrdered((box) -> {
            children.remove(box);
        });
    }

    private void restoreOrder() {
        propVB.getChildren().clear();
        propVB.getChildren().addAll(original.values());
    }

    private List<ConfigParametros> getTudoMenosNomeETipo() {
        return apagarContrario(new ConfigParametros[]{ConfigParametros.NOME,
            ConfigParametros.TIPO});
    }

    private List<ConfigParametros> apagarContrario(ConfigParametros[] arr) {
        List<ConfigParametros> original
                = new ArrayList<>(Arrays.asList(ConfigParametros.values()));
        for (var item : arr) {
            original.remove(item);
        }
        return original;
    }

    private Builder choiceToBuilder(String choice) {
        choice = choice.toLowerCase();
        switch (choice) {
            case "drive":
                return new DriveBuilder(Main.wrapper);
            case "mega":
                return new MegaBuilder(Main.wrapper);
            default:
                return null;
        }
    }

    private String primeiraLetraMaiuscula(String palavra) {
        char[] letras = palavra.toCharArray();
        letras[0] = Character.toUpperCase(letras[0]);
        return new String(letras);
    }

    private void addKeyAndValueInMap(List<HBox> la, ConfigParametros[] lb,
            HashMap<ConfigParametros, HBox> map) {
        HBox[] hboxs = la.toArray(new HBox[0]);
        for (int i = 0; i < hboxs.length; i++) {
            var hbox = hboxs[i];
            var coPa = lb[i];
            hbox.setUserData(coPa);
            map.put(coPa, hbox);
        }
    }

    private HashMap<ConfigParametros, String> transformMomento() {
        var map = new HashMap<ConfigParametros, String>();
        propVB.getChildren().forEach(a -> {
            var conf = (ConfigParametros) a.getUserData();
            String value;
            if (conf.equals(ConfigParametros.TIPO)) {
                value = tipoChoice.getValue().toString().toLowerCase();
            } else {
                value = ((TextField) original.get(conf).getChildren().get(1))
                        .getText();
            }

            map.put(conf, value);
        });
        return map;
    }

    public boolean validarMap() {
        return validarMap(transformMomento());
    }

    private boolean validarMap(HashMap<ConfigParametros, String> map) {
        ConfigParametros[] todos = null;
        ConfigParametros[] obrig = null;
        var tipo = map.get(ConfigParametros.TIPO);
        if (tipo.equals("drive")) {
            var bu = new DriveBuilder(null);
            todos = bu.parametrosBuild();
            obrig = bu.parametrosObrigadorios();
        } else if (tipo.equals("mega")) {
            var bu = new MegaBuilder(null);
            todos = bu.parametrosBuild();
            obrig = bu.parametrosObrigadorios();
        }

        for (var item : todos) {
            if (item.equals(ConfigParametros.TIPO)) {
                continue;
            }
            var value = map.get(item);
            boolean cond = value.isBlank() || value.isEmpty();
            if (Arrays.binarySearch(obrig, item) >= 0) {
                if (cond) {
                    return false;
                }
            } else {
                if (cond) {
                    map.remove(item);
                    continue;
                }
            }
        }

        return true;

    }

    public void configWindow(Dialog diag) {
        propVB.widthProperty().addListener((o) -> {
            diag.setWidth(propVB.getWidth());
        });
        propVB.heightProperty().addListener((o) -> {
            diag.setWidth(propVB.getHeight());
        });
    }

    public Object getResult() {
        var children = propVB.getChildren();
        if (children.size() == 2) {
            var name = ((TextField) original.get(ConfigParametros.NOME).getChildren().get(1))
                    .getText();
            Remote remoto = remotos.stream()
                    .filter(a -> a.getRemoteName().equals(name))
                    .findFirst().orElse(null);
            return remoto;
        } else {
            var map = transformMomento();
            if (validarMap(map)) {
                RemoteType type = new RemoteType(Main.wrapper, null, map);
                return type;
            } else {
                return Boolean.FALSE;
            }
        }
    }

}
