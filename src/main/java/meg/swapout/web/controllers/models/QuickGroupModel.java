package meg.swapout.web.controllers.models;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;

import java.util.List;

/**
 * Created by margaretmartin on 14/03/2017.
 */
public class QuickGroupModel {

    private QuickGroup quickGroup;

    private List<QuickGroupDetail> details;

    private Double percentage;

    private Category category;
    private int inEdit;
    private int toEdit;

    public QuickGroupModel() {
    }

    public QuickGroupModel(QuickGroup quickGroup) {
        this.quickGroup = quickGroup;
        this.details = quickGroup.getGroupdetails();
        this.inEdit = -1;
    }

    public QuickGroup getQuickGroup() {
        return quickGroup;
    }

    public void setQuickGroup(QuickGroup quickGroup) {
        this.quickGroup = quickGroup;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInEdit(int inEdit) {
        this.inEdit = inEdit;
    }

    public int getInEdit() {
        return inEdit;
    }

    public List<QuickGroupDetail> getDetails() {
        return details;
    }

    public void setDetails(List<QuickGroupDetail> details) {
        this.details = details;
    }

    public int getToEdit() {
        return toEdit;
    }

    public void setToEdit(int toEdit) {
        this.toEdit = toEdit;
    }
}
