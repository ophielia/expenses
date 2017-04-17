package meg.swapout.web.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;


import meg.swapout.common.DateUtils;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.services.CategoryService;
import meg.swapout.expense.services.DateRangeType;
import meg.swapout.expense.services.SearchService;
import meg.swapout.reporting.ReportCriteria;
import meg.swapout.reporting.ReportDataFactory;
import meg.swapout.reporting.elements.ReportData;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/reports")
@Controller
public class ReportController {

	@Autowired
	ReportDataFactory reportFactory;


	@Autowired
	CategoryService categoryService;

	@Autowired
	SearchService searchService;
	
	private FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
	
	private TransformerFactory tFactory = TransformerFactory.newInstance();
	
	@RequestMapping(produces = "text/html")
	public String showAllReports(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request) {

		return "reports/reportlist";
	}

	@RequestMapping(params = "reporttype", produces = "text/html")
	public String showInput(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			@RequestParam("reporttype") Long reporttype, Model uiModel,
			HttpServletRequest request) {
		if (reporttype != null) {
			reportCriteria.setReportType(reporttype);
		}
		String view = "reports/monthlytargetinput";
		if (reporttype.longValue() == ReportDataFactory.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetinput";
		} else if (reporttype.longValue() == ReportDataFactory.ReportType.YearlyTargetStatus) {
			view = "reports/yearlytargetinput";
		} else if (reporttype.longValue() == ReportDataFactory.ReportType.FullMonth) {
			view = "reports/fullmonthinput";
		} else if (reporttype.longValue() == ReportDataFactory.ReportType.Yearly) {
			view = "reports/yearlyinput";
		}
		return view;
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String showHtmlReport(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request) {
		Long reporttype = reportCriteria.getReportType();
		ReportData results = processReport(reportCriteria,request);

		// set results in model
		uiModel.addAttribute("results", results.resultsAsHashMap());

		// determine view
		String view = "reports/" + results.getJspViewname();
		
		if (reporttype.longValue() == ReportDataFactory.ReportType.MonthlyTarget) {
			view = "reports/monthlytargetoutput";
		} else if (reporttype.longValue() == ReportDataFactory.ReportType.YearlyTargetStatus) {
			view = "reports/yearlytargetoutput";
		} else if (reporttype.longValue() == ReportDataFactory.ReportType.FullMonth) {
			view = "reports/fullmonthoutput";
		}else if (reporttype.longValue() == ReportDataFactory.ReportType.Yearly) {
			view = "reports/yearlyoutput";
		}
		return view;

	}

	private ReportData processReport(ReportCriteria reportCriteria,HttpServletRequest request) {
		StringBuffer contextpath = request.getRequestURL();
		int i = contextpath.indexOf(request.getServletPath());
		contextpath.setLength(i);
		reportCriteria.setContextPath(contextpath.toString());
	

		// call report service
		ReportData results = reportFactory.createReportData(reportCriteria);
		return results;

	}

	@RequestMapping(method = RequestMethod.POST, params = "pdfreport", produces = "text/html")
	public void showPDFOutput(
			@ModelAttribute("reportCriteria") ReportCriteria reportCriteria,
			Model uiModel, HttpServletRequest request,HttpServletResponse response) throws JAXBException, FOPException, IOException, TransformerException {
		Long reporttype = reportCriteria.getReportType();
		reportCriteria.setUseFullImageLink(true);
		ReportData results = processReport(reportCriteria,request);
		
		
		// set results in model
		uiModel.addAttribute("results", results);

		// determine transform file
		String xslt = results.getXslTransformFilename();
		String cxslname = "META-INF/web-resources/transform/" + xslt;
		
		
		String xml = results.resultsAsXml();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Setup Transformer
			Resource resource = new ClassPathResource(cxslname);
			Source xsltSrc = new StreamSource(resource.getFile());
			Transformer transformer = tFactory.newTransformer(xsltSrc);

			// Make sure the XSL transformation's result is piped through to FOPx
			Result res = new SAXResult(fop.getDefaultHandler());

			// Setup input
			StringReader reader = new StringReader(xml);
			Source src = new StreamSource(reader);
			
			// Start the transformation and rendering process
			transformer.transform(src, res);

			// prepare response
			response.setContentType("application/pdf");
			response.setContentLength(out.size());

			// send content to browser
			response.getOutputStream().write(out.toByteArray());
			response.getOutputStream().flush();
		} finally {
			out.close();
		}

	}

	private ReportCriteria getDefaultCriteria() {
		ReportCriteria criteria = new ReportCriteria();
		criteria.setDaterangetype(DateRangeType.CurrentMonth);
		criteria.setBreakoutLevel(new Long(2));
		criteria.setExcludeNonExpense(true);

		return criteria;
	}

	@ModelAttribute("reportCriteria")
	public ReportCriteria getReportCriteria(HttpServletRequest request) {
		return getDefaultCriteria();
	}

	@ModelAttribute("categories")
	protected List<Category> referenceCategoryData(
			HttpServletRequest request, Object command, Errors errors)
			throws Exception {
		List<Category> list = categoryService.listAllCategories(true);
		// return model
		return list;
	}


//	@ModelAttribute("breakoutlevel")


//	@ModelAttribute("comptype")

	@ModelAttribute("monthselect")
	protected List<String> referenceMonthList(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
			Date oldest = searchService.getFirstTransDate();
			Date newest = searchService.getMostRecentTransDate();

			return DateUtils.getMonthsForSelect(oldest, newest);
	}

	@ModelAttribute("yearselect")
	protected List<String> referenceYearList(HttpServletRequest request,
			Object command, Errors errors) throws Exception {
		Date oldest = searchService.getFirstTransDate();
		Date newest = searchService.getMostRecentTransDate();

		return DateUtils.getYearsForSelect(oldest, newest);
	}

}
