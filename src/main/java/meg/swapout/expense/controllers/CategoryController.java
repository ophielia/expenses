package meg.swapout.expense.controllers;


import meg.swapout.expense.controllers.validators.CategoryValidator;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/category")
@Controller
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryValidator categoryValidator;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("categories", categoryService.listAllCategories());
        return "category";
    }

    @RequestMapping(value="/grid",method = RequestMethod.GET)
    public String grid(Model model) {
        model.addAttribute("categories", categoryService.getCategoriesUpToLevel(999));
        return "categorygrid";
    }

    @RequestMapping("/{id}")
    public String showCategory(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "categoryshow";
    }

    @RequestMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.listAllCategories(true));
        return "categorynewform";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String updateCategory(@PathVariable Long id, Category category, Model uiModel, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        // validation checks
        categoryValidator.validate(category, bindingResult);


        if (bindingResult.hasErrors()) {
            return "categoryeditform";
        }
        uiModel.asMap().clear();

        // save category
        categoryService.updateCategory(category);

        return "redirect:/category";

    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String saveCategory(Category category) {
        categoryService.updateCategory(category);
        return "redirect:/category";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        // get category
        Category category = categoryService.getCategoryById(id);
        // get parent id
        Category parentCategory = categoryService.getParentCategory(id);
        // get subcategory list
        List<Category> subcategories = categoryService.getSubcategories(id);
        // put all into model
        model.addAttribute("category", category);
        model.addAttribute("parentCategoryName", parentCategory != null ? parentCategory.getName() : "Top Level Category");
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("categories", categoryService.listAllCategories(false));

        return "categoryeditform";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/category";
    }

}
