package meg.swapout.reporting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.services.*;
import meg.swapout.reporting.elements.ChartData;
import meg.swapout.reporting.elements.ChartRow;
import meg.swapout.reporting.elements.ReportElement;
import meg.swapout.reporting.elements.ReportLabel;
import meg.swapout.reporting.elements.TargetProgressDisp;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;




public class YearlyTargetStatusReportData extends BankReportData {

	public YearlyTargetStatusReportData(ReportCriteria reportCriteria,
										SearchService searchService, CategoryService categoryService,
										TargetService targetService) {
		super(reportCriteria, searchService, categoryService, targetService);
	}




	@Override
	public void crunchNumbers() {
		// fill criteria object
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setTransactionType(TransactionType.Debits);
		String year = getReportCriteria().getYear();
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
		Date start;
		boolean iscurrentyear = false;
		String lastdatetag = null;
		Date calyearend = null;
		try {
			start = dateformat.parse(year);
			Calendar cal = Calendar.getInstance();
			Calendar comp = Calendar.getInstance();
			cal.setTime(start);
			iscurrentyear = cal.get(Calendar.YEAR) == comp.get(Calendar.YEAR);
			// get first of year (Calendar cal), first of next year (Calendar
			// comp)
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			Date startdate = cal.getTime();
			cal.roll(Calendar.YEAR, 1);
			calyearend = cal.getTime();
			criteria.setDateEnd(calyearend);
			lastdatetag = determineLastDateTag(criteria, iscurrentyear);
			Date end = daydateformat.parse(lastdatetag);
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(true);

		// calculate progress in year (how many days into the year)
		// only makes sense, if we're looking at the current year
		double percentageofyear = 100.0;
		if (iscurrentyear) {
			ExpenseCriteria fullyearc = new ExpenseCriteria();
			fullyearc.setDateStart(criteria.getDateStart());
			fullyearc.setDateEnd(calyearend);
			// current criteria contains full year - so use current criteria
			// to get full year day count (will be 365 or 366)
			int fullyear = getDayCount(fullyearc);
			// now, how many days are we into the year?
			ExpenseCriteria intomonth = new ExpenseCriteria();
			intomonth.setDateStart(criteria.getDateStart());
			intomonth.setDateEnd(new Date());
			int partofmonth = getDayCount(intomonth);

			// calculate percentage
			percentageofyear = (Math.round((double) partofmonth
					/ (double) fullyear * 10000.0));
			percentageofyear = percentageofyear / 100;

		}

		// Storage lists
		List<TargetProgressDisp> displayspoint = new ArrayList<TargetProgressDisp>();
		List<TargetProgressDisp> displays = new ArrayList<TargetProgressDisp>();
		Hashtable<Long, TargetDetail> targethash = new Hashtable<Long, TargetDetail>();

		// get Targets for year
		Target target = targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetail> details = target.getTargetdetails();
		for (TargetDetail det : details) {
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// prepare month hashtable (monthkey as key, and TargetProgressDisp as
		// value

		Hashtable<String, ArrayList<TargetProgressDisp>> runningTotals = prepareComparisonTable(
				criteria, lastdatetag);

		// loop through categories
		for (CategoryLevel catlvl : categories) {

			List<CategoryLevel> subcats = categoryService
					.getAllSubcategories(catlvl.getCategory());

			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);

			TargetDetail detail = targethash.get(catlvl
					.getCategory().getId());

			// only gather data if there's a target available for the category
			if (detail != null) {
				// retrieve totals
				List<CategorySummary> results = getExpenseTotalByYear(
						criteria, catlvl.getCategory().getName());

				// loop through results
				if (results != null && results.size() > 0) {
					// if results are available, insert totals in Hashtable, add
					// to
					// category total
					TargetProgressDisp cat = new TargetProgressDisp();
					cat.setCatName(catlvl.getCategory().getName());
					cat.setCatId(catlvl.getCategory().getId());
					// get info from results
					CategorySummary spentsum = results
							.get(0);
					cat.setAmountSpent(spentsum.getSum());
					// add targeted amount to cat

					if (detail != null) {
						cat.setAmountTargeted(detail.getAmount().doubleValue());
					}
					displays.add(cat);

					// add at this point graph information
					TargetProgressDisp catpt = new TargetProgressDisp();
					catpt.setCatName(catlvl.getCategory().getName());
					catpt.setCatId(catlvl.getCategory().getId());
					catpt.setAmountSpent(cat.getAmountSpent());

					// add target info - calculated for the percentage of the
					// year
					double targetedtotal = cat.getAmountTargeted();
					double targetednow = targetedtotal * percentageofyear
							/ 100.0;
					if (targetednow > 0) {
						catpt.setAmountTargeted(targetednow);
					}
					displayspoint.add(catpt);

					// calculate values throughout the year
					addValuesOverTime(criteria, detail, catpt, lastdatetag,
							runningTotals);

				}
			}
		}

		// calculate totals
		double totaltargeted = 0;
		double totalspent = 0;
		double pointtargeted = 0;
		for (TargetProgressDisp targ : displays) {
			totaltargeted += targ.getAmountTargeted();
			totalspent += targ.getAmountSpent();
		}
		for (TargetProgressDisp targ : displayspoint) {
			pointtargeted += targ.getAmountTargeted();
		}
		double statusamt = totalspent > totaltargeted ? totalspent
				- totaltargeted : totaltargeted - totalspent;
		String summary = totalspent > totaltargeted ? statusamt
				+ " over target" : statusamt + " under target";
		double statusamtpoint = totalspent > pointtargeted ? totalspent
				- pointtargeted : pointtargeted - totalspent;
		String fmtstatusamt=nf.format(statusamtpoint);
		String summarypoint = totalspent > pointtargeted ? fmtstatusamt
				+ " over target" : fmtstatusamt + " under target";
				
		ChartRow totalrow = new ChartRow();
		totalrow.addColumn(BankReportData.DispLabel.Summary);
		totalrow.addColumn(nf.format(totalspent));	
		totalrow.addColumn(nf.format(totaltargeted));	
		totalrow.addColumn(summary);
		
		ChartRow totalrowpoint = new ChartRow();
		totalrowpoint.addColumn(BankReportData.DispLabel.Summary);
		totalrowpoint.addColumn(nf.format(totalspent));	
		totalrowpoint.addColumn(nf.format(pointtargeted));	
		totalrowpoint.addColumn(summarypoint);	

		// get url for at this point graph
		String graphpointurl = generateGraph("Target Status (as of today)",
				displayspoint, true);

		// get url for progress graph
		String graphprogressurl = generateProgressGraph(runningTotals);

		// run category breakouts for all main categories
		criteria.setCategorizedType(CategorizedType.All);
		criteria.clearCategoryLists();
		List<ReportElement> allcategory = new ArrayList<ReportElement>();

		if (categories != null) {
			for (CategoryLevel catlvl : categories) {
				ReportElement catre = crunchNumbersCategory(criteria, catlvl,
						false);
				if (catre != null) {
					catre.setDisplay(catlvl.getCategory().getName());
					allcategory.add(catre);
				}
			}
		}

		// put together return objects
		
		// elements
		// target progress
		ReportElement el = new ReportElement();
		el.setTag("targetprogress");
		el.addUrl(graphprogressurl);
		// create headers
		ChartRow headers = new ChartRow();
		headers.addColumn(BankReportData.DispLabel.Category);
		headers.addColumn(BankReportData.DispLabel.Spent);	
		headers.addColumn(BankReportData.DispLabel.Targeted);	
		headers.addColumn(BankReportData.DispLabel.Status);	
		/*ChartData targetstatus = targetProgressToChart(displayspoint,headers);
		targetstatus.addRow(totalrow);
		el.setChart(targetstatus);*/
		addElement(el);
		
		// target point
		ReportElement elpoint = new ReportElement();
		elpoint.setTag("targetpoint");
		elpoint.addUrl(graphpointurl);
		ChartData targetpoint = targetProgressToChart(displayspoint,headers);
		targetpoint.addRow(totalrowpoint);
		elpoint.setChart(targetpoint);
		 
		addElement(elpoint);
		
		// categories
		ReportElement catel = new ReportElement();
		catel.setMembers(allcategory);
		catel.setTag("allcategories");
		addElement(catel);
		



		// labels
		ReportLabel titlelbl = new ReportLabel("lbltitle", BankReportData.DispLabel.YearlyTargetTitle + " - " + year);
		ReportLabel pctlbl = new ReportLabel("lblpctthryear", BankReportData.DispLabel.PercentageThrYear + ":" + nf.format(percentageofyear));
		addLabel(titlelbl);
		addLabel(pctlbl);

		
	}


	/**
	 * Determines whether the last date in the running totals is today's date
	 * (as in the case of the current year) or December 31st, as in the case of
	 * a previous year
	 * 
	 * @param criteria
	 * @param iscurrentyear
	 * @return
	 */
	private String determineLastDateTag(ExpenseCriteria criteria,
			boolean iscurrentyear) {
		if (iscurrentyear) {
			// return current date
			return daydateformat.format(new Date());
		} else {
			// return the last day of the year
			return daydateformat.format(criteria.getDateEnd());
		}
	}




	
	
	private String generateGraph(String graphname,
			List<TargetProgressDisp> results, boolean ishoriz) {
		final String exclabel = "Exceeded Target";
		final String tarlabel = "Target";
		final String spentlabel = "Spent";
		// row keys... are target, spent and exceeded
		// column keys... categories

		// fill in dataset
		List<Double> exc = new ArrayList<Double>();
		List<Double> targ = new ArrayList<Double>();
		List<Double> spe = new ArrayList<Double>();
		List<Double> notarget = new ArrayList<Double>();
		List<String> categories = new ArrayList<String>();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int i = 0;
		for (TargetProgressDisp target : results) {
			String catname = target.getCatName();
			categories.add(i, catname);

			double exceeded = 0;
			double targeted = 0;
			double spent = 0;
			double notarg = 0;
			// breakout according to relationship of target to spent
			if (target.spendingExceedsTarget()) {
				// we'll need two values - exceeded and Target
				exceeded = target.getExceededAmount();
				targeted = target.getAmountTargeted();
			} else if (target.targetDoesntExist()) {
				// we'll only need one value - spent
				notarg = target.getAmountSpent();
			} else if (target.spendingEqualsTarget()) {
				// we'll only need one value - spent
				spent = target.getAmountSpent();
			} else {
				// we'll need two values - spent and Target
				spent = target.getAmountSpent();
				targeted = target.getAmountTargeted() - target.getAmountSpent();
			}

			// add to dataset
			exc.add(i, new Double(exceeded));
			targ.add(i, new Double(targeted));
			spe.add(i, new Double(spent));
			targ.add(i, new Double(targeted));
			notarget.add(i, new Double(notarg));
			i++;
		}

		// fill in dataset
		for (int j = 0; j < categories.size(); j++) {
			String catname = categories.get(j);
			// add spent
			Double spent = spe.get(j);
			double val = spent != null ? spent.doubleValue() : 0;
			dataset.addValue(val, spentlabel, catname);
			// add targeted
			Double targeted = targ.get(j);
			val = targeted != null ? targeted.doubleValue() : 0;
			dataset.addValue(val, tarlabel, catname);
			// add exceeded
			Double excval = exc.get(j);
			val = excval != null ? excval.doubleValue() : 0;
			dataset.addValue(val, exclabel, catname);
			// add no target
			Double notargval = notarget.get(j);
			val = notargval != null ? notargval.doubleValue() : 0;
			dataset.addValue(val, "No Target", catname);
		}

		// create the chart...
		PlotOrientation orient = PlotOrientation.HORIZONTAL;
		if (!ishoriz) {
			orient = PlotOrientation.VERTICAL;
		}
		JFreeChart chart = ChartFactory.createStackedBarChart(graphname, // chart
				// title
				"Category", // domain axis label
				"Status", // range axis label
				dataset, // data
				orient, // orientation
				true, // include legend
				false, // tooltips?
				false // URLs?
				);

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// set colors
		StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.BLUE);
		renderer.setSeriesPaint(1, ChartColor.YELLOW);
		renderer.setSeriesPaint(2, ChartColor.RED);
		renderer.setSeriesPaint(3, ChartColor.LIGHT_CYAN);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "monthlytargets_" + ishoriz + "_"
				+ (new Date()).getTime() + ".png";
		File imagefile = new File(reportCriteria.getImageDir() + filename);
		try {
			ChartUtilities.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reportCriteria.getImageLink() + filename;

	}



	@Override
	public String getXslTransformFilename() {
		return "yearlytarget.xsl";
	}

	@Override
	public String getJspViewname() {
		return "yearlytargetoutput";
	}




}