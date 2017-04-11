package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Rule;
import meg.swapout.expense.repositories.RuleRepository;
import meg.swapout.expense.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        ruleRepository.save(rule);
    }

    @Override
    public void deleteRule(Long id) {
        Rule rule = ruleRepository.findOne(id);
        ruleRepository.delete(rule);
    }
}
