package meg.swapout.imp;


import meg.swapout.common.imp.FieldMapping;
import meg.swapout.common.imp.MapConfig;

import java.util.ArrayList;
import java.util.List;

class NewSocGenMapConfig implements MapConfig {

	public String getDestinationClassName() {
		return "meg.swapout.expense.domain.RawTransaction";
	}

	public List<FieldMapping> getMappings() {
		List<FieldMapping> mappings = new ArrayList<>();

		// map a couple of fields here - just a test for now

		FieldMapping map = new FieldMapping();
		map.setFromFieldTag("field1");
		map.setSetterMethod("setTransdate");
		mappings.add(map);

		map = new FieldMapping();
		map.setFromFieldTag("field3");
		map.setSetterMethod("setDetail");
		mappings.add(map);

		map = new FieldMapping();
		map.setFromFieldTag("field4");
		map.setSetterMethod("setAmount");
		mappings.add(map);

		map = new FieldMapping();
		map.setFromFieldTag("field3");
		map.setSetterMethod("setDescription");
		mappings.add(map);

		return mappings;
	}

	public String getHelperClassName() {
		return "meg.swapout.imp.SocGenMappingHelper";
	}

}
