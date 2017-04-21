package meg.swapout.reporting;

import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.SearchService;
import meg.swapout.expense.services.TargetService;
import meg.swapout.reporting.elements.ReportData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static meg.swapout.reporting.ReportType.*;

@Service
public class ReportDataFactory {
	
    @Value("${document.tmp.path}")
    private String tmpdir;

    @Value("${document.image.weblinkbase}")
    private String imageweblink;
    
    // TODO remove this @Value("${document.image.fullweblinkbase}")
	// TODO remove this private String fullimageweblink;
	
	@Autowired
	private SearchService searchService;

	@Autowired
	protected CategoryService categoryService;

	@Autowired
	protected TargetService targetService;
	
	

	
	public ReportData createReportData(ReportCriteria reportCriteria) {
		BankReportData report = null;
		// get reporttype
		ReportType reportType = reportCriteria.getReportType();
		// set image directory (to tmpdir)
		reportCriteria.setImageDir(tmpdir);
		// set weblinkbase
		reportCriteria.setImageLink(imageweblink);
		// get appropriate report
		if (reportType!=null) {
			switch (reportType) {
				case FullMonth:
					report = new FullMonthReportData(reportCriteria,searchService,categoryService,targetService)	;				break;
				case Yearly:
					report = new YearlyReportData(reportCriteria,searchService,categoryService,targetService);
					break;
				case YearlyTarget:
					report = new YearlyTargetStatusReportData(reportCriteria,searchService,categoryService,targetService);
					break;
				case MonthlyTarget:
					report = new MonthlyTargetReportData(reportCriteria,searchService,categoryService,targetService);					break;

			}

			if (report!=null) {
				report.crunchNumbers();
				
			}
			return report;
		}

		return null;
	}


}
