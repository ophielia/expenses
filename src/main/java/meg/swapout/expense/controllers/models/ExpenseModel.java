package meg.swapout.expense.controllers.models;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

/**
 * Created by margaretmartin on 14/03/2017.
 */
public class ExpenseModel {

    private RawTransaction transaction;

    private List<CategorizedTransaction> details;

    private Category category;

    private QuickGroup quickGroup;

    private boolean saveAsQuickGroup = false;

    private int inEdit;
    private int toEdit;

    public ExpenseModel() {
    }

    public ExpenseModel(RawTransaction transaction) {
        this.transaction = transaction;
        this.inEdit = -1;
    }

    public RawTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(RawTransaction transaction) {
        this.transaction = transaction;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<CategorizedTransaction> getDetails() {
        return details;
    }

    public void setDetails(List<CategorizedTransaction> details) {
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

    public QuickGroup getQuickGroup() {
        return quickGroup;
    }

    public void setQuickGroup(QuickGroup quickGroup) {
        this.quickGroup = quickGroup;
    }

    public boolean isSaveAsQuickGroup() {
        return saveAsQuickGroup;
    }

    public void setSaveAsQuickGroup(boolean saveAsQuickGroup) {
        this.saveAsQuickGroup = saveAsQuickGroup;
    }
}
