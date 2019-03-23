package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.CategoryRelationship;
import meg.swapout.expense.repositories.CategoryRelationshipRepository;
import meg.swapout.expense.repositories.CategoryRepository;
import meg.swapout.expense.services.CategoryLevel;
import meg.swapout.expense.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by margaretmartin on 04/03/2017.
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryRelationshipRepository catRelRep;

    @Override
    public Iterable<Category> listAllCategories() {
        return listAllCategories(false);
    }

    @Override
    public List<Category> listAllCategories(boolean displayOnly) {
        if (displayOnly) {
            return categoryRepository.findByDisplayinlistTrue(new Sort(new Sort.Order("name")));
        } else {
            return categoryRepository.findAll(new Sort(new Sort.Order("name")));
        }

    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).get();
    }

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).get();
        categoryRepository.delete(category);
    }

    @Override
    public Category getParentCategory(Long id) {
        CategoryRelationship result =
                catRelRep.findByChild(id);

        if (result != null && result.getParentId()>0L) {
           return categoryRepository.findById(result.getParentId()).get();
        }

        return null;
    }

    @Override
    public List<Category> getSubcategories(Long parentid) {
        return categoryRepository.findDirectSubcategories(parentid);
    }

    @Override
    public boolean hasCircularReference(Long newParentId, Category category) {
        // service of same name
        boolean hasCircular = false;
        List<CategoryLevel> allsubcategories = getAllSubcategories(category);
        if (newParentId != null) {
            if (allsubcategories != null) {
                for (CategoryLevel catlvl : allsubcategories) {
                    Category categ = catlvl.getCategory();
                    if (categ.getId().longValue() == newParentId.longValue()) {
                        hasCircular = true;
                    }
                }
            }
        }

        return hasCircular;
    }

    @Override
    public List<CategoryLevel> getAllSubcategories(Category cat) {
        return getSubcategoriesRecursive(cat.getId(), 1, 1, true);
    }

    @Override
    public List<Category> getDirectSubcategories(Long parentid) {
        return categoryRepository.findDirectSubcategories(parentid);
    }

    @Override
    public Category updateCategory(Category category) {
        // update category
        Category updated = categoryRepository.save(category);
        Long childId = updated.getId();

        // get parent category from category
        Category parent = category.getParentCategory();

        // update parent category relationship
        CategoryRelationship categoryRelationship = catRelRep.findByChild(childId);
        if (categoryRelationship==null) {
            // new category - create categoryRelationship
            categoryRelationship=new CategoryRelationship();
            categoryRelationship.setParentId(parent.getId());
            categoryRelationship.setChildId(childId);
        } else {
            // existing category - update categoryRelationship
            categoryRelationship.setParentId(parent.getId());
        }
        catRelRep.save(categoryRelationship);


        return updated;
    }

    @Override
    public List<CategoryLevel> getCategoriesUpToLevel(int level) {
        return getSubcategoriesRecursive(new Long(0), level, 1, false);
    }

    @Override
    public List<CategoryLevel> getSubcategoriesUpToLevel(Long categoryid,int level) {
        return getSubcategoriesRecursive(categoryid, level, 1, false);
    }

    @Override
    public CategoryLevel getAsCategoryLevel(Long categoryid) {
        // service of same name
        // get CategoryDao
        Category catd = categoryRepository.findById(categoryid).get();
        // get CategoryLevel number
        int catlvl = getCategoryLevel(catd);
        // create CategoryLevel Object and return
        CategoryLevel lvl = new CategoryLevel(catd, catlvl);
        return lvl;
    }

    private int getCategoryLevel(Category cat) {
        Long catid = cat.getId();
        int level = 1;

        // retrieve parent ids until toplevel category is found
        Long parentid = getParentIdForCat(catid);
        while (parentid.longValue() > 0) {
            level++;
            parentid = getParentIdForCat(parentid);
        }

        return level;
    }

    @Override
    public Long getParentIdForCat(Long id) {
        CategoryRelationship result = catRelRep.findByChild(id);

        if (result != null) {
            return result.getParentId();

        }
        return null;
    }

    @Override
    public HashMap<Long, Category> getCategoriesAsMap() {
        return null;
    }

    @Override
    public Category getCategoryByName(String othercategoryname) {
        return null;
    }

    @Override
    public Integer getAllSubcategoriesCount(Category category) {
        List<CategoryLevel> allSubCats = getAllSubcategories(category);
        if (allSubCats != null) {
            return allSubCats.size();
        }
        return 0;
    }

    private HashMap<Long, Category> getCategoriesAsMap(boolean exclNonDisp) {
        boolean showNonDisp = !exclNonDisp;
        HashMap<Long, Category> hash = new HashMap<Long, Category>();
        List<Category> categories = listAllCategories(showNonDisp);
        for (Category cat : categories) {
            hash.put(cat.getId(), cat);
        }
        return hash;
    }

    private List<CategoryLevel> getSubcategoriesRecursive(Long parentid,
                                                          int maxlvl, int currentlvl, boolean ignorelvl) {
        // done

        List<CategoryLevel> returncats = new ArrayList<CategoryLevel>();

        // get categories belonging to parentid
        List<CategoryLevel> subcategories = getDirectSubcategoryLvls(parentid,
                currentlvl);
        // if max level = current level then stop condition is met
        // -- return the subcategories for parentid
        if (!ignorelvl && (maxlvl == currentlvl)) {
            return subcategories;
        }
        // otherwise, cycle through all subcategories, retrieving
        // their subcategories.
        if (subcategories != null) {
            for (Iterator<CategoryLevel> iter = subcategories.iterator(); iter
                    .hasNext();) {
                CategoryLevel catlvl = iter.next();
                returncats.add(catlvl);
                // -- increment level
                // -- Add all subcategories to a list
                returncats.addAll(getSubcategoriesRecursive(catlvl
                                .getCategory().getId(), maxlvl, currentlvl + 1,
                        ignorelvl));
            }
        }
        // return collection of subcategories
        return returncats;
    }

    private List<CategoryLevel> getDirectSubcategoryLvls(Long parentid,
                                                         int level) {
        List<Category> direct = getDirectSubcategories(parentid);
        List<CategoryLevel> sublevels = new ArrayList<CategoryLevel>();
        if (direct != null) {
            for (Iterator<Category> iter = direct.iterator(); iter.hasNext();) {
                Category cat = iter.next();
                CategoryLevel catlvl = new CategoryLevel(cat, level);
                sublevels.add(catlvl);
            }

        }
        return sublevels;
    }
    
}
