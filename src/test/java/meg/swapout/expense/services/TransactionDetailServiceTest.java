package meg.swapout.expense.services;

import meg.swapout.expense.domain.*;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.impl.TransactionDetailServiceImpl;
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
public class TransactionDetailServiceTest {

    private final TestHelper testHelper = new TestHelper();
    @Autowired
    private TransactionDetailService transactionDetailService;

    private RawTransactionRepository mockTransactionRepository;

    private CategorizedTransactionRepository categorizedTransactionRepository;


    private Category category1;
    private Category category2;
    private Category category3;
    private Category category4;

    @Before
    public void setUp() {
        // place mockTransactionRepository in transactionDetailService
        transactionDetailService = new TransactionDetailServiceImpl();
        mockTransactionRepository = EasyMock.createMock(RawTransactionRepository.class);
        //categorizedTransactionRepository = EasyMock.createNiceMock(QuickGroupDetailRepository.class);

        // build categoriesCategory category1 = new Category();
        category1 = new Category();
        category2 = new Category();
        category3 = new Category();
        category4 = new Category();
        category1.setName("category1");
        ReflectionTestUtils.setField(category1, "id", 1L);
        category1.setName("category2");
        ReflectionTestUtils.setField(category2, "id", 2L);
        category3.setName("category3");
        ReflectionTestUtils.setField(category3, "id", 3L);
        category4.setName("category4");
        ReflectionTestUtils.setField(category4, "id", 4L);
        ReflectionTestUtils.setField(transactionDetailService, "rawTransactionRepo", mockTransactionRepository);
        //ReflectionTestUtils.setField(transactionDetailService, "quickGroupDetailRepository", categorizedTransactionRepository);
    }

    
    
    @Test
    public void testSaveQuickGroup_Squash() {
      /*  // build objects
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
        expect(mockTransactionRepository.findOne(11L)).andReturn(dbQuickGroup);
        expect(mockTransactionRepository.save(EasyMock.capture(quickGroupCapture))).andReturn(savedQuickGroup);

        // replay
        EasyMock.replay(mockTransactionRepository, categorizedTransactionRepository);

        // call
        transactionDetailService.saveQuickGroup(savedQuickGroup);

        // verify
        verify(mockTransactionRepository);
        Assert.assertNotNull(quickGroupCapture);
        Assert.assertTrue(quickGroupCapture.hasCaptured());

        QuickGroup toVerify = quickGroupCapture.getValue();
        Assert.assertEquals(1, toVerify.getGroupdetails().size());
        Assert.assertEquals(new Double(200D), toVerify.getGroupdetails().get(0).getPercentage());
        Assert.assertNotNull(toVerify.getGroupdetails().get(0).getId());
    */
    }

    @Test
    public void testSaveQuickGroup() {
        // build objects
  /*      QuickGroup dbQuickGroup = testHelper.newQuickGroup(11L);


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
        expect(mockTransactionRepository.findOne(11L)).andReturn(dbQuickGroup);
        expect(mockTransactionRepository.save(toSaveQuickGroup)).andReturn(savedQuickGroup);

        // replay
        EasyMock.replay(mockTransactionRepository, categorizedTransactionRepository);

        // call
        transactionDetailService.saveQuickGroup(toSaveQuickGroup);

        // verify
        verify(mockTransactionRepository);
    */
    }

    @Test
    public void testDistributeAmount_All() {
        // make transaction with amount of 100
        RawTransaction trans = testHelper.buildRawTransaction(1L, "detail","description", 100D);
        
        // make 4 details - amounts 100 each
        List<CategorizedTransaction> details = new ArrayList<>();
        CategorizedTransaction categorizedTransaction = new CategorizedTransaction();
        categorizedTransaction.setCategory(category1);
        categorizedTransaction.setAmount(100D);
        details.add(categorizedTransaction);
        
        CategorizedTransaction categorizedTransaction1 = new CategorizedTransaction();
        categorizedTransaction1.setCategory(category2);
        categorizedTransaction1.setAmount(100D);
        details.add(categorizedTransaction1);

        CategorizedTransaction categorizedTransaction2 = new CategorizedTransaction();
        categorizedTransaction2.setCategory(category3);
        categorizedTransaction2.setAmount(100D);
        details.add(categorizedTransaction2);

        CategorizedTransaction categorizedTransaction3 = new CategorizedTransaction();
        categorizedTransaction3.setCategory(category3);
        categorizedTransaction3.setAmount(100D);
        details.add(categorizedTransaction3);

        // call method
        List<CategorizedTransaction> result = transactionDetailService.distributeAmount(trans,details,false);
        
        // afterwards, should have each detail with 25
        Assert.assertNotNull(result);
        Assert.assertEquals(4,result.size());
        for (CategorizedTransaction detail: details) {
            Assert.assertEquals(new Double(25),detail.getAmount());
        }
        
    }

