package meg.swapout.bootstrap;

import meg.swapout.expense.domain.*;
import meg.swapout.expense.repositories.CategoryRepository;
import meg.swapout.expense.repositories.QuickGroupRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

//@Component
public class QuickGroupLoader extends AbstractLoader {

    private final Logger log = Logger.getLogger(QuickGroupLoader.class);

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    QuickGroupRepository quickGroupRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // save two categories
        Category category = buildCategory("qg_cat1",1);
        log.info("Saved Category - id: " + category.getId());

        Category anothercategory = buildCategory("qg_cat2",2);
        log.info("Saved anotherrule - id:" + anothercategory.getId());

        // save these new categories in new quick groups
        QuickGroup quickGroup1 = buildQuickGroup("quickGroup");
        log.info("Saved quickGroup - id: " + quickGroup1.getId());

        // save quick group details
        List<QuickGroupDetail> details = new ArrayList<>();
        QuickGroupDetail quikcGroupDetail = buildQuickGroupDetail(100D,category, quickGroup1);
        details.add(quikcGroupDetail);

        QuickGroupDetail quikcGroupDetail2 = buildQuickGroupDetail(100D,category, quickGroup1);
        details.add(quikcGroupDetail2);

        quickGroup1.setGroupdetails(details);
        quickGroupRepository.save(quickGroup1);

        // category relationships
        CategoryRelationship categoryRelationship = buildCategoryRelationship(category,anothercategory);
        CategoryRelationship categoryRelationship2 = buildCategoryRelationship(null,category);

    }



}
