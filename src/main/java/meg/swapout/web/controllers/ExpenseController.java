package meg.swapout.web.controllers;

import meg.swapout.web.controllers.models.ExpenseModel;
import meg.swapout.web.controllers.validators.ExpenseModelValidator;
import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.BankTransactionService;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.QuickGroupService;
import meg.swapout.expense.services.TransactionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
@RequestMapping("/expense/edit/")
@Controller
public class ExpenseController {

    @Autowired
    private
    BankTransactionService rawTransactionService;

    @Autowired
    private
    CategorizedTransactionRepository categorizedTransactionRepo;

    @Autowired
    private
    TransactionDetailService transactionDetailService;

    @Autowired
    private
    CategoryService categoryService;

    @Autowired
    private
    QuickGroupService quickGroupService;

    @Autowired
    private
    RawTransactionRepository transactionRepository;

    @Autowired
    private ExpenseModelValidator modelValidator;

    @RequestMapping("/{id}")
    public String beginEditExpense(@PathVariable Long id, Model model) {
        // create model
        ExpenseModel expenseModel= fillExpenseModel(id,null);

        // set amount flag (rounding)
        RawTransaction transaction = expenseModel.getTransaction();
        if (transaction.getRounded()!=null && transaction.getRounded()) {
            expenseModel.setAmountFlag(true);
        }

        // add details
        List<CategorizedTransaction> details = categorizedTransactionRepo.findByBankTrans(expenseModel.getTransaction());
        expenseModel.setDetails(details);
        
        setAmountTotalInModel(expenseModel, model);
        expenseModel.setInEdit(-1);
        model.addAttribute("expenseModel", expenseModel);
        return "expenseedit";
    }

