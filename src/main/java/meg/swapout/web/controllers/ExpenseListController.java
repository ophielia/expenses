package meg.swapout.web.controllers;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import meg.swapout.web.controllers.models.ExpenseListModel;
import meg.swapout.web.controllers.validators.ExpenseListModelValidator;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/expense/list")
@Controller
public class ExpenseListController {

	private final String sessioncriteria="sessioncriteria";
	
	@Autowired
	SearchService searchService;

	@Autowired
	TransactionDetailService transactionDetailService;
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

	@RequestMapping(method = RequestMethod.POST, produces = "text/html",params = {"unspentCash"})
	public String setUnspentCash(ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		Category unspent = categoryService.getCategoryByName("Cash - unspent");
		criteria.setCategory(unspent);
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria, criteria);

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
		transactionDetailService.assignCategoriesToTransactionDetails(model.getBatchUpdate(), toupdate);

		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("expenseModel",model);
		// return
		return "redirect:/expense/list";
	}

	@RequestMapping(method = RequestMethod.POST, params = "updatequickgroups",produces = "text/html")
	public String updateMultiQuickGroups(@ModelAttribute("expenseListModel") ExpenseListModel model, Model uiModel, BindingResult bindingResult, HttpServletRequest request) {
		ExpenseCriteria criteria = model.getSearchCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);

		// error checking here
		modelValidator.validateUpdateQuickGroup(model, bindingResult);


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
		transactionDetailService.assignQuickGroupToTransactionDetails(model.getBatchQuickGroup(), toupdate);

		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(model.getSearchCriteria());
		model.setExpenses(list);
		uiModel.addAttribute("expenseModel",model);
		// return
		return "redirect:/expense/list";
	}

	private ExpenseCriteria getDefaultCriteria() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setDateRangeByType(DateRangeType.CurrentMonth);
		criteria.setSearchText("");
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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowCollectionLimit(100024);
	}


	@ModelAttribute("categorylist")
	protected List<Category> referenceCategoryData(HttpServletRequest request, Object command,
												   Errors errors) throws Exception {
		List<Category> list = categoryService.listAllCategories(true);
		
		
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
