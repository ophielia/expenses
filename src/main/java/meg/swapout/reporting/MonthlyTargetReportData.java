package meg.swapout.reporting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.services.*;
import meg.swapout.reporting.elements.ChartData;
import meg.swapout.reporting.elements.ReportElement;
import meg.swapout.reporting.elements.ReportLabel;

public class MonthlyTargetReportData extends BankReportData {

	public MonthlyTargetReportData(ReportCriteria reportCriteria,
								   SearchService searchService, CategoryService categoryService,
								   TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}

	@Override
	public void crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(TransactionType.Debits);
		criteria.setCompareType(getReportCriteria().getCompareType());
		String month = getReportCriteria().getMonth();
		Date startdate = null;
		Date enddate = null;
		int daycount = 0;
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");
		Date start;
		try {
			start = dateformat.parse(month);
			Calendar cal = Calendar.getInstance();
			// get first of month, first of next month
			cal.setTime(start);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startdate = cal.getTime();
			cal.add(Calendar.MONTH, 1);
			enddate = cal.getTime();
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(enddate);
			daycount = getDayCount(criteria);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(getReportCriteria()
				.getExcludeNonExpense());

		// pull all expenses
		List<ExpenseDao> allexpenses = searchService.getExpenses(criteria);
		sortAndCategorizeExpenses(allexpenses);

		ChartData data = buildChartDataFromExpenseList(allexpenses);

		// chartdata now created, make into report element, and add to
		// reportdata
		ReportElement re = new ReportElement();
		re.setChart(data);
		re.setTag("allexpenses");
		addElement(re);

		// run target report
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		ReportElement targets = crunchNumbersTargets(criteria, month);
		targets.setTag("targets");
		addElement(targets);

		// Add labels title, rundate
		ReportLabel title = new ReportLabel("lbltitle",
				BankReportData.MONTHLY_TARGET_TITLE + " - " + month);
		String rangestr = daydateformat.format(startdate) + " - "
				+ daydateformat.format(enddate) + "(" + daycount + " "
				+ BankReportData.DAYS + ")";
		ReportLabel range = new ReportLabel("lblrange", rangestr);
		ReportLabel allexp = new ReportLabel("lblallexpenses",
				BankReportData.ALL_EXPENSES);
		ReportLabel targetlbl = new ReportLabel("lbltarget",
				BankReportData.TARGET_STATUS);

		addLabel(title);
		addLabel(range);
		addLabel(allexp);
		addLabel(targetlbl);

	}

	@Override
	public String getXslTransformFilename() {
		return "monthlytarget.xsl";
	}

	@Override
	public String getJspViewname() {
		return "monthlytargetoutput";
	}

}