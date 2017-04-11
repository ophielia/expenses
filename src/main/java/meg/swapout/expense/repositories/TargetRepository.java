package meg.swapout.expense.repositories;


import meg.swapout.expense.domain.Target;
import meg.swapout.expense.services.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetRepository extends
		JpaRepository<Target, Long>,
		JpaSpecificationExecutor<Target> {

	@Query("select tg from Target as tg where tg.targettype=:targettype and  tg.monthtag = :monthtag")
	List<Target> findTargetsByTypeAndMonthTag(
			@Param("targettype") Long targettype,
			@Param("monthtag") String monthtag);

	@Query("select tg from Target as tg where tg.targettype=:targettype and  tg.yeartag = :yeartag")
	List<Target> findTargetsByTypeAndYearTag(
			@Param("targettype") Long targettype,
			@Param("yeartag") String yeartag);

	@Query("select tg from Target as tg where tg.type=:targettype")
	List<Target> findTargetsByType(
			@Param("targettype") TargetType targetType);

	@Query("select tgdao from Target as tgdao where tgdao.isdefault = true and tgdao.targettype = :targettype")
	Target findDefaultGroupByType(@Param("targettype") Long targettype);
}
