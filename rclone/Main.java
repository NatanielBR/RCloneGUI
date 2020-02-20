package rclone;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rclone.config.AutoConfig;
import rclone.config.Configuracao;
import rclone.config.Configuracao.Tipos;
import rclone.wrapper.RCloneWrapper;

public class Main extends Application {
    //Acesso a configuração

    public static Configuracao config;
    //Acesso ao Wrapper (Evita ficar re instanciando)
    public static RCloneWrapper wrapper;

    public static void main(String[] args) {
        config = AutoConfig.autoConfig();
        wrapper = new RCloneWrapper(config.getValor(Tipos.RCLONE_LOCAL));
        if (wrapper == null) {
            System.err.println("Binario do rclone não encontrado.");
            System.exit(-2);
        }
        launch(args);
    }

    @Override
    public void start(Stage stg) throws Exception {
        Scene scene = new Scene(loadFXML("window.fxml"));
        stg.setScene(scene);
        stg.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        //Quando finalizar o programa, irá salvar as configuração.
        Configuracao.guardarConfig(config);
    }
    //Metodos quebra galho

    public static InputStream loadFXMLFile(String file) {
        return Main.class.getResourceAsStream(String.format("fx/%s", file));
    }

    public static InputStream loadResource(String file) {
        return Main.class.getResourceAsStream(String.format("fx/res/%s", file));
    }

    public static <T> T loadFXML(String file) throws IOException {
        return new FXMLLoader().load(loadFXMLFile(file));
    }
}
