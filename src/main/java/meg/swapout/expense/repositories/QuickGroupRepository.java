package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.QuickGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuickGroupRepository extends JpaRepository<QuickGroup, Long>,
        JpaSpecificationExecutor<QuickGroup> {

}
