package meg.swapout.expense.repositories;

import java.util.Date;
import java.util.List;

import meg.swapout.expense.domain.RawTransaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@SuppressWarnings("JpaQlInspection")
@Repository
public interface RawTransactionRepository extends JpaRepository<RawTransaction, Long>, JpaSpecificationExecutor<RawTransaction> {

	@Query("select b from RawTransaction b where b.amount < 0 and b.deleted = false")
	List<RawTransaction> findAllUndeleted(Sort sort);



	@Query("select b from RawTransaction b where b.amount < 0 and b.hascat = false order by transdate DESC")
	List<RawTransaction> findNoCategoryExpenses();


	@Query("select b from RawTransaction b where b.hascat is false and b.deleted is false and upper(b.detail) like upper('%:detail%') order by transdate DESC")
	List<RawTransaction> findTransWithDetailLike(@Param("detail") String detail);


    @Query(value = "SELECT min(at.transdate) FROM RawTransaction at")
    public Date getFirstTransDate();

    @Query(value = "SELECT max(at.transdate) FROM RawTransaction at")
    public Date getMostRecentTransDate();

	@Query("select trans from RawTransaction as trans where trans.amount = :amount and trans.transdate = :transdate and trans.description = :description ")
	List<RawTransaction> findTransDuplicates(@Param("amount") Double amount,@Param("transdate") Date transdate,@Param("description") String description);
}
