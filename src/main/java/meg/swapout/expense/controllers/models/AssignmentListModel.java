package meg.swapout.expense.controllers.models;

import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.services.RuleAssignment;

import java.util.ArrayList;
import java.util.List;



public class AssignmentListModel {


	private List<RuleAssignment> assignbycategory;
	private List<Boolean> checked;


	public AssignmentListModel(List<RuleAssignment> ruleassignments) {
		super();
		setRuleAssignments(ruleassignments);
	}

	public List<RuleAssignment> getRuleAssignments() {
		return assignbycategory;
	}

	private void setRuleAssignments(List<RuleAssignment> ruleassignment) {
		this.assignbycategory = ruleassignment;
		if (ruleassignment != null) {
			// get total number of transactions
			int total = 0;
			for (RuleAssignment rule : ruleassignment) {
				total += rule.getTransactionCount();
			}
			checked = new ArrayList<>();
			for (int i = 0; i < total; i++) {
				checked.add(true);
			}
		}
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
		for (RuleAssignment assign : assignbycategory) {
			// initialize list of selectedtrans
			List<RawTransaction> selectedtrans = new ArrayList<>();
			// loop through transactions
			for (RawTransaction banktrans : assign.getTransactions()) {
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
