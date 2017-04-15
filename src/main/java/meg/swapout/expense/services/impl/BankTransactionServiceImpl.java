package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.BankTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by margaretmartin on 30/03/2017.
 */
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "DefaultFileTemplate"})
@Component
@Transactional
class BankTransactionServiceImpl implements BankTransactionService {



    @Override
    public boolean doesDuplicateExist(RawTransaction trans) {
        return false;
    }

    @Override
    public void addTransaction(RawTransaction trans) {

    }


    @Override
    public void saveTransactionAndDetails(RawTransaction rawTransaction, List<CategorizedTransaction> details) {

    }

}
