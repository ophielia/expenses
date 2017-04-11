package meg.swapout.expense.services;

import meg.swapout.expense.domain.Rule;



/**
 * Created by margaretmartin on 04/03/2017.
 */
public interface RuleService {

    Iterable<Rule> listAllRules();

    Rule getRuleById(Long id);

    void saveRule(Rule category);

    void deleteRule(Long id);
}
