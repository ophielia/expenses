package meg.swapout.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;


import meg.swapout.web.controllers.models.AssignmentListModel;
import meg.swapout.web.controllers.validators.ExpenseListModelValidator;
import meg.swapout.expense.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RequestMapping("/ruleassignment")
@Controller
public class AssignByRulesController {

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


    @ModelAttribute("assignmentListModel")
    public AssignmentListModel populateAssignmentListModel(HttpServletRequest request) {
        // service call which assigns uncategorized expenses by rules
        List<RuleAssignment> assignmentlist=transService.getRuleAssignments();
        return new AssignmentListModel(assignmentlist);
    }


    @RequestMapping(produces = "text/html")
    public String showList(@ModelAttribute("assignmentListModel") AssignmentListModel model,Model uiModel,HttpServletRequest request) {
        return "assignrules";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String makeAssignments(@ModelAttribute("assignmentListModel") AssignmentListModel model,Model uiModel,HttpServletRequest request) {
        List<RuleAssignment> toupdate = model.getCheckedRuleAssignments();
        // update by rule assignment
        transService.updateExpenseByRuleAssignments(toupdate);
        return "redirect:expense/list";
    }

}
