package meg.swapout.web.controllers;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.services.*;
import meg.swapout.web.controllers.models.ExpenseListModel;
import meg.swapout.web.controllers.models.RoundingModel;
import meg.swapout.web.controllers.validators.ExpenseListModelValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@RequestMapping("/rounding/list")
@Controller
public class RoundingController {

	private final String sessioncriteria="sessioncriteria";
	
	@Autowired
	SearchService searchService;

	@Autowired
	TransactionDetailService transactionDetailService;

	@Autowired
	ExpenseListModelValidator modelValidator;

	@RequestMapping(produces = "text/html")
    public String showList( Model uiModel, HttpServletRequest request) {
		ExpenseCriteria criteria = getDefaultCriteria();
		RoundingModel model = new RoundingModel();
		model.setSearchCriteria(criteria);
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("roundingModel",model);
		return "rounding";
    }

	@RequestMapping(value="/rounded",produces = "text/html")
	public String showRoundedList( Model uiModel, HttpServletRequest request) {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.showRoundedOnly(true);
		criteria.setCategorizedType(CategorizedType.OnlyCategorized);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		RoundingModel model = new RoundingModel();
		model.setSearchCriteria(criteria);
		model.setExpenses(list);
		uiModel.addAttribute("roundingModel",model);
		return "rounded";
	}



	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String searchExpenses(@ModelAttribute("expenseListModel") RoundingModel model,Model uiModel,HttpServletRequest request) {
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("roundingModel",model);
		return "rounding";
	}

	@RequestMapping(method = RequestMethod.POST,params="sort" ,produces = "text/html")
	public String sortExpenses(@RequestParam("sortField") String newSort, ExpenseListModel model,Model uiModel,HttpServletRequest request) {

		ExpenseCriteria criteria = model.getSearchCriteria();

		String currentSort=criteria.getSortfield();
		if (currentSort!=null && newSort!=null && newSort.equals(currentSort)) {
			Long newDirection = model.getSortDirection()== ExpenseCriteria.SortDirection.Desc?ExpenseCriteria.SortDirection.Asc: ExpenseCriteria.SortDirection.Desc;
			criteria.setSortdir(newDirection);
		}
		criteria.setSortfield(newSort);

		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		model.setSearchCriteria(criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);
		uiModel.addAttribute("expenseModel",model);
		return "rounding";
	}

	@RequestMapping(method = RequestMethod.POST, params = "round",produces = "text/html")
	public String roundTransactions(@ModelAttribute("expenseListModel") ExpenseListModel model, Model uiModel, BindingResult bindingResult, HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();

		// get expenses to update
		if (bindingResult.hasErrors()) {
			// retrieve and set list
			List<ExpenseDao> list = searchService.getExpenses(criteria);
			model.setExpenses(list);
			uiModel.addAttribute("expenseModel",model);

			// return
			return "rounding";
		}

		// update expenses
		List<String> toupdate = model.getCheckedExpenseIds();
		transactionDetailService.roundTransactions( toupdate);

		// return
		return "redirect:/rounding/list";
	}

	private ExpenseCriteria getDefaultCriteria() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setDateRangeByType(DateRangeType.CurrentYear);
		criteria.setCategorizedType(CategorizedType.OnlyCategorized);
		criteria.setTransactionType(TransactionType.Debits);
		criteria.setShowSubcats(false);
		criteria.setSortdir(ExpenseCriteria.SortDirection.Desc);
		criteria.setSortfield(ExpenseCriteria.SortType.Date);
		criteria.setForRounding(true);
		return criteria;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowCollectionLimit(100024);
	}

 }
