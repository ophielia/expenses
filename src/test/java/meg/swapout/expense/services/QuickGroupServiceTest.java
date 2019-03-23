package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;
import meg.swapout.expense.repositories.QuickGroupDetailRepository;
import meg.swapout.expense.repositories.QuickGroupRepository;
import meg.swapout.expense.services.impl.QuickGroupServiceImpl;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuickGroupServiceTest {

    private final TestHelper testHelper = new TestHelper();
    @Autowired
    private QuickGroupService quickGroupService;

    private QuickGroupRepository mockQuickGroupRepository;

    private QuickGroupDetailRepository mockQuickGroupDetailRepository;


    private Category category1;
    private Category category2;
    private Category category3;

    @Before
    public void setUp() {
        // place mockQuickGroupRepository in quickGroupService
        quickGroupService = new QuickGroupServiceImpl();
        mockQuickGroupRepository = EasyMock.createMock(QuickGroupRepository.class);
        mockQuickGroupDetailRepository = EasyMock.createNiceMock(QuickGroupDetailRepository.class);

        // build categoriesCategory category1 = new Category();
        category1 = new Category();
        category2 = new Category();
        category3 = new Category();
        category1.setName("category1");
        ReflectionTestUtils.setField(category1, "id", 1L);
        category1.setName("category2");
        ReflectionTestUtils.setField(category2, "id", 2L);
        category3.setName("category3");
        ReflectionTestUtils.setField(category3, "id", 3L);
        ReflectionTestUtils.setField(quickGroupService, "quickGroupRepository", mockQuickGroupRepository);
        ReflectionTestUtils.setField(quickGroupService, "quickGroupDetailRepository", mockQuickGroupDetailRepository);
    }

    @Test
    public void testSaveQuickGroup_Squash() {
        // build objects
        QuickGroup dbQuickGroup = testHelper.newQuickGroup(11L);
        QuickGroupDetail toSaveDetail = testHelper.buildQuickGroupDetail(11L, dbQuickGroup, category1, 100D);
        List<QuickGroupDetail> details = new ArrayList();
        details.add(toSaveDetail);
        QuickGroupDetail toSaveDetail2 = testHelper.buildQuickGroupDetail(12L, dbQuickGroup, category1, 100D);
        details.add(toSaveDetail2);
        dbQuickGroup.setGroupdetails(details);

        Capture<QuickGroup> quickGroupCapture = new Capture<QuickGroup>();

        QuickGroup savedQuickGroup = testHelper.newQuickGroup(11L);
        QuickGroupDetail savedDetail1 = testHelper.buildQuickGroupDetail(11L, dbQuickGroup, category1, 100D);
        QuickGroupDetail savedDetail2 = testHelper.buildQuickGroupDetail(12L, dbQuickGroup, category1, 100D);
        List<QuickGroupDetail> savedDetails = new ArrayList();
        savedDetails.add(savedDetail1);
        savedDetails.add(savedDetail2);
        savedQuickGroup.setGroupdetails(savedDetails);

        // expect
        expect(mockQuickGroupRepository.findById(11L).get()).andReturn(dbQuickGroup);
        expect(mockQuickGroupRepository.save(EasyMock.capture(quickGroupCapture))).andReturn(savedQuickGroup);

        // replay
        EasyMock.replay(mockQuickGroupRepository, mockQuickGroupDetailRepository);

        // call
        quickGroupService.saveQuickGroup(savedQuickGroup);

        // verify
        verify(mockQuickGroupRepository);
        Assert.assertNotNull(quickGroupCapture);
        Assert.assertTrue(quickGroupCapture.hasCaptured());

        QuickGroup toVerify = quickGroupCapture.getValue();
        Assert.assertEquals(1, toVerify.getGroupdetails().size());
        Assert.assertEquals(new Double(200D), toVerify.getGroupdetails().get(0).getPercentage());
        Assert.assertNotNull(toVerify.getGroupdetails().get(0).getId());
    }

    @Test
    public void testSaveQuickGroup() {
        // build objects
        QuickGroup dbQuickGroup = testHelper.newQuickGroup(11L);


        QuickGroup toSaveQuickGroup = testHelper.newQuickGroup(11L);
        QuickGroupDetail toSaveDetail = testHelper.buildQuickGroupDetail(null, toSaveQuickGroup, category1, 100D);
        List<QuickGroupDetail> details = new ArrayList();
        details.add(toSaveDetail);
        toSaveQuickGroup.setGroupdetails(details);

        QuickGroup savedQuickGroup = testHelper.newQuickGroup(11L);
        QuickGroupDetail savedDetail = testHelper.buildQuickGroupDetail(11L, toSaveQuickGroup, category1, 100D);
        List<QuickGroupDetail> savedDetails = new ArrayList();
        savedDetails.add(savedDetail);
        savedQuickGroup.setGroupdetails(savedDetails);

        // expect
        expect(mockQuickGroupRepository.findById(11L).get()).andReturn(dbQuickGroup);
        expect(mockQuickGroupRepository.save(toSaveQuickGroup)).andReturn(savedQuickGroup);

        // replay
        EasyMock.replay(mockQuickGroupRepository, mockQuickGroupDetailRepository);

        // call
        quickGroupService.saveQuickGroup(toSaveQuickGroup);

        // verify
        verify(mockQuickGroupRepository);
    }


}
