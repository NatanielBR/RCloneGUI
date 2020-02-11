package rclone.config;

/**
 * Classe que irá auto configurar as propriedades do RClone.
 * Pretendo dar mais peso a essa classe num futuro.
 * @author neoold
 *
 */
public class AutoConfig {
	/**
	 * Obtem a configuração padrão.
	 * @return
	 */
	public static Configuracao autoConfig() {
		Configuracao conf = Configuracao.carregarConfig();
		if (conf == null) {
			conf = DefaultConfig.getDefaultConfig();
			Configuracao.guardarConfig(conf);
		}
		return conf;
	}
}
