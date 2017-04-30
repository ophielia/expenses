package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.*;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.SearchService;
import meg.swapout.expense.services.TransactionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
@Service
public class TransactionDetailServiceImpl implements TransactionDetailService {

    @Autowired
    private CategorizedTransactionRepository catTransRep;

    @Autowired
    private
    CategorizedTransactionRepository categorizedTransactionRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private RawTransactionRepository transactionRepository;

    @Autowired
    RawTransactionRepository rawTransactionRepo;


    @Override
    public List<CategorizedTransaction> listAllCategorizedTransactions() {
        return categorizedTransactionRepository.findAll();
    }

    @Override
    public CategorizedTransaction getCategorizedTransactionById(Long id) {
        return categorizedTransactionRepository.findOne(id);
    }

    @Override
    public List<CategorizedTransaction> saveDetailsAndTransaction(RawTransaction transaction, List<CategorizedTransaction> details) {
        if (transaction != null) {

            // get details from db
            List<CategorizedTransaction> dbdetails = categorizedTransactionRepository.findByBankTrans(transaction);

            // squash details from db
            HashMap<Long, CategorizedTransaction> dbMap = squashDetails(dbdetails);


            // squash details from passed quick group
            HashMap<Long, CategorizedTransaction> newMap = squashDetails(details);

            // go through new details
            List<CategorizedTransaction> toDelete = new ArrayList<>();
            List<CategorizedTransaction> toSave = new ArrayList<>();
            for (Long catId : newMap.keySet()) {
                CategorizedTransaction newDetail = newMap.get(catId);
                if (dbMap.containsKey(catId)) {
                    CategorizedTransaction dbDetail = dbMap.get(catId);
                    dbDetail.setAmount(newDetail.getAmount());
                    toSave.add(dbDetail);
                    dbMap.remove(catId);
                } else {
                    toSave.add(newDetail);
                }

            }
            // delete details to be deleted
            if (dbdetails != null) {
                for (CategorizedTransaction detail : dbdetails) {
                    if (toSave.contains(detail)) continue;
                    toDelete.add(detail);
                }
            }
            if (toDelete.size() > 0) {
                categorizedTransactionRepository.delete(toDelete);
            }


            // save quick group and object
            return saveCategorizedTransactionList(transaction, toSave);
        }


        return null;
    }

    private List<CategorizedTransaction> saveCategorizedTransactionList(RawTransaction transaction, List<CategorizedTransaction> details) {
        for (CategorizedTransaction detail : details) {
            detail.setBanktrans(transaction);
        }
        List<CategorizedTransaction> toReturn = categorizedTransactionRepository.save(details);

        transaction.setHascat(true);
        rawTransactionRepo.save(transaction);

        return toReturn;
    }

    private HashMap<Long, CategorizedTransaction> squashDetails(List<CategorizedTransaction> details) {
        HashMap<Long, CategorizedTransaction> map = new HashMap<>();
        if (details != null) {
            for (CategorizedTransaction detail : details) {
                Long catId = detail.getCategory().getId();
                if (map.containsKey(catId)) {
                    CategorizedTransaction mapDetail = map.get(catId);
                    // perform swap if necessary
                    CategorizedTransaction toSaveInHash = detail.getId() != null ? detail : mapDetail;
                    Double percentage = mapDetail.getAmount() != null ? mapDetail.getAmount() : 0D;
                    percentage += detail.getAmount() != null ? detail.getAmount() : 0D;
                    toSaveInHash.setAmount(percentage);
                    map.put(catId, toSaveInHash);
                } else {
                    map.put(catId, detail);
                }
            }
        }
        return map;
    }


