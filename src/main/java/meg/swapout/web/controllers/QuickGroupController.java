package meg.swapout.web.controllers;

import meg.swapout.web.controllers.models.QuickGroupModel;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.QuickGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
@RequestMapping("/quickgroup")
@Controller
public class QuickGroupController {

    @Autowired
    QuickGroupService quickGroupService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("quickgroups", quickGroupService.listAllQuickGroups());
        return "quickgroups";
    }

    @RequestMapping("/{id}")
    public String showQuickGroup(@PathVariable Long id, Model model) {
        model.addAttribute("quickgroup", quickGroupService.getQuickGroupById(id));
        return "quickgroupshow";
    }


    @RequestMapping("/edit/{id}")
    public String showEditQuickGroup(@PathVariable Long id, Model model) {
        // create model
        QuickGroup quickGroup = quickGroupService.getQuickGroupById(id);
        QuickGroupModel qcModel = new QuickGroupModel(quickGroup);

        model.addAttribute("qcModel", qcModel);
        setPercentageTotalInModel(qcModel,model);
        return "quickgroupform";
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"addDetail"})
    public String addQuickGroupDetail(@PathVariable Long id, QuickGroupModel qgModel, Model model) {

        List<QuickGroupDetail> details = qgModel.getDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(new QuickGroupDetail());
        qgModel.setDetails(details);

        qgModel = prepareForEdit(details.size() - 1, qgModel);

        model.addAttribute("qcModel", qgModel);
        setPercentageTotalInModel(qgModel,model);
        return "quickgroupform";
    }

    private QuickGroupModel prepareForEdit(int inEdit, QuickGroupModel qgModel) {
        // set inEdit index
        qgModel.setInEdit(inEdit);

        // pull detail, and set in percentage and category
        List<QuickGroupDetail> details = qgModel.getDetails();
        QuickGroupDetail detail = details.get(inEdit);
        qgModel.setPercentage(detail.getPercentage() != null ? detail.getPercentage() : 0);
        qgModel.setCategory(detail.getCategory());

        // return QuickGroup
        return qgModel;
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String saveQuickGroup(@PathVariable Long id, QuickGroupModel qcModel, Model model) {
        QuickGroup quickGroup = qcModel.getQuickGroup();
        List<QuickGroupDetail> details = qcModel.getDetails();
        quickGroup.setGroupdetails(details);

        quickGroupService.saveQuickGroup(quickGroup);
        return list(model);
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"editLine"})
    public String editLine(@PathVariable Long id, QuickGroupModel qcModel, Model model) {
        qcModel = setUpEdit(qcModel);
        model.addAttribute("qcModel", qcModel);
        setPercentageTotalInModel(qcModel,model);
        return "quickgroupform";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"clearLine"})
    public String clearLine(@PathVariable Long id, QuickGroupModel qcModel, Model model) {

        int toEdit = qcModel.getToEdit();
        qcModel.setInEdit(toEdit);

        // get detail and clear category and percentage
        List<QuickGroupDetail> details = qcModel.getDetails();
        QuickGroupDetail toclear = details.get(toEdit);
        toclear.setCategory(null);
        toclear.setPercentage(0.0D);
        details.set(toEdit, toclear);
        qcModel.setDetails(details);

        model.addAttribute("qcModel", qcModel);
        setPercentageTotalInModel(qcModel,model);
        return "quickgroupform";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"removeLine"})
    public String removeLine(@PathVariable Long id, QuickGroupModel qcModel, Model model) {
        int toEdit = qcModel.getToEdit();

        // get detail and clear category and percentage
        List<QuickGroupDetail> details = qcModel.getDetails();
        QuickGroupDetail toclear = details.remove(toEdit);
        qcModel.setDetails(details);

        // calculate new get edit
        if (qcModel.getInEdit() > toEdit) {
            qcModel.setInEdit(qcModel.getInEdit() - 1);
        } else if (qcModel.getInEdit() == toEdit) {
            qcModel.setInEdit(-1);
        }
        model.addAttribute("qcModel", qcModel);
        setPercentageTotalInModel(qcModel,model);
        return "quickgroupform";
    }

    private QuickGroupModel setUpEdit(QuickGroupModel qcModel) {
        int toEdit = qcModel.getToEdit();
        qcModel.setInEdit(toEdit);
        return qcModel;
    }

    private void setPercentageTotalInModel(QuickGroupModel quickGroupModel, Model model) {
        if (quickGroupModel != null && quickGroupModel.getDetails() != null) {
            double total = 0;
            for (QuickGroupDetail detail : quickGroupModel.getDetails()) {
                total += detail.getPercentage()!=null?detail.getPercentage():0;
            }
            model.addAttribute("totalPercentage", new Double(total));
        }
    }

    @ModelAttribute("categorylist")
    protected List<Category> referenceCategoryData(HttpServletRequest request, Object command,
                                                   Errors errors) throws Exception {
        List<Category> list = categoryService.listAllCategories(true);


        // return model
        return list;
    }

}
