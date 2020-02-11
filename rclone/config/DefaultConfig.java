package rclone.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Representa as Configurações padrão com as seguintes chaves e  valores:
 * RCLONE_LOCAL: rbin/rclone (caso seja windows, a barra será
 * alterada e será colocado a extensão de executavel)
 * REMOTOS_ABERTOS: <Uma lista de String vazia>
 * @author neoold
 *
 */
@SuppressWarnings("serial")
public class DefaultConfig extends Configuracao{
	private HashMap<Tipos, Object> data;
	private DefaultConfig() {
		data = new HashMap<Configuracao.Tipos, Object>();
		data.put(Tipos.RCLONE_LOCAL, String.format("rbin%srclone%s", File.separator,
				isLinux() ? "" : ".exe"));
		data.put(Tipos.REMOTOS_ABERTOS, new ArrayList<String>());
	}
	/**
	 * Constroi uma configuração padrão.
	 * @return
	 */
	public static Configuracao getDefaultConfig(){
		return new DefaultConfig();
	}
	/**
	 * Metodo para verificar se é linux.
	 * @return
	 */
	private boolean isLinux() {
		return System.getProperty("os.name").equals("Linux");
	}
	@Override
	public HashMap<Tipos, Object> getPropriedades() {
		return data;
	}
}
