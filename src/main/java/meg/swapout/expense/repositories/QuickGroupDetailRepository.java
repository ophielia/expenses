package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuickGroupDetailRepository extends
        JpaRepository<QuickGroupDetail, Long>,
        JpaSpecificationExecutor<QuickGroupDetail> {

    @Query("select b from QuickGroupDetail b where b.quickgroup=:quickgroup")
    List<QuickGroupDetail> findByQuickGroup(
            @Param("quickgroup") QuickGroup quickgroup);

}
