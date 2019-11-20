package meg.swapout.imp;


import meg.swapout.common.imp.FileConfig;
import meg.swapout.common.imp.FileConfigManager;

class FileConfigManagerImpl implements FileConfigManager {

	/**
	 * This method is obviously not scaleable or modular, and should be changed
	 * so that a new client doesn't require changing the business logic java
	 * class. This should be configured, not coded. Until I have time, though,
	 * this gets the job done.
	 * 
	 * @param clientkey
	 * @return
	 */
	@Override
	public FileConfig getFileConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportManager.ImportClient.SocGen:
			return new NewSocGenFileConfig();
		default:
			return null;
		}
	}


}
