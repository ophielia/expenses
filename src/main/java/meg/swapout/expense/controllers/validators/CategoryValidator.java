package meg.swapout.expense.controllers.validators;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Component
public class CategoryValidator implements Validator {

	@Autowired
	CategoryService categoryService;

	@Override
	public boolean supports(Class clazz) {
		return Category.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Category category = (Category) target;

		// check standard fields
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = factory.getValidator();
		Set<ConstraintViolation<Category>> valerrors = validator
				.validate(category);

		// put JSR-303 errors into standard errors
		for (ConstraintViolation<Category> cv : valerrors) {
			errors.rejectValue(cv.getPropertyPath().toString(),
					cv.getMessageTemplate());
		}
		
		// check circular references
		Long parentCategoryId = category.getParentCategory().getId();
		boolean hascircular = categoryService.hasCircularReference(parentCategoryId, category);
		if (hascircular) {
			errors.rejectValue("parentcatid", "error_circularref","doesn't work");
		}
	}

}