    @Override
    @Transactional
    public void assignCategoriesToTransactionDetails(Category category, List<String> selectedids) {
        if (category == null || category.getId() == null) {
            return;
        }
        if (selectedids != null && selectedids.size() > 0) {
            List<ExpenseDao> toupdate = searchService.getExpenseListByIds(selectedids);
            // loop through selected list
            for (ExpenseDao expense : toupdate) {
                if (expense.getHascat()) {
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
    @Transactional
    public void roundTransactions(List<String> selectedids) {
        if (selectedids != null && selectedids.size() > 0) {
            List<ExpenseDao> toupdate = searchService.getExpenseListByIds(selectedids);
            // loop through selected list
            for (ExpenseDao expense : toupdate) {
                Double amount = expense.getTranstotal() * -1;

                int roundCheck = (int) (Math.round(amount * 100) * 100);
                if ((amount * 100) == roundCheck) {
                    continue;
                }
                // round the sucker here
                double seed = Math.ceil(amount) * 100D;
                double roundAmount = seed - (amount * 100D);

                // make new cat transaction
                CategorizedTransaction categorizedTransaction = new CategorizedTransaction();
                Category category = categoryService.getCategoryById(expense.getCatid());
                RawTransaction rawtrans = transactionRepository.findOne(expense.getTransid());
                categorizedTransaction.setCategory(category);
                categorizedTransaction.setBanktrans(rawtrans);
                categorizedTransaction.setCreatedon(new Date());
                categorizedTransaction.setAmount(roundAmount / -100D);

                // update transaction
                rawtrans.setRounded(true);

                // save changes
                transactionRepository.save(rawtrans);
                categorizedTransactionRepository.save(categorizedTransaction);

            }
        }
    }


    private void assignCategory(Long transid, Category category) {
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
        bankta.setHascat(Boolean.TRUE);
        transactionRepository.saveAndFlush(bankta);
    }

    @Transactional
    private void updateCategoryExp(Long catexpid, Category category) {
        // retrieve CategoryExpense
        CategorizedTransaction catexp = catTransRep.findOne(catexpid);
        // update with new catid
        catexp.setCategory(category);
        // persist change
        catTransRep.saveAndFlush(catexp);

    }


    @Override
    public void assignQuickGroupToTransactionDetails(QuickGroup quickgroup, List<String> selectedids) {
        if (selectedids != null && selectedids.size() > 0) {
            List<ExpenseDao> toupdate = searchService.getExpenseListByIds(selectedids);


            // loop through selected list
            for (ExpenseDao expense : toupdate) {
                // get bank transaction
                RawTransaction banktrans = transactionRepository.findOne(expense.getTransid());

                // delete existing expense details
                List<CategorizedTransaction> expdetails = getCategoryExpForTrans(banktrans.getId());
                if (expdetails != null && expdetails.size() > 0) {
                    // delete db expense details
                    catTransRep.delete(expdetails);
                }

                // update bankta (hascat true)
                banktrans.setHascat(true);
                banktrans = transactionRepository.save(banktrans);

                // get amount for bankta
                double amount = banktrans.getAmount().doubleValue();

                // get new expensedetails for quickgroup
                List<CategorizedTransaction> newdetails = quickGroupToDetails(amount, quickgroup);

                // set bankta in new expensedetails, and save
                if (newdetails != null) {
                    for (CategorizedTransaction exp : newdetails) {
                        exp.setBanktrans(banktrans);
                    }
                    catTransRep.save(newdetails);
                }

            }
        }

    }

    @Override
    public List<CategorizedTransaction> getDetailsForTransaction(RawTransaction transaction) {
        if (transaction == null) {
            return null;
        }
        return categorizedTransactionRepository.findByBankTrans(transaction);
    }

    @Override
    public List<CategorizedTransaction> distributeAmount(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly) {
        // determine amount to be distributed
        Double amountToDistribute = getRemainder(transaction, details, remainderOnly);
        if (amountToDistribute == null) {
            return null;
        }

        // find count of details in which to distribute
        int distributeCount = remainderOnly ? getDistributeCount(details) : details.size();

        // calculate distribute amount
        Double amountPerDetail = Math.round(amountToDistribute * 100.0 / (double) distributeCount) / 100.00;

        // get last amount
        Double lastAmount = amountPerDetail;
        if ((distributeCount * amountPerDetail) != amountToDistribute) {
            lastAmount = lastAmount + (amountToDistribute - (distributeCount * amountPerDetail));
        }

        // set amounts in details
        List<CategorizedTransaction> results = new ArrayList<>();
        int count = 0;
        for (CategorizedTransaction detail : details) {
            if (remainderOnly && detail.getAmount() != null && detail.getAmount() != 0.0) {
                results.add(detail);
                continue;
            }
            count++;
            if (count == distributeCount) {
                detail.setAmount(lastAmount);
            } else {
                detail.setAmount(amountPerDetail);
            }
            results.add(detail);

        }
        // return details list
        return details;
    }

    @Override
    public List<CategorizedTransaction> addQuickGroupToDetails(RawTransaction transaction, List<CategorizedTransaction> details, QuickGroup quickGroup, boolean remainderOnly) {
        // get amount to divy up with quick group
        Double toDistribute = getRemainder(transaction, details, remainderOnly);

        // get return elements
        List<CategorizedTransaction> returnDetails = remainderOnly ? details : new ArrayList<>();
        if (returnDetails == null) {
            returnDetails = new ArrayList<>();
        }

        // add quickgroup elements to return elements
        List<CategorizedTransaction> quickGroupDetails = quickGroupToDetails(toDistribute, quickGroup);
        // return return elements
        returnDetails.addAll(quickGroupDetails);
        return returnDetails;
    }


    @Override
    public void assignCategoryToTransaction(Long transid, Category cat) {
        // lookup BankTransaction
        RawTransaction transaction = transactionRepository.findOne(transid);

        // begin creating CategorizedTransaction based upon BankTransaction
        CategorizedTransaction detail = new CategorizedTransaction();
        detail.setAmount(transaction.getAmount());
        detail.setCategory(cat);
        detail.setCreatedon(new Date());
        detail.setBanktrans(transaction);

        // add CategorizedTransaction to DB
        catTransRep.saveAndFlush(detail);

        // update Transaction
        transaction.setHascat(new Boolean(true));
        transactionRepository.saveAndFlush(transaction);
    }


    private List<CategorizedTransaction> quickGroupToDetails(Double toDistribute, QuickGroup quickGroup) {
        List<CategorizedTransaction> details = new ArrayList<>();

        Double totalCheck = 0D;
        List<QuickGroupDetail> quickGroupDetails = quickGroup.getGroupdetails();
        if (quickGroupDetails == null || quickGroupDetails.size() == 0) {
            return details;
        }

        for (QuickGroupDetail quickGroupDetail : quickGroupDetails) {
            CategorizedTransaction newDetails = new CategorizedTransaction();
            Double amount = Math.round(quickGroupDetail.getPercentage() * toDistribute) / 100D;
            totalCheck += amount;
            newDetails.setCategory(quickGroupDetail.getCategory());
            newDetails.setAmount(amount);
            details.add(newDetails);
        }

        if (totalCheck.doubleValue() != toDistribute.doubleValue()) {
            CategorizedTransaction detail = details.get(details.size() - 1);
            Double adjustment = toDistribute - totalCheck;
            detail.setAmount(detail.getAmount() + adjustment);
        }

        return details;
    }

    private int getDistributeCount(List<CategorizedTransaction> details) {
        if (details == null) {
            return 1;
        }
        int count = 0;
        for (CategorizedTransaction detail : details) {
            if (detail.getAmount() == null || detail.getAmount() == 0D) {
                count++;
            }
        }
        return count;
    }

    private Double getRemainder(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly) {
        if (transaction == null || details == null) {
            return null;
        }

        Double transamount = transaction.getAmount();
        if (!remainderOnly) {
            return transamount;
        }
        Double sumofparts = 0D;
        for (CategorizedTransaction detail : details) {
            sumofparts += detail.getAmount() != null ? detail.getAmount() : 0D;
        }

        return transamount - sumofparts;
    }

    private List<CategorizedTransaction> getCategoryExpForTrans(Long transid) {
        RawTransaction trans = transactionRepository.findOne(transid);
        return catTransRep.findByBankTrans(trans);
    }

}