    @Test
    public void testDistributeAmount_RemainderOnly() {
        // make transaction with amount of 100
        RawTransaction trans = testHelper.buildRawTransaction(1L, "detail","description", 100D);

        // make 4 details - amounts 100 each
        List<CategorizedTransaction> details = new ArrayList<>();
        CategorizedTransaction categorizedTransaction = new CategorizedTransaction();
        categorizedTransaction.setCategory(category1);
        categorizedTransaction.setAmount(50D);
        details.add(categorizedTransaction);

        CategorizedTransaction categorizedTransaction1 = new CategorizedTransaction();
        categorizedTransaction1.setCategory(category2);
        categorizedTransaction1.setAmount(0D);
        details.add(categorizedTransaction1);

        CategorizedTransaction categorizedTransaction2 = new CategorizedTransaction();
        categorizedTransaction2.setCategory(category3);
        categorizedTransaction2.setAmount(0D);
        details.add(categorizedTransaction2);

        // call method
        List<CategorizedTransaction> result = transactionDetailService.distributeAmount(trans,details,true);

        // afterwards, should have each detail with 25
        Assert.assertNotNull(result);
        Assert.assertEquals(3,result.size());
        int count25 = 0;
        int count50 = 0;
        for (CategorizedTransaction detail: details) {
            if (detail.getAmount()==50D) {
                count50++;
            } else if (detail.getAmount()==25) {
                count25++;
            }
        }
        Assert.assertEquals(1,count50);
        Assert.assertEquals(2,count25);

    }

    @Test
    public void testAddQuickGroups() {
        // make transaction with amount of 120
        RawTransaction trans = testHelper.buildRawTransaction(1L, "detail","description", 120D);

        // make 4 details - amounts 100 each
        List<CategorizedTransaction> details = new ArrayList<>();
        CategorizedTransaction categorizedTransaction = new CategorizedTransaction();
        categorizedTransaction.setCategory(category1);
        categorizedTransaction.setAmount(20D);
        details.add(categorizedTransaction);

        // make quick group details
        QuickGroup quickGroup = TestHelper.newQuickGroup(11L);
        QuickGroupDetail detail1 = TestHelper.buildQuickGroupDetail(1L,quickGroup,  category2,75D);
        QuickGroupDetail detail2 = TestHelper.buildQuickGroupDetail(1L,quickGroup,  category2,25D);
        List<QuickGroupDetail> qgDetails = new ArrayList<>();
        qgDetails.add(detail1);
        qgDetails.add(detail2);
        quickGroup.setGroupdetails(qgDetails);

        // call method
        List<CategorizedTransaction> result = transactionDetailService.addQuickGroupToDetails(trans,details,quickGroup,true);

        // afterwards, should have each detail with 25
        Assert.assertNotNull(result);
        Assert.assertEquals(3,result.size());
        int count20 = 0;
        int count25 = 0;
        int count75 = 0;
        for (CategorizedTransaction detail: details) {
            if (detail.getAmount()==20D) {
                count20++;
            } else if (detail.getAmount()==25) {
                count25++;
            } else if (detail.getAmount()==75) {
                count75++;
            }
        }
        Assert.assertEquals(1,count20);
        Assert.assertEquals(1,count25);
        Assert.assertEquals(1,count75);

    }

    @Test
    public void testReplaceQuickGroups() {
        // make transaction with amount of 120
        RawTransaction trans = testHelper.buildRawTransaction(1L, "detail","description", 120D);

        // make 4 details - amounts 100 ea™ch
        List<CategorizedTransaction> details = new ArrayList<>();
        CategorizedTransaction categorizedTransaction = new CategorizedTransaction();
        categorizedTransaction.setCategory(category1);
        categorizedTransaction.setAmount(20D);
        details.add(categorizedTransaction);

        // make quick group details
        QuickGroup quickGroup = TestHelper.newQuickGroup(11L);
        QuickGroupDetail detail1 = TestHelper.buildQuickGroupDetail(1L,quickGroup,  category2,75D);
        QuickGroupDetail detail2 = TestHelper.buildQuickGroupDetail(1L,quickGroup,  category2,25D);
        List<QuickGroupDetail> qgDetails = new ArrayList<>();
        qgDetails.add(detail1);
        qgDetails.add(detail2);
        quickGroup.setGroupdetails(qgDetails);

        // call method
        List<CategorizedTransaction> result = transactionDetailService.addQuickGroupToDetails(trans,details,quickGroup,false);

        // afterwards, should have each detail with 25
        Assert.assertNotNull(result);
        Assert.assertEquals(2,result.size());
        int count30 = 0;
        int count90 = 0;
        for (CategorizedTransaction detail: result) {
            if (detail.getAmount()==30D) {
                count30++;
            } else if (detail.getAmount()==90) {
                count90++;
            }
        }
        Assert.assertEquals(1,count30);
        Assert.assertEquals(1,count90);

    }

}
