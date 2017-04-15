package meg.swapout.expense.controllers.validators;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import meg.swapout.expense.controllers.models.ExpenseListModel;
import meg.swapout.expense.repositories.CategoryRepository;
import meg.swapout.expense.repositories.TargetDetailRepository;
import meg.swapout.expense.repositories.TargetRepository;
import meg.swapout.expense.services.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

@Component
public class ExpenseListModelValidator implements Validator {

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
		return ExpenseListModel.class.equals(clazz);
	}


	public void validate(Object target,Errors errors) {
		// empty method
	}

	public void validateUpdateCategory(Object target, Errors errors) {
		ExpenseListModel model = (ExpenseListModel) target;
		
		// check that the category has been set
		if (model.getBatchUpdate()==null || model.getBatchUpdate().getId()==0) {
			errors.rejectValue("batchUpdate",
					"field_required");
		}

	}
	
	public void validateUpdateQuickGroup(Object target, Errors errors) {
		ExpenseListModel model = (ExpenseListModel) target;
		
		// check that the category has been set
		if (model.getBatchQuickGroup()==null ) {
			errors.rejectValue("batchQuickgroup",
					"field_required");
		}

	}



}
