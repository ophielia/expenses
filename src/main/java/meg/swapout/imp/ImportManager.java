package meg.swapout.imp;


import meg.swapout.common.FileUtils;
import meg.swapout.common.imp.*;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.services.BankTransactionService;
import meg.swapout.expense.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Component
public class ImportManager {

	private FileConfigManager fcman = new FileConfigManagerImpl();

	private MapConfigManager mcman = new MapConfigManagerImpl();
	//private TransactionManager transman ;
	
	@Autowired
	private
	SearchService searchService;
	
	
	@Autowired
	private
	BankTransactionService transService;

	public final class ImportClient {
		public final static int SocGen = 1;
		public static final int Banesto = 2;
		public static final int BanestoCredit = 3;
		public static final int All = 4;
	}

	private List<Object> importFile(FileConfig config, MapConfig mapconfig, File file) {
		// Get importer
		Importer importer = ImporterFactory.getImporter(config);

		// parse file into placeholders
		List<Placeholder> placeholders = importer != null ? importer.parseFile(file) : null;

		// map objects
		Mapper mapper = MapperFactory.getMapper(mapconfig);
		List<Object> importedobjects = new ArrayList<>();

		for (Placeholder placeholder : placeholders) {
			Object mapped = mapper.mapObject((Placeholder) placeholder);
			importedobjects.add(mapped);
		}

		// return lists
		return importedobjects;
	}

	public List<Object> importTransactions(MultipartFile multipartfile) throws IOException {
		int clientkey = ImportManager.ImportClient.SocGen;
		// get configs for client
		FileConfig config = fcman
				.getFileConfigForClient(clientkey);
		MapConfig mapconfig = mcman
				.getMapConfigForClient(clientkey);

		// handle file
		Date now = new Date();
		File tempFile = File.createTempFile( now.getTime() + "_imp", "txt");
		tempFile.deleteOnExit();
		boolean success = FileUtils.writeMultipartToTempFile(multipartfile,tempFile);

		if (!success) {
			return null;
		}

		// import file into RawTransactions
		List<Object> newobjects = importFile(config, mapconfig, tempFile);

		// Begin persisting objects
		// ---- search for date of most recently entered banktrans for
		//      comparison
		Date mostrecent = searchService.getMostRecentTransDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(mostrecent);
		// ---- set calendar to latest possible time in date
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

		// ---- go through object list - create list without duplicates
		List<RawTransaction> nondups = new ArrayList<>();
		for (Object newobject : newobjects) {
			RawTransaction trans = (RawTransaction) newobject;
			if (mostrecent.before(trans.getTransdate())) {
				// ------ if date is after most recent db banktrans, save the trans
				// immediately
				nondups.add(trans);
			} else {
				// ------ if date is on or before the most recent db banktrans date,
				// check for duplicate in db (on amount, desc, and date)
				boolean duplicate = transService.doesDuplicateExist(trans);
				if (!duplicate) nondups.add(trans);
			}
		}

		// ---- persist non duplicates
		for (RawTransaction trans: nondups) {
			transService.addTransaction(trans);
		}
		// returned List is a list of error / log messages (although
		// this isn't yet really implemented - currently just returns the list of new objects
		// TODO: add result object
		return newobjects;

	}

}
