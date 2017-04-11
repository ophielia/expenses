package meg.swapout.bootstrap;

import meg.swapout.expense.domain.*;
import meg.swapout.expense.repositories.*;
import meg.swapout.expense.services.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public abstract class AbstractLoader  implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected RuleRepository ruleRepository;

    @Autowired
    protected CategoryRelationshipRepository categoryRelationshipRepository;

    @Autowired
    protected TargetRepository targetRepository;

    @Autowired
    protected QuickGroupRepository quickGroupRepository;

    @Autowired
    protected QuickGroupDetailRepository quickGroupDetailRepository;

    protected Target buildTarget(String name, String tag, TargetType type) {
        Target target = new Target();
        target.setName(name);
        target.setDescription("description");
        target.setIsdefault(true);
        target.setTag(tag);
        target.setTargettype(1L);
        target.setType(type);
        return targetRepository.save(target);

    }


    protected CategoryRelationship buildCategoryRelationship(Category parentCategory, Category childCategory) {
        CategoryRelationship categoryRelationship = new CategoryRelationship();
        categoryRelationship.setChildId(childCategory.getId());
        categoryRelationship.setParentId(parentCategory!=null?parentCategory.getId():0L);
        return categoryRelationshipRepository.save(categoryRelationship);

    }

    protected Category buildCategory(String name, int i) {
        Category category=new Category();
        category.setDescription("description" + i);
        category.setDisplayInList(true);
        category.setName(name);
        category.setNonexpense(false);
        return categoryRepository.save(category);
    }


    protected Rule buildRule(String matches, Category category, int order) {
        Rule rule = new Rule();
        rule.setCatDisplay("nada");
        rule.setCategory(category);
        rule.setContaining(matches);
        rule.setLineorder(Long.valueOf(order));
        return ruleRepository.save(rule);

    }

    protected QuickGroup buildQuickGroup(String name) {
        QuickGroup quickGroup = new QuickGroup();
        quickGroup.setName(name);
        return quickGroupRepository.save(quickGroup);

    }

    protected QuickGroupDetail buildQuickGroupDetail(double percentage, Category category, QuickGroup quickGroup1) {
        QuickGroupDetail quickGroupDetail = new QuickGroupDetail();
        quickGroupDetail.setCategory(category);
        quickGroupDetail.setPercentage(percentage );
        quickGroupDetail.setQuickgroup(quickGroup1);

        return quickGroupDetailRepository.save(quickGroupDetail);
    }


}
