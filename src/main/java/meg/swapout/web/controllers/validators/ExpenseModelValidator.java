package meg.swapout.web.controllers.validators;

import meg.swapout.web.controllers.models.ExpenseModel;
import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.repositories.CategoryRepository;
import meg.swapout.expense.repositories.TargetDetailRepository;
import meg.swapout.expense.repositories.TargetRepository;
import meg.swapout.expense.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class ExpenseModelValidator implements Validator {

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	TargetDetailRepository targetDetRepo;

	@Autowired
	TargetRepository targetGrpRepo;
	
	@Autowired
	TargetService targetService;

	@Override
	public boolean supports(Class clazz) {
		return ExpenseModel.class.equals(clazz);
	}


	public void validate(Object target,Errors errors) {
		// empty method
	}

	public void validateSaveDetails(Object target, Errors errors) {
		ExpenseModel model = (ExpenseModel) target;
		
		// get the transaction
		RawTransaction transaction=model.getTransaction();

		// get the total of the details
		List<CategorizedTransaction> details = model.getDetails();
		if (details == null || transaction==null) {
			// return error
			errors.reject("no details or transaction");
		}

		// check that the transaction total equals the details total
		double detailstotal = 0;
		for (CategorizedTransaction detail:details) {
			detailstotal+= detail.getAmount()!=null?detail.getAmount():0;
		}
		double transactionamount = transaction.getAmount();
		detailstotal=Math.abs(detailstotal);
		transactionamount=Math.abs(transactionamount);

		// otherwise return error
		if (detailstotal!=transactionamount) {
			// return error
			errors.reject("details don't add up!");
		}

	}





}
