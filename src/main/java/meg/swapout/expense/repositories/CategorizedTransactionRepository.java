package meg.swapout.expense.repositories;
import java.util.List;


import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategorizedTransactionRepository extends JpaRepository<CategorizedTransaction, Long>, JpaSpecificationExecutor<CategorizedTransaction> {
	
			@Query("select b from CategorizedTransaction b where b.banktrans=:transid")
			List<CategorizedTransaction> findByBankTrans(@Param("transid") RawTransaction banktrans);
}
