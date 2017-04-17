package meg.swapout.web.controllers.models;

import meg.swapout.expense.services.RuleAssignment;
import meg.swapout.expense.services.RuleTransaction;

import java.util.ArrayList;
import java.util.List;



public class AssignmentListModel {


	private List<RuleAssignment> ruleAssignments;
	private List<Boolean> checked;




	public AssignmentListModel(List<RuleAssignment> ruleassignments) {
		super();
		setRuleAssignments(ruleassignments);
	}

	public List<RuleAssignment> getRuleAssignments() {
		return ruleAssignments;
	}

	private void setRuleAssignments(List<RuleAssignment> ruleassignment) {
		this.ruleAssignments = ruleassignment;

		if (ruleassignment == null) {
			return;
		}

		int count=0;
		checked = new ArrayList<>();
		for (RuleAssignment rule : ruleassignment) {
			for (RuleTransaction ruleTransaction:rule.getTransactions()) {
				ruleTransaction.setCount(count);
				count++;
				checked.add(true);
			}
		}

/*
		if (ruleassignment != null) {
			// get total number of transactions
			int total = 0;
			for (RuleAssignment rule : ruleassignment) {
				total += rule.getTransactionCount();
			}

			for (int i = 0; i < total; i++) {
				checked.add(true);
			}

		}*/
	}

	public List<Boolean> getChecked() {
		return checked;
	}
	public void setChecked(List<Boolean> checked) {
		this.checked = checked;
	}

	public List<RuleAssignment> getCheckedRuleAssignments() {
		List<RuleAssignment> chkdassignments = new ArrayList<>();

		// initialize counter
		int i = 0;
		// loop through existing assignments
		for (RuleAssignment assign : ruleAssignments) {
			// initialize list of selectedtrans
			List<RuleTransaction> selectedtrans = new ArrayList<>();
			// loop through transactions
			for (RuleTransaction banktrans : assign.getTransactions()) {
				// check counter against checked list
				boolean ischecked = this.checked.get(i);

				if (ischecked) {
					// if checked, add to selected trans
					selectedtrans.add(banktrans);
				}
				// after every transaction increment counter
				i++;
			}
			// end transaction loop

			// if selectedtrans has transactions, make new RuleAssignment
			if (!selectedtrans.isEmpty()) {
				// set category, and transactions
				RuleAssignment newassign = new RuleAssignment(
						assign.getCategory());
				newassign.setTransactions(selectedtrans);
				// add to chkdassignments
				chkdassignments.add(newassign);
			}

		}
		// end ruleassignment loop

		// return checkedassignments
		return chkdassignments;
	}

	
	
	

	
	


}
