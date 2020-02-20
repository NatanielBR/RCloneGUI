package rclone.config;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import rclone.config.Configuracao.Tipos;

/**
 * Classe que irá auto configurar as propriedades do RClone. Pretendo dar mais
 * peso a essa classe num futuro.
 *
 * @author neoold
 *
 */
public class AutoConfig {

    /**
     * Obtem a configuração padrão
     *
     * @return Uma configuração existende ou uma nova configuração com valores
     * padrão.
     */
    public static Configuracao autoConfig() {
        Configuracao conf = Configuracao.carregarConfig();
        Iterator<String> locais = Arrays.asList("/usr/bin/rclone").iterator();
        if (conf == null) {
            conf = DefaultConfig.getDefaultConfig();
        }
        File local = new File(conf.getValor(Tipos.RCLONE_LOCAL));
        while (!local.exists() && locais.hasNext()) {
            conf = DefaultConfig.getDefaultConfig(locais.next());
            local = new File(conf.getValor(Tipos.RCLONE_LOCAL));
        }
        //caso a lista de locais acabe e o binario não exista.
        if (!local.exists()) {
            return null;
        } else {
            Configuracao.guardarConfig(conf);
        }
        return conf;
    }
}
