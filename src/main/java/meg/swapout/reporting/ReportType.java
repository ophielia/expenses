package meg.swapout.reporting;

import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.SearchService;
import meg.swapout.expense.services.TargetService;
import meg.swapout.reporting.elements.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public enum ReportType {
	
	MonthlyTarget,
	YearlyTarget,
	FullMonth,
	Yearly

}
