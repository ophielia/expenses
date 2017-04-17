package meg.swapout.web.controllers.models;

import meg.swapout.expense.domain.*;

import java.util.List;

/**
 * Created by margaretmartin on 14/03/2017.
 */
public class TargetModel {

    private Target target;

    private List<TargetDetail> details;

    private Category category;
    private int inEdit;
    private int toEdit;

    public TargetModel() {
    }

    public TargetModel(Target target) {
        this.target = target;
        this.details = target.getTargetdetails();
        this.inEdit = -1;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<TargetDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TargetDetail> details) {
        this.details = details;
    }

    public int getToEdit() {
        return toEdit;
    }

    public void setToEdit(int toEdit) {
        this.toEdit = toEdit;
    }

    public int getInEdit() {
        return inEdit;
    }

    public void setInEdit(int inEdit) {
        this.inEdit = inEdit;
    }
}
