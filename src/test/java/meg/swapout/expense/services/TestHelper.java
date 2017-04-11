package meg.swapout.expense.services;

import meg.swapout.expense.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

public  class TestHelper {
    public TestHelper() {
    }

    public static QuickGroupDetail buildQuickGroupDetail(Long id, QuickGroup toSaveQuickGroup, Category category1, double percentage) {
        QuickGroupDetail toBuild = new QuickGroupDetail();
        if (id != null) {
            ReflectionTestUtils.setField(toBuild, "id", id);
        }
        toBuild.setQuickgroup(toSaveQuickGroup);
        toBuild.setCategory(category1);
        toBuild.setPercentage(percentage);
        return toBuild;
    }

    public static QuickGroup newQuickGroup(Long id) {
        QuickGroup toBuild = new QuickGroup();
        if (id != null) {
            ReflectionTestUtils.setField(toBuild, "id", id);
        }

        return toBuild;

    }

    public static TargetDetail buildTargetDetail(Long id, Target toSaveTarget, Category category1, double percentage) {
        TargetDetail toBuild = new TargetDetail();
        if (id != null) {
            ReflectionTestUtils.setField(toBuild, "id", id);
        }
        toBuild.setTargetgroup(toSaveTarget);
        toBuild.setCategory(category1);
        toBuild.setAmount(percentage);
        return toBuild;
    }

    public static Target newTarget(Long id) {
        Target toBuild = new Target();
        if (id != null) {
            ReflectionTestUtils.setField(toBuild, "id", id);
        }

        return toBuild;

    }

    public RawTransaction buildRawTransaction(Long id,String detail, String description, double amount) {
        RawTransaction toBuild = new RawTransaction();
        if (id != null) {
            ReflectionTestUtils.setField(toBuild, "id", id);
        }
        toBuild.setDescription(description);
        toBuild.setDetail(detail);
        toBuild.setAmount(amount);

        return toBuild;
    }
}