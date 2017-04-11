package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@SuppressWarnings("JpaQlInspection")
public interface RuleRepository extends JpaRepository<Rule, Long> {

    @Query("select rule from Rule as rule  where rule.lineorder>:lineorder")
    List<Rule> findCategoryRulesGreaterThanOrder(@Param("lineorder") long lineorder);

    @Query("select rule from Rule as rule  where rule.lineorder=:lineorder")
    List<Rule> findCategoryRulesByOrder(@Param("lineorder") long lineorder);

    @Query("select rule from Rule as rule  where rule.containing=:text")
    List<Rule> findCategoryRulesByContaining(@Param("text") String text);


}