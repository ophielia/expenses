package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetDetailRepository extends
		JpaRepository<TargetDetail, Long>,
		JpaSpecificationExecutor<TargetDetail> {

	@Query("select tg from TargetDetail as tg where tg.targetgroup=:targetgroup ")
	List<TargetDetail> findByTargetGroup(
			@Param("targetgroup") Target targetgroup);

	@Query("select tg from TargetDetail as tg where tg.targetgroup=:targetgroup and tg.category=:catid")
	List<TargetDetail> findByTargetGroupAndCategory(
			@Param("targetgroup") Target targetgroup,
			@Param("catid") Category category);

}
