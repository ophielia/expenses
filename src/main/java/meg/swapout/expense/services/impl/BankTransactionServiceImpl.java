package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.*;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.BankTransactionService;
import meg.swapout.expense.services.QuickGroupService;
import meg.swapout.expense.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 30/03/2017.
 */
@Component
@Transactional
public class BankTransactionServiceImpl implements BankTransactionService {

@Autowired
private RawTransactionRepository transactionRepository;

@Autowired
private CategorizedTransactionRepository catTransRep;

    @Autowired
    private SearchService searchService;

    @Autowired
    private QuickGroupService quickGroupService;

    @Override
    public boolean doesDuplicateExist(RawTransaction trans) {
        return false;
    }

    @Override
    public void addTransaction(RawTransaction trans) {

    }

    @Override
@Transactional
    public void assignCategoriesToExpenses(Category category, List<String> selectedids) {
        if (category==null || category.getId()==null) {
            return;
        }
        Long catid = category.getId();
        if (selectedids!=null && selectedids.size()>0) {
            List<ExpenseDao> toupdate = searchService.getExpenseListByIds(selectedids);
            // loop through selected list
            for (ExpenseDao expense:toupdate) {
                if (expense.getHascat().booleanValue()) {
                    // if expense has a category already, existing category must be
                    // updated
                    updateCategoryExp(expense.getCattransid(), category);
                } else {
                    // otherwise, assign category to BankTA
                    assignCategory(expense.getTransid(), category);
                }
            }
        }
    }

    @Override
    public void assignQuickGroupToExpenses(Long quickgroupid, List<String> selectedids) {
        if (selectedids!=null && selectedids.size()>0) {
            List<ExpenseDao> toupdate = searchService.getExpenseListByIds(selectedids);

            // get quickgroup
            QuickGroup quickgroup = quickGroupService.getQuickGroupById(quickgroupid);
            // loop through selected list
            for (ExpenseDao expense:toupdate) {
                // get bankta
                RawTransaction banktrans = transactionRepository.findOne(expense.getTransid());

                // get db expense details
                List<CategorizedTransaction> expdetails = getCategoryExpForTrans(banktrans.getId());
                if (expdetails!=null&&expdetails.size()>0) {
                    // delete db expense details
                    catTransRep.delete(expdetails);
                }

                // update bankta (hascat true)
                banktrans.setHascat(true);
                banktrans = transactionRepository.save(banktrans);
                // get amount for bankta
                double amount = banktrans.getAmount().doubleValue();
                // get new expensedetails for quickgroup
                List<CategorizedTransaction> newdetails = quickGroupService.getExpDetailsForQuickGroup(amount, quickgroupid);
                // set bankta in new expensedetails, and save
                if (newdetails!=null) {
                    for (CategorizedTransaction exp:newdetails) {
                        exp.setBanktrans(banktrans);
                    }
                    catTransRep.save(newdetails);
                }

            }
        }

    }

@Transactional
    private void updateCategoryExp(Long catexpid, Category category) {
        // retrieve CategoryExpense
        CategorizedTransaction catexp = catTransRep.findOne(catexpid);
        // update with new catid
        catexp.setCategory(category);
        // persist change
        catexp =catTransRep.saveAndFlush(catexp);

    }

    @Override
    public List<CategorizedTransaction> getCategoryExpForTrans(Long transid) {
        RawTransaction trans =  transactionRepository.findOne(transid);
        return catTransRep.findByBankTrans(trans);
    }
    
    @Override
    public void assignCategory(Long transid, Category category) {
        // lookup BankTransaction
        
        RawTransaction bankta = transactionRepository.findOne(transid);

        // begin creating CategorizedTransaction based upon BankTransaction
        CategorizedTransaction catta = new CategorizedTransaction();
        catta.setAmount(bankta.getAmount());
        catta.setCategory(category);
        catta.setCreatedon(new Date());
        catta.setBanktrans(bankta);

        // add CategorizedTransaction to DB
        catTransRep.saveAndFlush(catta);

        // update Transaction
        bankta.setHascat(new Boolean(true));
        transactionRepository.saveAndFlush(bankta);
    }


    @Override
    public void saveTransactionAndDetails(RawTransaction rawTransaction, List<CategorizedTransaction> details) {

    }

}
