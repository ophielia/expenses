package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Rule;
import meg.swapout.expense.repositories.RuleRepository;
import meg.swapout.expense.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by margaretmartin on 04/03/2017.
 */
@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    RuleRepository ruleRepository;

    @Override
    public Iterable<Rule> listAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public Rule getRuleById(Long id) {
        return ruleRepository.findOne(id);
    }

    @Override
    public void saveRule(Rule rule) {
        if (rule == null) {
            return;
        }
        if (rule.getLineorder() == null) {
            // get maximum line order for rule
            List<Rule> maxRules = ruleRepository.findAllByOrderByLineorderDesc();
            Long newlineorder = 1L;
            if (maxRules != null && !maxRules.isEmpty()) {
                Rule maxRule = maxRules.get(0);
                newlineorder = maxRule.getLineorder() + 1;
            }
            rule.setLineorder(newlineorder);
        }

        ruleRepository.save(rule);
    }


    @Override
    public void deleteRule(Long id) {
        Rule rule = ruleRepository.findOne(id);
        ruleRepository.delete(rule);
    }
}
