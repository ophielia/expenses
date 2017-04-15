package meg.swapout.expense.controllers;

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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@RequestMapping("/old/expense/list")
@Controller
public class ExpenseListControllerOrig {

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
    /*
	@RequestMapping(produces = "text/html")
    public String showList(ExpenseListModel model, Model uiModel, HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		//List<ExpenseDao> list = searchService.getExpenses(criteria);
		//model.setExpenses(list);
		return "expenselist";
    }
	
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String searchExpenses(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);

		return "expense/list";
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, params = "updatecat",produces = "text/html")
	public String updateMultiCategories(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,BindingResult bindingResult,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		
		// error checking here
		modelValidator.validateUpdateCategory(model, bindingResult);
		
		
		// get expenses to update
		List<String> toupdate = model.getCheckedExpenseIds();
		if (bindingResult.hasErrors()) {
			// retrieve and set list
			List<ExpenseDao> list = searchService.getExpenses(criteria);
			model.setExpenses(list);		
			// return
			return "expense/list";
		}
		
		// update expenses
		transService.assignCategoriesToTransactionDetails(model.getBatchUpdate(), toupdate);
		
		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);		
		// return
		return "expense/list";
	}
	
	@RequestMapping(method = RequestMethod.PUT, params = "batchQuickGroup",produces = "text/html")
	public String updateMultiQuickGroups(@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,BindingResult bindingResult,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
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
			return "expense/list";
		}
		
		// update expenses
		transService.assignQuickGroupToTransactionDetails(model.getBatchQuickgroup(), toupdate);
		
		// retrieve and set list
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);		
		// return
		return "expense/list";
	}
	
	@RequestMapping(method = RequestMethod.PUT,params="sort" ,produces = "text/html")
	public String sortExpenses(@RequestParam("sort") String sorttype,@ModelAttribute("expenseListModel") ExpenseListModel model,Model uiModel,HttpServletRequest request) {
		ExpenseCriteria criteria = model.getCriteria();
		if (sorttype!=null) {
			criteria.setSortfield(sorttype);
		}
		HttpSession session = request.getSession();
		session.setAttribute(sessioncriteria,criteria);
		List<ExpenseDao> list = searchService.getExpenses(criteria);
		model.setExpenses(list);

		return "expense/list";
	
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.setAutoGrowCollectionLimit(100024);
	}
	
	private ExpenseCriteria getDefaultCriteria() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setDateRangeByType(DateRangeType.CurrentMonth);
		criteria.setCategorizedType(CategorizedType.All);
		criteria.setTransactionType(TransactionType.Debits);
		criteria.setShowSubcats(false);
		return criteria;
	}

	@ModelAttribute("expenseListModel")
	public ExpenseListModel populateExpenseList(HttpServletRequest request) {
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
	*/
/*	@ModelAttribute("daterangelist")
	protected Enumeration<DateRangeType> referenceDataDateRange(HttpServletRequest request, Object command,
																Errors errors) throws Exception {
		// return model
		return DateRangeType;
	}	


	@ModelAttribute("sourcelist")
	protected List<ColumnValueDao> referenceDataSource(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.ClientKeyLkup	);

		// return model
		return reference;
	}	

	@ModelAttribute("cattypelist")
	protected List<ColumnValueDao> referenceDataCatType(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.CatTypeLkup	);

		// return model
		return reference;
	}	

	@ModelAttribute("transtypelist")
	protected List<ColumnValueDao> referenceDataTransType(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		List<ColumnValueDao> reference = cvManager.getColumnValueList(ExpenseCriteria.TransTypeLkup	);

		// return model
		return reference;
	}	*/
 }
