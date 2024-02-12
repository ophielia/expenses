package meg.swapout.imp;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import meg.swapout.common.imp.AbstractMappingHelper;
import meg.swapout.common.imp.Placeholder;
import meg.swapout.expense.domain.RawTransaction;

public class SocGenMappingHelper extends AbstractMappingHelper {
	final static String pendingRegEx ="(PAIEMENT CARTE DU \\d{2}\\/\\d{2} \\d{2}:\\d{2} )(.*)";
	Pattern pendingPattern = Pattern.compile(pendingRegEx);
	public void doManualMapping(Object mapped, Placeholder placeholder) {
		RawTransaction bank = (RawTransaction) mapped;
		bank.setHascat(new Boolean(false));
		bank.setImportdate(new Date());
		bank.setSource(new Integer(ImportManager.ImportClient.SocGen));

		// set matching label - for pending
		bank.setMatchingLabel(stripMatchingLabel(bank.getDescription()));
	}

	private String stripMatchingLabel(String description) {
		if (description == null) {
			return null;
		}
		if (description.toLowerCase().indexOf("carte ") < 0) {
			return null;
		}
		// matching label only used for card transactions - and
		// may be pending or booked.
		// pending pattern -
		//		PAIEMENT CARTE DU 10/02 20:41 SIELANKA
		// booked pattern
		//      CARTE X5571 08/02 AMAZON EU SARL COMMERCE ELECTRONIQUE
		Matcher pendingMatcher = pendingPattern.matcher(description);
		 if (pendingMatcher.matches()) {
			 return pendingMatcher.group(0);
		 }
		return null;

	}

}
