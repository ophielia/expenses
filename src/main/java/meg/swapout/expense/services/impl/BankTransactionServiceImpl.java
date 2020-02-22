package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.domain.Rule;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.repositories.RuleRepository;
import meg.swapout.expense.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by margaretmartin on 30/03/2017.
 */
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "DefaultFileTemplate"})
@Component
@Transactional
class BankTransactionServiceImpl implements BankTransactionService {

    @Autowired
    RawTransactionRepository rawTransactionRepository;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    private TransactionDetailService transactionDetailService;


    @Override
    public boolean doesDuplicateExist(RawTransaction trans) {
        boolean exists = false;
        String detail = trans.getDetail() != null ? trans.getDetail().trim().toLowerCase() : "";
        if (trans != null) {
            List<RawTransaction> result = rawTransactionRepository.findTransDuplicates(
                    trans.getAmount(), trans.getTransdate(),
                    detail);

            if (!result.isEmpty()) {
                // this transaction seems to already exist
                exists = true;
            }
        }
        return exists;
    }

    @Override
    public RawTransaction addTransaction(RawTransaction trans) {
        // set deleted to false
        trans.setDeleted(Boolean.valueOf(false));
        // set rounded to false
        trans.setRounded(false);
        // call Dao interface here
        rawTransactionRepository.save(trans);
        return trans;
    }

    @Override
    public RawTransaction getById(Long id) {
        if (id == null) {
            return null;
        }
        return rawTransactionRepository.findById(id).get();
    }

    @Override
    public List<RuleAssignment> getRuleAssignments() {
        // Assigns Categories to Uncategorized expenses according to the text in the Expense Detail.
        // does work within program rather than through repeated calls on database

        // load rules in order
        List<Rule> rules = ruleRepository.findAll(new Sort(Sort.Direction.ASC, "lineorder"));
        // load all uncategorized expenses
        List<RawTransaction> expenses = rawTransactionRepository.findNoCategoryExpenses();

        // prepare holders
        HashMap<Long, RuleAssignment> assigned = new HashMap<>();
        List<Long> assignedexpenses = new ArrayList<>();

        // loop through all rules, assigning categories
        for (Rule rule : rules) {
            String searchfor = rule.getContaining().toLowerCase();
            Category cat = rule.getCategory();
            if (cat == null) {
                continue;

            }

            assignedexpenses = categorizeExpensesForRule(expenses,assigned, assignedexpenses, searchfor,rule);
        }

        // return list of ruleassignment objects
        List<RuleAssignment> results = new ArrayList<>();
        for (Map.Entry<Long, RuleAssignment> entry : assigned.entrySet()) {
            RuleAssignment check = entry.getValue();
            if (check.getTransactionCount() > 0) {
                results.add(check);
            }
        }

        return results;

    }

    private List<Long> categorizeExpensesForRule(List<RawTransaction> expenses, HashMap<Long, RuleAssignment> assigned, List<Long> assignedexpenses, String searchString, Rule rule) {
        Long rulecatid = rule.getCategory().getId();

        for (RawTransaction exp : expenses) {
            if (assignedexpenses.contains(exp.getId())) {
                continue;
            }
            // check in detail
            String searchin = exp.getDetail().toLowerCase();
            if (searchin.indexOf(searchString) >= 0) {
                // string found
                // retrieve or create RuleAssignment
                RuleAssignment ruleassign;
                if (assigned.containsKey(rulecatid)) {
                    ruleassign = assigned.get(rulecatid);
                } else {
                    ruleassign = new RuleAssignment(rule.getCategory());
                }
                // add transaction to RuleAssignment
                ruleassign.addTransaction(new RuleTransaction(exp));
                // reset RuleAssignment in holder
                assigned.put(rulecatid, ruleassign);
                // add expenseid to assignedidlist
                assignedexpenses.add(exp.getId());
            }
        }
        return assignedexpenses;

    }

    @Override
    public void updateExpenseByRuleAssignments(List<RuleAssignment> assignedcategories) {
        if (assignedcategories != null) {
            // loop through categories
            for (RuleAssignment rule : assignedcategories) {
                // for each category group, pull transactions to be assigned
                List<RuleTransaction> transactions = rule.getTransactions();
                Category category = rule.getCategory();
                if (transactions != null) {
                    // loop through transactions
                    for (RuleTransaction ruleTransaction : transactions) {
                        RawTransaction trans = ruleTransaction.getTransaction();
                        if (trans == null) {
                            continue;
                        }
                        // assign each transaction to the group
                        transactionDetailService.assignCategoryToTransaction(trans.getId(), category);
                    }
                }
            }
        }

    }



}
