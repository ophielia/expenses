package meg.swapout.expense.controllers;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import meg.swapout.expense.controllers.models.ExpenseListModel;
import meg.swapout.expense.controllers.validators.ExpenseListModelValidator;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/expense/list")
@Controller
public class ExpenseListController {

	private final String sessioncriteria="sessioncriteria";
	
	@Autowired
	SearchService searchService;
	
	@Autowired
	BankTransactionService transService;
		
    @Autowired
	CategoryService categoryService;
    
	@Autowired
	ExpenseListModelValidator modelValidator;
	
	@Autowired
	QuickGroupService quickGroupService;
    
	@RequestMapping(produces = "text/html")
    public String showList( Model uiModel, HttpServletRequest request) {

		ExpenseListModel model = populateExpenseList(request);
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("expenseModel",model);
		return "expenses";
    }

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String searchExpenses(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria, criteria);
		// return
		return "redirect:/expense/list";
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html",params = {"clearCriteria"})
	public String clearSearchCriteria(ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = getDefaultCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		model.setSearchCriteria(criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);

		uiModel.addAttribute("expenseModel",model);

		return "expenses";
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
		return "expenses";
	}

	@RequestMapping(method = RequestMethod.POST, params = "updatecat",produces = "text/html")
	public String updateMultiCategories(@ModelAttribute("expenseListModel") ExpenseListModel model, Model uiModel, BindingResult bindingResult, HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);

		// error checking here
		modelValidator.validateUpdateCategory(model, bindingResult);


		// get expenses to update
		if (bindingResult.hasErrors()) {
			// retrieve and set list
			List<ExpenseDao> list = searchService.getExpenses(criteria);
			model.setExpenses(list);
			uiModel.addAttribute("expenseModel",model);

			// return
			return "expenses";
		}

		// update expenses
		List<String> toupdate = model.getCheckedExpenseIds();
		transService.assignCategoriesToExpenses(model.getBatchUpdate(), toupdate);

		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("expenseModel",model);
		// return
		return "redirect:/expense/list";
	}


	/*

	
	@RequestMapping(method = RequestMethod.PUT, params = "batchQuickGroup",produces = "text/html")
	public String updateMultiQuickGroups(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,BindingResult bindingResult,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		
		// error checking here
		modelValidator.validateUpdateQuickGroup(model, bindingResult);
		
		
		// get expenses to update
		List<String> toupdate = model.getCheckedExpenseIds();
		if (bindingResult.hasErrors()) {
			// retrieve and set list
			List<ExpenseDao> list = searchService.getExpenses(criteria);
			model.setExpenses(list);		
			// return
			return "expenses";
		}
		
		// update expenses
		transService.assignQuickGroupToExpenses(model.getBatchQuickgroup(), toupdate);
		
		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);		
		// return
		return "expenses";
	}
	
	@RequestMapping(method = RequestMethod.PUT,params="sort" ,produces = "text/html")
	public String sortExpenses(@RequestParam("sort") String sorttype,@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		if (sorttype!=null) {
			criteria.setSortfield(sorttype);
		}
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);

		return "expenses";
	
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.setAutoGrowCollectionLimit(100024);
	}
	


*/

	private ExpenseCriteria getDefaultCriteria() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setDateRangeByType(DateRangeType.CurrentYear);
		criteria.setCategorizedType(CategorizedType.All);
		criteria.setTransactionType(TransactionType.Debits);
		criteria.setShowSubcats(false);
		criteria.setSortdir(ExpenseCriteria.SortDirection.Desc);
		criteria.setSortfield(ExpenseCriteria.SortType.Date);
		return criteria;
	}

	private ExpenseListModel populateExpenseList(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ExpenseCriteria criteria = (ExpenseCriteria) session.getAttribute(sessioncriteria);
		if (criteria==null) {
			criteria = getDefaultCriteria();
			session.setAttribute(sessioncriteria,criteria);
		}
		ExpenseListModel model = new ExpenseListModel(criteria);
		
		return model;
	}
	
	
	@ModelAttribute("categorylist")
	protected List<Category> referenceCategoryData(HttpServletRequest request, Object command,
												   Errors errors) throws Exception {
		List<Category> list = categoryService.listAllCategories(false);
		
		
		// return model
		return list;
	}	
	
	@ModelAttribute("quickgrouplist")
	protected List<QuickGroup> referenceQuickGroupData(HttpServletRequest request, Object command,
													   Errors errors) throws Exception {
		List<QuickGroup> list = quickGroupService.listAllQuickGroups();
		
		// return model
		return list;
	}		

 }
