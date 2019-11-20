package meg.swapout.imp;


import meg.swapout.common.imp.MapConfig;
import meg.swapout.common.imp.MapConfigManager;

class MapConfigManagerImpl implements MapConfigManager {

	
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
	public MapConfig getMapConfigForClient(int clientkey) {
		switch (clientkey) {
		case ImportManager.ImportClient.SocGen:
			return new NewSocGenMapConfig();
		default:
			return null;
		}
	}

	
}
