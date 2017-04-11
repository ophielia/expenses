package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.repositories.TargetDetailRepository;
import meg.swapout.expense.repositories.TargetRepository;
import meg.swapout.expense.services.impl.TargetServiceImpl;
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
public class TargetServiceTest {

    private final TestHelper testHelper = new TestHelper();
    @Autowired
    private TargetService targetService;

    private TargetRepository mockTargetRepository;
    private TargetDetailRepository mockTargetDetailRepository;


    private Category category1;
    private Category category2;
    private Category category3;

    @Before
    public void setUp() {
        // place mockTargetRepository in targetService
        targetService = new TargetServiceImpl();
        mockTargetRepository = EasyMock.createMock(TargetRepository.class);
        mockTargetDetailRepository = EasyMock.createNiceMock(TargetDetailRepository.class);

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
        ReflectionTestUtils.setField(targetService, "targetRepository", mockTargetRepository);
        ReflectionTestUtils.setField(targetService, "targetDetailRepository", mockTargetDetailRepository);
    }

    @Test
    public void testSaveTarget_Squash() {
        // build objects
        Target dbTarget = TestHelper.newTarget(11L);
        TargetDetail toSaveDetail = TestHelper.buildTargetDetail(11L, dbTarget, category1, 100D);
        List<TargetDetail> details = new ArrayList();
        details.add(toSaveDetail);
        TargetDetail toSaveDetail2 = TestHelper.buildTargetDetail(12L, dbTarget, category1, 100D);
        details.add(toSaveDetail2);
        dbTarget.setTargetdetails(details);

        Capture<Target> targetCapture = new Capture<Target>();

        Target savedTarget = TestHelper.newTarget(11L);
        TargetDetail savedDetail1 = TestHelper.buildTargetDetail(11L, dbTarget, category1, 100D);
        TargetDetail savedDetail2 = TestHelper.buildTargetDetail(12L, dbTarget, category1, 100D);
        List<TargetDetail> savedDetails = new ArrayList();
        savedDetails.add(savedDetail1);
        savedDetails.add(savedDetail2);
        savedTarget.setTargetdetails(savedDetails);

        // expect
        expect(mockTargetRepository.findOne(11L)).andReturn(dbTarget);
        expect(mockTargetRepository.save(EasyMock.capture(targetCapture))).andReturn(savedTarget);

        // replay
        EasyMock.replay(mockTargetRepository,mockTargetDetailRepository);

        // call
        targetService.saveTarget(savedTarget);

        // verify
        verify(mockTargetRepository);
        Assert.assertNotNull(targetCapture);
        Assert.assertTrue(targetCapture.hasCaptured());

        Target toVerify = targetCapture.getValue();
        Assert.assertEquals(1, toVerify.getTargetdetails().size());
        Assert.assertEquals(new Double(200D), toVerify.getTargetdetails().get(0).getAmount());
        Assert.assertNotNull(toVerify.getTargetdetails().get(0).getId());
    }

    @Test
    public void testSaveTarget() {
        // build objects
        Target dbTarget = TestHelper.newTarget(11L);


        Target toSaveTarget = TestHelper.newTarget(11L);
        TargetDetail toSaveDetail = TestHelper.buildTargetDetail(null, toSaveTarget, category1, 100D);
        List<TargetDetail> details = new ArrayList();
        details.add(toSaveDetail);
        toSaveTarget.setTargetdetails(details);

        Target savedTarget = TestHelper.newTarget(11L);
        TargetDetail savedDetail = TestHelper.buildTargetDetail(11L, toSaveTarget, category1, 100D);
        List<TargetDetail> savedDetails = new ArrayList();
        savedDetails.add(savedDetail);
        savedTarget.setTargetdetails(savedDetails);

        // expect
        expect(mockTargetRepository.findOne(11L)).andReturn(dbTarget);
        expect(mockTargetRepository.save(toSaveTarget)).andReturn(savedTarget);

        // replay
        EasyMock.replay(mockTargetRepository,mockTargetDetailRepository);

        // call
        targetService.saveTarget(toSaveTarget);

        // verify
        verify(mockTargetRepository);
    }


}
