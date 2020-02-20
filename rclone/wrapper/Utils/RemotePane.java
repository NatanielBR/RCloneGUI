package rclone.wrapper.Utils;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import rclone.Main;
import rclone.models.remotes.FileRemote;
import rclone.models.remotes.Remote;

/**
 * Painel onde irá mostrar o Remoto ao usuario. Ele herda de BorderPane, por
 * causa de uma ideia futura.
 *
 * @author neoold
 *
 */
public class RemotePane extends BorderPane {
    //Task unica, ou seja, não é possivel abrir dois diretorios ao mesmo tempo.

    private RCloneService monoRcloneTask;

    private Image folderIcon;  // icone padrão ou sem outras pastas em seu interior
    private Image folderIcon2; // icone para pasta com outras pastas em seu interior
    private Image loadIcon;    // icone que representa que a pasta esta carrega
    private Image fileIcon;    // icone que representa um arquivo.

    //Celula de um arquivo
    private Callback<ListView<FileRemote>, ListCell<FileRemote>> fileCell;
    //Celula de uma pasta
    private Callback<TreeView<FileRemote>, TreeCell<FileRemote>> folderCell;

    //lista de arquivos remotos
    public ListView<FileRemote> arquivoList;
    //lista de pastas remotas.
    public TreeView<FileRemote> pastaList;

    /**
     * Construtor carrega os icones, instancia as listas, organiza o container e
     * configura o serviço do rclone.
     *
     * @param remote
     */
    public RemotePane(Remote remote) {
        arquivoList = new ListView<FileRemote>();
        pastaList = new TreeView<FileRemote>();
        folderIcon = (new Image(Main.loadResource("folder-outline.png"), 16, 16, true, true));
        folderIcon2 = (new Image(Main.loadResource("folder-remove-outline.png"), 16, 16, true, true));
        loadIcon = (new Image(Main.loadResource("loader-outline.png"), 16, 16, true, true));
        fileIcon = (new Image(Main.loadResource("file-outline.png"), 16, 16, true, true));

        SplitPane pane = new SplitPane();
        pane.getItems().addAll(pastaList, arquivoList);
        pane.setDividerPositions(0.3);
        setCenter(pane);

        monoRcloneTask = new RCloneService(remote);
        monoRcloneTask.setOnScheduled(a -> {
            pastaList.refresh();
        });
        monoRcloneTask.setOnSucceeded((a) -> {
            List<FileRemote> lista = monoRcloneTask.getValue();
            arquivoList.getItems().clear();
            // filtra oque for diretorio
            setAllInPastaList(lista.stream().filter(b -> b.isDirectory()).collect(Collectors.toList()));
            // filtra oque não for diretorio
            lista.stream().filter(b -> !b.isDirectory()).forEach(arquivoList.getItems()::add);
        });

        fileCell = new Callback<ListView<FileRemote>, ListCell<FileRemote>>() {

            @Override
            public ListCell<FileRemote> call(ListView<FileRemote> arg0) {
                return new ListCell<FileRemote>() {
                    protected void updateItem(FileRemote item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        var name = item.getName();
                        setText(name);
                        setGraphic(new ImageView(fileIcon));
                    }
                };
            }

        };
        folderCell = new Callback<TreeView<FileRemote>, TreeCell<FileRemote>>() {

            @Override
            public TreeCell<FileRemote> call(TreeView<FileRemote> view) {
                return new TreeCell<FileRemote>() {
                    @Override
                    protected void updateItem(FileRemote item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setOnMouseClicked(a -> {
                                pastaList.getSelectionModel().clearSelection();
                                monoRcloneTask.releaseDirectory();
                                monoRcloneTask.restart();
                            });
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        var treeItem = treeItemProperty().get();
                        var name = item.getName();
                        // remover o '/' do final, fins de estetica
                        if (item.isDirectory()) {
                            name = name.substring(0, name.length() - 1);
                        }
                        ImageView graphic;

                        if (monoRcloneTask.isDirectoryActived()
                                && monoRcloneTask.getActiveDirectory().getName().equals(item.getName())
                                && monoRcloneTask.isRunning()) {
                            graphic = new ImageView(loadIcon);
                        } else if (treeItem.getChildren().size() > 0) {
                            graphic = new ImageView(folderIcon2);
                        } else {
                            graphic = new ImageView(folderIcon);
                        }

                        setGraphic(graphic);
                        setDisclosureNode(null);
                        setText(name);
                        setOnMouseClicked(a -> {
                            if (a.getButton().equals(MouseButton.PRIMARY)
                                    && a.getClickCount() == 1) {
                                if (treeItem.getChildren().size() > 0) {
                                    treeItem.setExpanded(!treeItem.isExpanded());
                                    return;
                                }
                                monoRcloneTask.activeDirectory(item);
                                monoRcloneTask.restart();
                            }
                        });
                    }
                };
            }
        };

        pastaList.setShowRoot(false);

        pastaList.setCellFactory(folderCell);
        arquivoList.setCellFactory(fileCell);

        monoRcloneTask.start();
    }

    /**
     * Metodo para inserir uma lista no TreeView. Usando um outro metodo para
     * converter de FileRemote para TreeItem, esse metodo somente adiciona esse
     * resultado na TreeView, entretando ele tambem caça o diretorio para
     * adicionar novos diretorios aninhados.
     *
     * @param list Uma lista de pastas.
     */
    private void setAllInPastaList(List<FileRemote> list) {
        if (list.isEmpty()) {
            return;
        }
        if (monoRcloneTask.isDirectoryActived()) {
            var direc = pastaList.getRoot().getChildren().stream()
                    .filter(a -> a.getValue().getName().equals(monoRcloneTask.getActiveDirectory().getName())).findFirst()
                    .orElse(null);
            direc.getChildren().setAll(transformListToTreeItem(list));
            direc.setExpanded(true);
        } else {
            TreeItem<FileRemote> root = new TreeItem<FileRemote>();
            root.setValue(null);
            root.getChildren().setAll(transformListToTreeItem(list));
            pastaList.setRoot(root);
        }
    }

    /**
     * Esse metodo converte uma List<FileRemote> para List<TreeItem<FileRemote>>
     * @param listA
     * @return
     */
    private List<TreeItem<FileRemote>> transformListToTreeItem(List<FileRemote> listA) {
        return listA.stream().map(a -> new TreeItem<FileRemote>(a))
                .collect(Collectors.toList());
    }

}
