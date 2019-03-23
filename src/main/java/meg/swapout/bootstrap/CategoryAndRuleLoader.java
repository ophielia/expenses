package meg.swapout.bootstrap;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Rule;
import meg.swapout.expense.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;

//@Component
public class CategoryAndRuleLoader extends AbstractLoader {


    private final Logger log = LoggerFactory.getLogger(CategoryAndRuleLoader.class);

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // save two categories
        Category category = buildCategory("george",1);
        log.info("Saved Category - id: " + category.getId());

        Category anothercategory = buildCategory("name",2);
        log.info("Saved anotherrule - id:" + anothercategory.getId());

        // save these new categories in new rules
        Rule rule = buildRule("matches",category,1);
        log.info("Saved rule - id: " + rule.getId());

        Rule anotherrule = buildRule("bop",anothercategory,2);
        log.info("Saved anotherrule - id:" + anotherrule.getId());

        // category relationships
        buildCategoryRelationship(category,anothercategory);
        buildCategoryRelationship(null,category);


    }


}
