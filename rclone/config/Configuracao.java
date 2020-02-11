package rclone.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Classe abstrada de configuração. Essa classe irá ser
 * salva no disco rigido com o nome config.data .
 * @author neoold
 *
 */
@SuppressWarnings("serial")
public abstract class Configuracao implements Serializable{
	//Lista de propriedades.
	public abstract HashMap<Tipos, Object> getPropriedades();
	/**
	 * Metodo para obter o valor de uma propriedade usando chaves
	 * ja definidas.
	 * @param chave Um tipo de chave.
	 * @return Uma objeto "bruto" onde deverá ser usado um cast
	 * para transformar em um objeto "utilizavel".
	 */
	public Object getValor(Tipos chave) {
		return getPropriedades().get(chave);
	}
	/**
	 * Metodo para inserir uma chave e um valor. 
	 * @param chave Uma chave ja definida.
	 * @param valor Um Objeto qualquer, entretando
	 * o tipo de objeto deve ser do mesmo tipo,
	 * especificado na documentação do Tipos.
	 * @see Tipos
	 */
	public void setChaveEValor(Tipos chave, Object valor) {
		getPropriedades().put(chave, valor);
	}
	/**
	 * Metodo para salvar uma configuração no disco rigido,
	 * usando ObjectOutputStream. O arquivo será salvo com o nome
	 * config.data .
	 * @param conf Uma configuração para ser salva.
	 */
	public static void guardarConfig(Configuracao conf) {
		File f = new File("config.data");
		try {
			f.delete();
			f.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(conf);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Metodo para carregar uma configuração.
	 * @return Uma configuração carregada ou null caso ocorra um erro.
	 */
	public static Configuracao carregarConfig() {
		File f = new File("config.data");
		Configuracao config;
		if (!f.exists()) return null;
		try {
			ObjectInputStream inp = new ObjectInputStream(new FileInputStream(f));
			config = (Configuracao) inp.readObject();
			inp.close();
		}catch(Exception err) {
			err.printStackTrace();
			return null;
		}
		return config;
	}
	
	public enum Tipos{
		/**
		 * Representa o local onde o binario do rclone se encontra.
		 * O tipo de retorno é <b>String<\b>.
		 */
		RCLONE_LOCAL(),
		/**
		 * Representa os remotos abertos.
		 * O tipo de retorno é <b>List<String><\b>
		 * Ou seja, uma lista de nomes dos remotos a ser carregado.
		 */
		REMOTOS_ABERTOS();
	}
}
