package rclone.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Classe abstrada de configuração. Essa classe irá ser salva no disco rigido
 * com o nome gui.prop.
 *
 * @author neoold
 *
 */
@SuppressWarnings("serial")
public class Configuracao extends Properties {

    /**
     * Metodo para carregar o gui.prop existende no disco. Caso o arquivo não
     * existe irá retornar null ou caso alguns das chaves não seja informado.
     *
     * @return Uma configuração ou null caso o arquivo não exista ou as chaves
     * não seja informadas.
     */
    public static Configuracao carregarConfig() {
        Configuracao conf = new Configuracao();
        try {
            File f = new File("gui.prop");
            if (!f.exists()) {
                return null;
            }
            conf.load(new FileInputStream(f));
            for (var tipo : Tipos.values()) {
                if (!conf.containsKey(tipo.valor)) {
                    return null;
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(-1);
        }

        return conf;
    }

    /**
     * Metodo para salvar as configuração em gui.prop.
     *
     * @param conf A configuração.
     */
    public static void guardarConfig(Configuracao conf) {
        try {
            conf.store(new FileOutputStream("gui.prop"), null);
        } catch (FileNotFoundException ex) {
            try {
                new File("gui.prop").createNewFile();
                guardarConfig(conf);
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Metodo para obter o valor de uma propriedade usando chaves ja definidas.
     *
     * @param chave Um tipo de chave.
     * @return Uma objeto "bruto" onde deverá ser usado um cast para transformar
     * em um objeto "utilizavel".
     */
    public String getValor(Tipos chave) {
        return getProperty(chave.valor);
    }

    /**
     * Metodo para obter o valor de uma propriedade usando chaves ja definidas.
     *
     * @param chave Um tipo de chave.
     * @return Uma objeto "bruto" onde deverá ser usado um cast para transformar
     * em um objeto "utilizavel".
     */
    public List<String> getValorAsList(Tipos chave) {
        return stringToCollection(getValor(chave));
    }

    /**
     * Metodo para inserir uma chave e um valor.
     *
     * @param chave Uma chave ja definida.
     * @param valor Um Objeto qualquer, entretando o tipo de objeto deve ser do
     * mesmo tipo, especificado na documentação do Tipos.
     * @see Tipos
     */
    public void setChaveEValor(Tipos chave, String valor) {
        setProperty(chave.valor, valor);
    }

    /**
     * Metodo para inserir uma chave e uma lista de String.
     *
     * @param chave Uma chave ja definida.
     * @param valor Um Objeto qualquer, entretando o tipo de objeto deve ser do
     * mesmo tipo, especificado na documentação do Tipos.
     * @see Tipos
     */
    public void setChaveEValor(Tipos chave, Collection<String> valor) {
        setProperty(chave.valor, collectionToString(valor));
    }

    /**
     * Metodo para padronizar a transformação de uma coleção para String.
     *
     * @param lista Uma lista de String ou que herde dela.
     * @return Uma String tendo cada item da lista separado por ';'.
     */
    private String collectionToString(Collection<String> lista) {
        if (lista.isEmpty()) {
            return "";
        }
        return String.join(";", lista);
    }

    /**
     * Metodo para padronizar a transformação de uma String em uma coleção.
     *
     * @param valor
     * @return Uma lista de String.
     */
    private List<String> stringToCollection(String valor) {
        if (valor == null || valor.isEmpty() || valor.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        String[] array = valor.split(";");
        var list = new ArrayList<String>(array.length);
        Collections.addAll(list, array);
        return list;
    }

    public enum Tipos {
        /**
         * Representa o local onde o binario do rclone se encontra.
         *
         */
        RCLONE_LOCAL("rcloneLocal"),
        /**
         * Representa os remotos abertos. Ou seja, uma lista de nomes dos
         * remotos a ser carregado.
         */
        REMOTOS_ABERTOS("remotosAbertos");

        public final String valor;

        private Tipos(String valor) {
            this.valor = valor;
        }

    }
}
