package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.CategoryRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRelationshipRepository extends JpaRepository<CategoryRelationship, Long>, JpaSpecificationExecutor<CategoryRelationship> {
	
	
	@Query("select r from CategoryRelationship as r where r.parentId=:parentid and r.childId = :childid")
	CategoryRelationship findByParentAndChild(@Param("parentid") Long parentid,@Param("childid") Long childid);
	
	@Query("select r from CategoryRelationship as r where  r.childId = :childid")
	CategoryRelationship findByChild(@Param("childid") Long childid);
	
}
