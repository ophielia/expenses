package meg.swapout.expense.repositories;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;
import meg.swapout.expense.services.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by margaretmartin on 25/03/2017.
 */


    @RunWith(SpringRunner.class)
    @DataJpaTest
    public class QuickGroupRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private QuickGroupRepository quickGroupRepository;

        private Category category1;
        private Category category2;

        private QuickGroup savedQuickGroup;
        @Test
        public void testSimpleTest() {
            QuickGroup quickGroup = new QuickGroup();
            quickGroup.setName("george");
            entityManager.persist(quickGroup);
            Long searchId = quickGroup.getId();

            QuickGroup quickGroup1 = quickGroupRepository.findById(searchId).get();
            Assert.assertNotNull(quickGroup1);
            assertEquals(new Long(searchId), quickGroup.getId());
        }

        @Before
        public void setUp() {
            category1 = new Category();
            category2 = new Category();
            category1.setName("category1");
            category2.setName("category2");
            entityManager.persistAndFlush(category1);
            entityManager.persistAndFlush(category2);

            savedQuickGroup = new QuickGroup();
            savedQuickGroup.setName("saved");
            QuickGroupDetail detail1 = TestHelper.buildQuickGroupDetail(null,savedQuickGroup,category1,50D);
            QuickGroupDetail detail2 = TestHelper.buildQuickGroupDetail(null,savedQuickGroup,category1,50D);
            List<QuickGroupDetail> details = new ArrayList();
            details.add(detail1);
            details.add(detail2);
            savedQuickGroup.setGroupdetails(details);
            entityManager.persist(savedQuickGroup);
        }

    @Test
    public void testSimpleSave() {
        QuickGroup quickGroup = new QuickGroup();
        quickGroup.setName("george");

        QuickGroup quickGroup1 = quickGroupRepository.save(quickGroup);
        Assert.assertNotNull(quickGroup1);
        Assert.assertNotNull(quickGroup1.getId());
    }


    @Test
    public void testSimpleSaveWithDetails() {
        QuickGroup quickGroup = new QuickGroup();
        quickGroup.setName("george");
        QuickGroupDetail detail1 = TestHelper.buildQuickGroupDetail(null,quickGroup,category1,50D);

        List<QuickGroupDetail> details = new ArrayList<>();
        details.add(detail1);
        quickGroup.setGroupdetails(details);

        QuickGroup quickGroup1 = quickGroupRepository.save(quickGroup);
        Assert.assertNotNull(quickGroup1);
        Assert.assertNotNull(quickGroup1.getId());
        QuickGroupDetail detail = quickGroup1.getGroupdetails().get(0);
        Assert.assertNotNull(detail);
        Assert.assertNotNull(detail.getId());
    }

    @Test
    public void testUpdateWithDetails() {
        // get id
        Long savedId = savedQuickGroup.getId();
        // get group
        QuickGroup quickGroup = quickGroupRepository.findById(savedId).get();
        List<QuickGroupDetail> details = quickGroup.getGroupdetails();

        // remove one detail
        details.remove(1);

        // change another detail
        details.get(0).setPercentage(100D);

        // save
        QuickGroup quickGroup1 = quickGroupRepository.save(quickGroup);
        Assert.assertNotNull(quickGroup1);
        Assert.assertNotNull(quickGroup1.getId());
        QuickGroupDetail detail = quickGroup1.getGroupdetails().get(0);
        Assert.assertNotNull(detail);
        Assert.assertNotNull(detail.getId());
    }

}
