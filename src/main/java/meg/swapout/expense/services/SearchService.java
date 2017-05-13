package meg.swapout.expense.services;

import meg.swapout.expense.domain.ExpenseDao;

import java.util.Date;
import java.util.List;


public interface SearchService {

	List<ExpenseDao> getExpenses(ExpenseCriteria criteria);
	
	List<ExpenseDao> getAllExpenses();
	
	List<CategorySummary> getExpenseTotalByMonthAndCategory(
            ExpenseCriteria criteria);
	
	List<CategorySummary> getExpenseTotalByMonth(
            ExpenseCriteria criteria);
	

	List<CategorySummary> getExpenseTotalByYear(
            ExpenseCriteria criteria);

	
	List<ExpenseDao> getExpenseListByIds(List<String> idlist) ;

	Date getFirstTransDate();
	
	Date getMostRecentTransDate();

	List<CategorySummary> getExpenseTotalByYearAndCategory(
            ExpenseCriteria criteria);

	List<CategorySummary> getExpenseTotal(
            ExpenseCriteria criteria);

    CategorySummary getCategorySummaryByMonthYear(ExpenseCriteria criteria);
}