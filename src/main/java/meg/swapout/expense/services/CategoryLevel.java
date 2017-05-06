package meg.swapout.expense.services;


import meg.swapout.expense.domain.Category;

public class CategoryLevel {
    private Category category;
    private int level;

    public CategoryLevel(Category cat, int level) {
        this.category = cat;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Category getCategory() {
        return category;
    }


}