    private ExpenseModel fillExpenseModel(Long id, ExpenseModel expenseModel) {
        // get transaction
        RawTransaction rawTransaction = transactionRepository.findById(id).get();

        // get model - or create, if empty
        if (expenseModel == null) {
            expenseModel = new ExpenseModel(rawTransaction);
        } else {
            expenseModel.setTransaction(rawTransaction);
        }
        return expenseModel;
    }


    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"addDetail"})
    public String addCategorizedTransaction(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel= fillExpenseModel(id,expenseModel);
        
        List<CategorizedTransaction> details = expenseModel.getDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(new CategorizedTransaction());
        expenseModel.setDetails(details);

        expenseModel = prepareForEdit(details.size() - 1, expenseModel);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);

        return "expenseedit";
    }

    private ExpenseModel prepareForEdit(int inEdit, ExpenseModel ExpenseModel) {
        // set inEdit index
        ExpenseModel.setInEdit(inEdit);

        // return RawTransaction
        return ExpenseModel;
    }



    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public String saveTransactionAndDetails(@PathVariable Long id, ExpenseModel expenseModel, Model model, Errors bindingResult) {
        expenseModel = fillExpenseModel(id,expenseModel);

        modelValidator.validateSaveDetails(expenseModel, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("expenseModel",model);

            // return
            return "expenses";
        }

        // call service
        RawTransaction transaction = expenseModel.getTransaction();
        transaction.setRounded(expenseModel.getAmountFlag());
        transactionDetailService.saveDetailsAndTransaction(expenseModel.getTransaction(),expenseModel.getDetails());

        // check if quick group should be saved
        if (expenseModel.isSaveAsQuickGroup()) {
            QuickGroup quickGroup = quickGroupService.createQuickGroupFromTransactionDetails(id);
            return "redirect:/quickgroup/edit/" + quickGroup.getId();
        }

        return "redirect:/expense/list";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"editLine"})
    public String editLine(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);
        setUpEdit(expenseModel);
        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"clearLine"})
    public String clearLine(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        int toEdit = expenseModel.getToEdit();
        expenseModel.setInEdit(toEdit);

        // get detail and clear category and percentage
        List<CategorizedTransaction> details = expenseModel.getDetails();
        CategorizedTransaction toclear = details.get(toEdit);
        toclear.setAmount(0.0D);
        details.set(toEdit, toclear);
        expenseModel.setDetails(details);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"clearAllLines"})
    public String clearAllLines(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        // get detail and clear category and percentage
        List<CategorizedTransaction> details = expenseModel.getDetails();
        if (details!=null) {
            for (CategorizedTransaction detail:details) {
                detail.setAmount(0D);
            }
        }
        expenseModel.setDetails(details);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"distributeAmount"})
    public String distributeAmount(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        // get detail and clear category and percentage
        RawTransaction transaction = expenseModel.getTransaction();
        transaction.setRounded(expenseModel.getAmountFlag());
        List<CategorizedTransaction> details = expenseModel.getDetails();
        List<CategorizedTransaction> detailresult = transactionDetailService.distributeAmount(transaction,details,false);

        expenseModel.setDetails(details);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"distributeAmountRemainder"})
    public String distributeAmountRemainder(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        // get detail and clear category and percentage
        RawTransaction transaction = expenseModel.getTransaction();
        transaction.setRounded(expenseModel.getAmountFlag());
        List<CategorizedTransaction> details = expenseModel.getDetails();
        List<CategorizedTransaction> detailresult = transactionDetailService.distributeAmount(transaction,details,true);

        expenseModel.setDetails(details);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"addQuickGroup"})
    public String addQuickGroup(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        // get detail and clear category and percentage
        RawTransaction transaction = expenseModel.getTransaction();
        List<CategorizedTransaction> details = expenseModel.getDetails();
        QuickGroup quickGroup = expenseModel.getQuickGroup();
        List<CategorizedTransaction> detailresult = transactionDetailService.addQuickGroupToDetails(transaction,details,quickGroup,true);

        expenseModel.setDetails(detailresult);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"replaceWithQuickGroup"})
    public String replaceWithQuickGroup(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);

        // get detail and clear category and percentage
        RawTransaction transaction = expenseModel.getTransaction();
        List<CategorizedTransaction> details = expenseModel.getDetails();
        QuickGroup quickGroup = expenseModel.getQuickGroup();
        List<CategorizedTransaction> detailresult = transactionDetailService.addQuickGroupToDetails(transaction,details,quickGroup,false);

        expenseModel.setDetails(detailresult);

        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, params = {"removeLine"})
    public String removeLine(@PathVariable Long id, ExpenseModel expenseModel, Model model) {
        expenseModel = fillExpenseModel(id,expenseModel);
        int toEdit = expenseModel.getToEdit();

        // get detail and clear category and percentage
        List<CategorizedTransaction> details = expenseModel.getDetails();
        CategorizedTransaction toclear = details.remove(toEdit);
        expenseModel.setDetails(details);

        // calculate new get edit
        if (expenseModel.getInEdit() > toEdit) {
            expenseModel.setInEdit(expenseModel.getInEdit() - 1);
        } else if (expenseModel.getInEdit() == toEdit) {
            expenseModel.setInEdit(-1);
        }
        model.addAttribute("expenseModel", expenseModel);
        setAmountTotalInModel(expenseModel, model);
        return "expenseedit";
    }

    private void setUpEdit(ExpenseModel expenseModel) {
        int toEdit = expenseModel.getToEdit();
        expenseModel.setInEdit(toEdit);
        return;
    }

    private void setAmountTotalInModel(ExpenseModel ExpenseModel, Model model) {
        if (ExpenseModel != null && ExpenseModel.getDetails() != null) {
            double total = 0;
            for (CategorizedTransaction detail : ExpenseModel.getDetails()) {
                total += detail.getAmount()!=null?detail.getDisplayAmount():0;
            }
            model.addAttribute("totalAmount", new Double(total));
        }
    }

    @ModelAttribute("categorylist")
    protected List<Category> referenceCategoryData(HttpServletRequest request, Object command,
                                                   Errors errors) throws Exception {


        // return model
        return categoryService.listAllCategories(true);
    }

    @ModelAttribute("quickgrouplist")
    protected List<QuickGroup> referenceQuickGroupData(HttpServletRequest request, Object command,
                                                       Errors errors) throws Exception {

        // return model
        return quickGroupService.listAllQuickGroups();
    }


}
