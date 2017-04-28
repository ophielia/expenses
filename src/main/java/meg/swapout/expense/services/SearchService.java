package meg.swapout.expense.services;

import meg.swapout.expense.domain.ExpenseDao;

import java.util.Date;
import java.util.List;


public interface SearchService {

	public abstract List<ExpenseDao> getExpenses(ExpenseCriteria criteria);
	
	public abstract List<ExpenseDao> getAllExpenses();
	
	public abstract List<CategorySummary> getExpenseTotalByMonthAndCategory(
			ExpenseCriteria criteria);
	
	public abstract List<CategorySummary> getExpenseTotalByMonth(
			ExpenseCriteria criteria);
	

	public abstract List<CategorySummary> getExpenseTotalByYear(
			ExpenseCriteria criteria);

	
	public List<ExpenseDao> getExpenseListByIds(List<String> idlist) ;	

	public abstract Date getFirstTransDate();
	
	public abstract Date getMostRecentTransDate();

	public abstract List<CategorySummary> getExpenseTotalByYearAndCategory(
			ExpenseCriteria criteria);

	public abstract List<CategorySummary> getExpenseTotal(
			ExpenseCriteria criteria);

    CategorySummary getCategorySummaryByMonthYear(ExpenseCriteria criteria);
}