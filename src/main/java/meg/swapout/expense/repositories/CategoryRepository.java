package meg.swapout.expense.repositories;


import meg.swapout.expense.domain.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>  {
	
	Category findByName(String name);
	
	List<Category> findByDisplayinlistTrue(Sort sort);
	
	@Query("select r from Category as r where r.id in  ( select rel.childId from CategoryRelationship as rel where rel.parentId = :parentid)")
	List<Category> findDirectSubcategories(@Param("parentid") Long parentid);
}
