package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.RawTransaction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RuleAssignment {

	private Category cat;
	private List<RuleTransaction> transactions;


	public RuleAssignment(Category category) {
		this.cat=category;
	}


	public Category getCategory() {
		return this.cat;
	}
	
	public void setCategory(Category cat) {
		this.cat = cat;
	}
	
	public List<RuleTransaction> getTransactions() {
		return this.transactions;
	}
	
	public void setTransactions(List<RuleTransaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(RuleTransaction exp) {
		if (transactions == null) {
			transactions = new ArrayList<RuleTransaction>();
		}
		transactions.add(exp);
	}


	public int getTransactionCount() {
		return transactions!=null?transactions.size():0;

	}
	
}
