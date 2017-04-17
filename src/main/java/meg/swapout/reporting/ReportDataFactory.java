package meg.swapout.reporting;

import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.SearchService;
import meg.swapout.expense.services.TargetService;
import meg.swapout.reporting.elements.ReportData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportDataFactory {
	
    @Value("${document.tmp.path}")
    private String tmpdir;

    @Value("${document.image.weblinkbase}")
    private String imageweblink;
    
    @Value("${document.image.fullweblinkbase}")
    private String fullimageweblink;    
	
	@Autowired
	private SearchService searchService;

	@Autowired
	protected CategoryService categoryService;

	@Autowired
	protected TargetService targetService;
	
	
	public final static class ReportType {
		public static final Long MonthlyTarget = 1L;
		public static final Long YearlyTargetStatus = 2L;
		public static final Long FullMonth = 3L;
		public static final Long Yearly = 4L;
	}	
	
	public ReportData createReportData(ReportCriteria reportCriteria) {
		BankReportData report = null;
		// get reporttype
		Long reporttype = reportCriteria.getReportType();
		// set image directory (to tmpdir)
		reportCriteria.setImageDir(tmpdir);
		// set weblinkbase
		reportCriteria.setImageLink(imageweblink);
		reportCriteria.setFullImageLink(fullimageweblink);
		// get appropriate report
		if (reporttype!=null) {
			// get appropriate report for reporttype
			if (reporttype.longValue()==ReportType.FullMonth.longValue()) {
				report = new FullMonthReportData(reportCriteria,searchService,categoryService,targetService);
			} else if (reporttype.longValue()==ReportType.MonthlyTarget.longValue()) {
				report = new MonthlyTargetReportData(reportCriteria,searchService,categoryService,targetService);
			}  else if (reporttype.longValue()==ReportType.YearlyTargetStatus.longValue()) {
				report = new YearlyTargetStatusReportData(reportCriteria,searchService,categoryService,targetService);
			}  else if (reporttype.longValue()==ReportType.Yearly.longValue()) {
				report = new YearlyReportData(reportCriteria,searchService,categoryService,targetService);
			}

			if (report!=null) {
				report.crunchNumbers();
				
			}
			return report;
		}

		return null;
	}


}
