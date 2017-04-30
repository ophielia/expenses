package meg.swapout.web.controllers.models;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.services.CategorizedType;
import meg.swapout.expense.services.DateRangeType;
import meg.swapout.expense.services.ExpenseCriteria;
import meg.swapout.expense.services.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RoundingModel implements Serializable {

	private static final long serialVersionUID = 1L;



	private List<ExpenseDao> expenses;
	private List<Boolean> checked;
	private List<String> idref;
	private Category batchUpdate;
	private Boolean showSubcats;
	private QuickGroup batchQuickgroup;


	private ExpenseCriteria searchCriteria;

	public RoundingModel() {
		this.searchCriteria = new ExpenseCriteria();
	}

	public RoundingModel(ExpenseCriteria searchCriteria) {
		super();
		this.searchCriteria = searchCriteria;
	}
	
	public List<ExpenseDao> getExpenses() {
		return expenses;
	}
	public void setExpenses(List<ExpenseDao> expenses) {
		this.expenses = expenses;
		if (expenses!=null && expenses.size()>0) {
			// initialize checked list
			createCheckedAndIdSlots(expenses.size());
		}
	}
	public List<Boolean> getChecked() {
		return checked;
	}
	public void setChecked(List<Boolean> checked) {
		this.checked = checked;
	}
	public List<String> getIdref() {
		return idref;
	}

	public void setIdref(List<String> idref) {
		this.idref = idref;
	}


	// getters and setters for criteria fields
	public DateRangeType getDateRangeByType() {
		return searchCriteria.getDateRangeByType();
	}
	public void setDateRangeByType(DateRangeType daterangetype) {
		searchCriteria.setDateRangeByType(daterangetype);
	}

	public CategorizedType getCategorizedType() {
		return searchCriteria.getCategorizedType();
	}
	public void setCategorizedType(CategorizedType daterangetype) {
		searchCriteria.setCategorizedType(daterangetype);
	}	
	
	public Category getCategory() {
		return searchCriteria.getCategory();
	}
	public void setCategory(Category category) {
		searchCriteria.setCategory(category);
			}
	
	public TransactionType getTransactionType() {
		return searchCriteria.getTransactionType();
	}

	public void setTransactionType(TransactionType transactionType) {
		searchCriteria.setTransactionType(transactionType);
	}
	public Category getBatchUpdate() {
		return batchUpdate;
	}

	public void setBatchUpdate(Category batchUpdate) {
		this.batchUpdate = batchUpdate;
	}

	public void setForRounding(Boolean forRounding) {searchCriteria.setForRounding(forRounding);}

	public Boolean getForRounding() {return searchCriteria.getForRounding();}

	public Boolean getShowSubcats() {
		return searchCriteria.getShowSubcats();
	}

	public void setShowSubcats(Boolean showSubcats) {
		this.searchCriteria.setShowSubcats(showSubcats);
	}

	public QuickGroup getBatchQuickGroup() {
		return batchQuickgroup;
	}



	public void setBatchQuickGroup(QuickGroup batchQuickgroup) {
		this.batchQuickgroup = batchQuickgroup;
	}

	public List<String> getCheckedExpenseIds() {
		// make new empty list (for ExpenseDao)
		List<String> checkedexp = new ArrayList<String>();
		if (idref!=null) {
			// go through checked list
			for (int i=0;i<checked.size();i++) {
				// if checked is true, add expenseDao at same slot to checkedlist
				Boolean test = checked.get(i);
				if (test!=null && test) {
					checkedexp.add(idref.get(i));
				}
			}
		}
		// return checked list
		return checkedexp;
	}

	private void createCheckedAndIdSlots(int size) {
		checked = new ArrayList<Boolean>();
		//idref = new ArrayList<Long>();
		for (int i=0;i<size;i++) {
			checked.add(false);
			//idref.add(expenses.get(i).getId());
		}

	}

	public void setSortField(String sortField) {
		searchCriteria.setSortfield(sortField);
	}

	public String getSortField() {
		return this.searchCriteria.getSortfield();
	}

	public Long getSortDirection() {
		return this.searchCriteria.getSortdir();
	}

	public void setSortDirection(Long sortDirection) {
		this.searchCriteria.setSortdir(sortDirection);
	}

	public void setSearchCriteria(ExpenseCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public ExpenseCriteria getSearchCriteria() {
		return searchCriteria;
	}
}
