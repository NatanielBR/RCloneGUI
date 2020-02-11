package rclone.wrapper.Utils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;

import rclone.models.drive.FileRemote;
import rclone.models.drive.Remote;
/**
 * Servico para carregar uma lista de arquivos remotos.
 * @author neoold
 *
 */
public class RCloneService extends Service<List<FileRemote>> {
	//Remoto para poder carregar a lista.
	private Remote remote;
	private FileRemote activeDirectory = null;
	
	public RCloneService(Remote remote) {
		this.remote = remote;
	}
	/**
	 * Metodo para ativar um diretorio
	 * @param directory Diretorio
	 */
	public void activeDirectory(FileRemote directory) {
		if (directory != null && directory.isDirectory()) {
			activeDirectory = directory;
		}
	}
	/**
	 * Metodo para soltar um diretorio
	 */
	public void releaseDirectory() {
		activeDirectory = null;
	}
	/**
	 * Metodo para verificar a existencia do diretorio
	 * @return true caso o diretorio exista e false caso contrario.
	 */
	public boolean isDirectoryActived() {
		return activeDirectory != null;
	}
	/**
	 * Metodo para obter o diretorio.
	 * @return O diretorio remoto.
	 */
	public FileRemote getActiveDirectory() {
		return activeDirectory;
	}
	
	
	@Override
	protected Task<List<FileRemote>> createTask() {
		return new Task<List<FileRemote>>() {
			@Override
			protected List<FileRemote> call() throws Exception {
				return remote.listFiles(activeDirectory);
			}
		};
	}

}
