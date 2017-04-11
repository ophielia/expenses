package meg.swapout.common.imp;

import java.util.List;

public interface MapConfig {

	String getDestinationClassName();

	List<FieldMapping> getMappings();

	String getHelperClassName();

}
