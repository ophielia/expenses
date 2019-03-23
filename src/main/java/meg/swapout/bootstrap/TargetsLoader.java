package meg.swapout.bootstrap;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.repositories.TargetRepository;
import meg.swapout.expense.services.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

//@Component
public class TargetsLoader extends AbstractLoader {


    private Logger log = LoggerFactory.getLogger(TargetsLoader.class);

    @Autowired
    public void setTargetRepository(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // save categories to use with targets
        Category category = buildCategory("target category", 1);
        Category category2 = buildCategory("target category", 2);


        // save two monthly targets with details
        Target mtarget1 = buildTarget("name", "01-2017", TargetType.Monthly);

        // make 2 details
        List<TargetDetail> targetDetails = new ArrayList<TargetDetail>();
        TargetDetail mdetail1 = buildTargetDetail(mtarget1, category, 100D);
        targetDetails.add(mdetail1);
        TargetDetail mdetail2 = buildTargetDetail(mtarget1, category2, 200D);
        targetDetails.add(mdetail2);

        mtarget1.setTargetdetails(targetDetails);
        targetRepository.save(mtarget1);


        Target mtarget2 = buildTarget("second", "02-2017", TargetType.Monthly);

        // make 2 details
        List<TargetDetail> targetDetails2 = new ArrayList<TargetDetail>();
        TargetDetail mdetail21 = buildTargetDetail(mtarget1, category, 100D);
        targetDetails2.add(mdetail21);
        TargetDetail mdetail22 = buildTargetDetail(mtarget1, category2, 200D);
        targetDetails2.add(mdetail22);

        mtarget2.setTargetdetails(targetDetails2);
        targetRepository.save(mtarget2);

      /*  mdetail1.setAmount();
        mdetail1.setCatdisplay();
        mdetail1.setTargetgroup();

*/
    }

    private TargetDetail buildTargetDetail(Target mtarget1, Category category, double amount) {
        new ArrayList<TargetDetail>();
        TargetDetail mdetail1 = new TargetDetail();
        mdetail1.setCategory(category);
        mdetail1.setAmount(amount);
        mdetail1.setTargetgroup(mtarget1);
        return mdetail1;
    }

}
