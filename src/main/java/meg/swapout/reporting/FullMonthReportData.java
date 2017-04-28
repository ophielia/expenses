package meg.swapout.reporting;

import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.services.*;
import meg.swapout.reporting.elements.ChartData;
import meg.swapout.reporting.elements.ReportElement;
import meg.swapout.reporting.elements.ReportLabel;

import java.util.HashMap;
import java.util.List;


public class FullMonthReportData extends BankReportData {

    FullMonthReportData(ReportCriteria reportCriteria,
                        SearchService searchService, CategoryService categoryService,
                        TargetService targetService) {
        super(reportCriteria, searchService, categoryService, targetService);
    }


    @Override
    public void crunchNumbers() {
        // fill up this ReportData with, uh, data....


        // fill criteria object
        ExpenseCriteria criteria = new ExpenseCriteria();
        criteria.setTransactionType(TransactionType.Debits);
        criteria.setCompareType(getReportCriteria().getCompareType());
        String month = getReportCriteria().getMonth();
        // parse into date
        criteria = setMonthInCriteria(month, criteria);

        criteria.setExcludeNonExpense(getReportCriteria().getExcludeNonExpense());

        // run summary report
        getReportCriteria().setBreakoutLevel(1L);
        ReportElement summary = crunchNumbersSummary(criteria, false);


        // run year to date report
        criteria.clearCategoryLevelList();
        criteria.clearCategoryLists();
        criteria.setCategory(null);
        ReportElement yeartodate = crunchNumbersMonthlyComp(criteria, true, "MM-yyyy");

        // pull all expenses
        List<ExpenseDao> allexpenses = searchService.getExpenses(criteria);
        ChartData data = buildChartDataFromExpenseList(allexpenses);

        // chartdata now created, make into report element, and add to reportdata
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

        // run category breakouts for all main categories
        HashMap<Long, String> catlkup = new HashMap<>();
        criteria.setCategorizedType(CategorizedType.All);
        ReportElement allcategory = new ReportElement();
        ReportElement detailedcategories = new ReportElement();
        List<CategoryLevel> categories = categoryService
                .getCategoriesUpToLevel(1);
        if (categories != null) {
            for (CategoryLevel catlvl : categories) {
                ReportElement catre = crunchNumbersCategory(criteria, catlvl, true,6);
                if (catre == null) {
                    continue;
                }

                catre.setDisplay(catlvl.getCategory().getName());

                // does this ReportElement have a subelement?
                if (catre.getMembers()!=null&&!catre.getMembers().isEmpty()) {
                    ReportElement detailed = catre.getMembers().get(0);
                    detailedcategories.addMember(detailed);
                    catre.setMembers(null);
                }
                allcategory.addMember(catre);
            }
        }

        // add summary element
        allcategory.setTag("allcategories");
        addElement(allcategory);


        // add summary element
        summary.setTag("summary");
        addElement(summary);

        // add year to date element
        yeartodate.setTag("yeartodate");
        addElement(yeartodate);

        // add year to date element
        detailedcategories.setTag("detailedcategories");
        addElement(detailedcategories);

        // Add labels title, rundate
        ReportLabel title = new ReportLabel("lbltitle", BankReportData.FULL_MONTH_TITLE + " - " + month);
        String rangestr = daydateformat.format(criteria.getDateStart()) + " - " + daydateformat.format(criteria.getDateEnd()) + "(" + criteria.getDayCount() + " " + BankReportData.DAYS + ")";
        ReportLabel range = new ReportLabel("lblrange", rangestr);
        ReportLabel allexp = new ReportLabel("lblallexpenses", BankReportData.ALL_EXPENSES);
        ReportLabel targetlbl = new ReportLabel("lbltarget", BankReportData.TARGET_STATUS);
        ReportLabel summarylbl = new ReportLabel("lblsummary", BankReportData.SUMMARY_BY_CATEGORY);
        ReportLabel yeartodatelbl = new ReportLabel("lblyeartodate", BankReportData.YEAR_TO_DATE);
        ReportLabel detailslbl = new ReportLabel("lbldetail", BankReportData.CATEGORY_DETAIL);


        addLabel(title);
        addLabel(range);
        addLabel(allexp);
        addLabel(targetlbl);
        addLabel(summarylbl);
        addLabel(yeartodatelbl);


    }




    @Override
    public String getXslTransformFilename() {
        return "fullmonth.xsl";
    }

    @Override
    public String getJspViewname() {
        return "fullmonthoutput";
    }


}