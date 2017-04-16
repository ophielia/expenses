package meg.swapout.imp;

import java.util.Date;

import meg.swapout.common.imp.AbstractMappingHelper;
import meg.swapout.common.imp.Placeholder;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.imp.ImportManager;

public class SocGenMappingHelper extends AbstractMappingHelper {

	public void doManualMapping(Object mapped, Placeholder placeholder) {
		RawTransaction bank = (RawTransaction) mapped;
		bank.setHascat(new Boolean(false));
		bank.setImportdate(new Date());
		bank.setSource(new Integer(ImportManager.ImportClient.SocGen));
	}

}
