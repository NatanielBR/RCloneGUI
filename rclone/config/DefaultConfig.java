package rclone.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa as Configurações padrão com as seguintes chaves e valores:
 * RCLONE_LOCAL: rbin/rclone (caso seja windows, a barra será alterada e será
 * colocado a extensão de executavel) REMOTOS_ABERTOS:
 * <Uma lista de String vazia>
 *
 * @author neoold
 *
 */
@SuppressWarnings("serial")
public class DefaultConfig extends Configuracao {

    /**
     * Constroi uma configuração padrão. Sendo o local padrão o rbin/rclone ,
     * entratando a barra e a extensão do arquivo poderá ser alterada de acordo
     * com o SO.
     *
     * @return Uma configuração padrão com os sequintes valores:
     *
     * rcloneLocal=rbin/rclone remotosAbertos=
     */
    public static Configuracao getDefaultConfig() {
        return getDefaultConfig(String.format("rbin%srclone%s", File.separator,
                isLinux() ? "" : ".exe"));
    }

    /**
     * Constroi uma configuração padrão usando um local diferentea do padrão,
     * entretando sendo locais não especificado pelo usuario.
     *
     * @param local Um local do binario do rclone.
     * @return
     */
    public static Configuracao getDefaultConfig(String local) {
        var def = new DefaultConfig();
        def.setChaveEValor(Tipos.RCLONE_LOCAL, local);
        def.setChaveEValor(Tipos.REMOTOS_ABERTOS, new ArrayList<>());
        return def;
    }

    /**
     * Metodo para verificar se é linux.
     *
     * @return
     */
    private static boolean isLinux() {
        return System.getProperty("os.name").equals("Linux");
    }
}
