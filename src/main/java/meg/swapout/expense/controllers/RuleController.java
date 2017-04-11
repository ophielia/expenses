package meg.swapout.expense.controllers;


import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Rule;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/rule")
@Controller
public class RuleController {
	
	 @Autowired
	 RuleService ruleService;

	@Autowired
	CategoryService categoryService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model){
		model.addAttribute("rules", ruleService.listAllRules());
		return "rules";
	}

	@RequestMapping("/{id}")
	public String showRule(@PathVariable Long id, Model model){
		model.addAttribute("rule", ruleService.getRuleById(id));
		return "ruleshow";
	}

	@RequestMapping("/new")
	public String newRule(Model model){
		model.addAttribute("rule", new Rule());
		return "ruleform";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveRule(Rule rule){
		ruleService.saveRule(rule);
		return "redirect:/rule";
	}

	@RequestMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model){
		model.addAttribute("rule", ruleService.getRuleById(id));
		return "ruleform";
	}

	@RequestMapping("/delete/{id}")
	public String delete(@PathVariable Long id){
		ruleService.deleteRule(id);
		return "redirect:/rule";
	}

	@ModelAttribute("categorylist")
	protected List<Category> referenceCategoryData(HttpServletRequest request, Object command,
												   Errors errors) throws Exception {
		List<Category> list = categoryService.listAllCategories(true);


		// return model
		return list;
	}


}
