package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;

import java.util.HashMap;
import java.util.List;


/**
 * Created by margaretmartin on 04/03/2017.
 */
public interface CategoryService {

    String OTHERCATEGORYNAME ="Other";

    Iterable<Category> listAllCategories();

    List<Category> listAllCategories(boolean displayOnly);

    Category getCategoryById(Long id);

    void saveCategory(Category category);

    void deleteCategory(Long id);

    Category getParentCategory(Long id);

    List<Category> getSubcategories(Long id);

    boolean hasCircularReference(Long newParentId, Category category);

    List<CategoryLevel> getAllSubcategories(Category cat);

    List<Category> getDirectSubcategories(Long parentid);

    Category updateCategory(Category category);

    List<CategoryLevel> getCategoriesUpToLevel(int level);

    CategoryLevel getAsCategoryLevel(Long category);

    Long getParentIdForCat(Long id);


    HashMap<Long, Category> getCategoriesAsMap();

    Category getCategoryByName(String othercategoryname);
}
