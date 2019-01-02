package meg.swapout.web.controllers;

import meg.swapout.web.controllers.models.TargetModel;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.TargetService;
import meg.swapout.expense.services.TargetType;
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
@RequestMapping("/target")
@Controller
public class TargetController {

    @Autowired
    TargetService targetService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping(value = "/monthly", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("targets", targetService.listAllTargets(TargetType.Monthly));
        return "monthlytargets";
    }

    @RequestMapping(value = "/yearly", method = RequestMethod.GET)
    public String listYearly(Model model) {
        model.addAttribute("targets", targetService.listAllTargets(TargetType.Yearly));
        return "yearlytargets";
    }

    @RequestMapping("/{id}")
    public String showTarget(@PathVariable Long id, Model model) {
        model.addAttribute("target", targetService.getTargetById(id));
        return "targetsshow";
    }

    @RequestMapping("/delete/{id}")
    public String deleteTarget(@PathVariable Long id, Model model) {
        // create model
        targetService.deleteTarget(id);

        return list(model);
    }

    @RequestMapping("/edit/{id}")
    public String beginEditTarget(@PathVariable Long id, Model model) {
        // create model
        Target target = targetService.getTargetById(id);
        TargetModel targetModel = new TargetModel(target);

        model.addAttribute("targetModel", targetModel);
        setAmountTotalInModel(targetModel, model);
        return "targeteditform";
    }



    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"addDetail"})
    public String addTargetDetail(@PathVariable Long id, TargetModel targetModel, Model model) {

        List<TargetDetail> details = targetModel.getDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(new TargetDetail());
        targetModel.setDetails(details);

        targetModel = prepareForEdit(details.size() - 1, targetModel);

        model.addAttribute("targetModel", targetModel);
        setAmountTotalInModel(targetModel, model);

        return "targeteditform";
    }

    private TargetModel prepareForEdit(int inEdit, TargetModel targetModel) {
        // set inEdit index
        targetModel.setInEdit(inEdit);

        // return Target
        return targetModel;
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String saveTarget(@PathVariable Long id, TargetModel targetModel, Model model) {
        Target input = targetModel.getTarget();
        Target target = targetService.getTargetById(id);
        copyInputIntoTarget(input, target);
        List<TargetDetail> details = targetModel.getDetails();
        target.setTargetdetails(details);

        targetService.saveTarget(target);
        targetModel.setTarget(target);
        return list(model);
    }

    private void copyInputIntoTarget(Target input, Target target) {
        target.setDescription(input.getDescription());
        target.setIsdefault(input.getIsdefault());
        target.setName(input.getName());
        target.setDescription(input.getDescription());
        target.setTag(input.getTag());

    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"editLine"})
    public String editLine(@PathVariable Long id, TargetModel qcModel, Model model) {
        qcModel = setUpEdit(qcModel);
        model.addAttribute("qcModel", qcModel);
        setAmountTotalInModel(qcModel, model);
        return "targeteditform";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"clearLine"})
    public String clearLine(@PathVariable Long id, TargetModel qcModel, Model model) {

        int toEdit = qcModel.getToEdit();
        qcModel.setInEdit(toEdit);

        // get detail and clear category and percentage
        List<TargetDetail> details = qcModel.getDetails();
        TargetDetail toclear = details.get(toEdit);
        toclear.setCategory(null);
        toclear.setAmount(0.0D);
        details.set(toEdit, toclear);
        qcModel.setDetails(details);

        model.addAttribute("qcModel", qcModel);
        setAmountTotalInModel(qcModel, model);
        return "targeteditform";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST, params = {"removeLine"})
    public String removeLine(@PathVariable Long id, TargetModel qcModel, Model model) {
        int toEdit = qcModel.getToEdit();

        // get detail and clear category and percentage
        List<TargetDetail> details = qcModel.getDetails();
        TargetDetail toclear = details.remove(toEdit);
        qcModel.setDetails(details);

        // calculate new get edit
        if (qcModel.getInEdit() > toEdit) {
            qcModel.setInEdit(qcModel.getInEdit() - 1);
        } else if (qcModel.getInEdit() == toEdit) {
            qcModel.setInEdit(-1);
        }
        model.addAttribute("qcModel", qcModel);
        setAmountTotalInModel(qcModel, model);
        return "targeteditform";
    }

    private TargetModel setUpEdit(TargetModel qcModel) {
        int toEdit = qcModel.getToEdit();
        qcModel.setInEdit(toEdit);
        return qcModel;
    }

    private void setAmountTotalInModel(TargetModel targetModel, Model model) {
        if (targetModel != null && targetModel.getDetails() != null) {
            double total = 0;
            for (TargetDetail detail : targetModel.getDetails()) {
                total += detail.getAmount()!=null?detail.getAmount():0;
            }
            model.addAttribute("totalAmount", new Double(total));
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
