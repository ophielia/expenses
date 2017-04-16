package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.RawTransaction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class RuleAssignment {

	private Category cat;
	private List<RawTransaction> transactions;
	
	public RuleAssignment(Category cat, List<RawTransaction> transactions) {
		this.cat = cat;
		this.transactions = transactions;
	}


	public RuleAssignment(Category category) {
		this.cat=category;
	}


	public Category getCategory() {
		return this.cat;
	}
	
	public void setCategory(Category cat) {
		this.cat = cat;
	}
	
	public List<RawTransaction> getTransactions() {
		return this.transactions;
	}
	
	public void setTransactions(List<RawTransaction> transactions) {
		this.transactions = transactions;
	}

	public Long getCategoryId() {
		return this.cat.getId();
	}
	
	public void addTransactions(List<RawTransaction> newtrans, Hashtable<Long,Long> assigned) {
		if (this.transactions==null) {
			this.transactions = new
					ArrayList<RawTransaction>();
		}
		for (RawTransaction tr:newtrans) {
			if (!assigned.containsKey(tr.getId())) {
				this.transactions.add(tr);
				assigned.put(tr.getId(), new Long(1));
			}
		}
	}


	public void addTransaction(RawTransaction exp) {
		if (transactions == null) {
			transactions = new ArrayList<RawTransaction>();
		}
		transactions.add(exp);
	}


	public int getTransactionCount() {
		return transactions!=null?transactions.size():0;
	}
	
}
