package meg.swapout.reporting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import meg.swapout.common.DateUtils;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.services.*;
import meg.swapout.reporting.elements.ChartData;
import meg.swapout.reporting.elements.ChartRow;
import meg.swapout.reporting.elements.ReportElement;
import meg.swapout.reporting.elements.ReportLabel;
import meg.swapout.reporting.elements.TargetProgressDisp;
import meg.swapout.reporting.tools.UtilityComparator;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class YearlyReportData extends BankReportData {

	public YearlyReportData(ReportCriteria reportCriteria,
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
		getReportCriteria().setBreakoutLevel(1L);
		// parse into date
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
		Date start;
		boolean iscurrentyear = false;
		String lastdatetag = null;
		double percentageofyear = 100.0;
		Date endlastfullmonth;
		try {
			start = dateformat.parse(year);
			Calendar cal = Calendar.getInstance();
			Calendar comp = Calendar.getInstance();
			Date currentdate = cal.getTime();

			// get first of month, first of next month
			cal.setTime(start);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			iscurrentyear = cal.get(Calendar.YEAR) == comp.get(Calendar.YEAR);
			Date startdate = cal.getTime();
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			endlastfullmonth = cal.getTime();

			if (currentdate.before(endlastfullmonth)) {
				// get last complete month
				cal.setTime(currentdate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				endlastfullmonth = cal.getTime();
			}
			// set in criteria
			criteria.setDateStart(startdate);
			criteria.setDateEnd(endlastfullmonth);

			// calculate last day for generating target disp - last day of year
			// or current date (if current year)
			// also calculate percentage through year
			if (iscurrentyear) {
				// return current date
				lastdatetag = daydateformat.format(new Date());
				percentageofyear = getPercentageThrYear(criteria);
			} else {
				// return the last day of the year
				lastdatetag = daydateformat.format(criteria.getDateEnd());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		criteria.setExcludeNonExpense(getReportCriteria()
				.getExcludeNonExpense());

		// run summary report
		ReportElement summary = crunchNumbersSummary(criteria, true);

		// run Target SUMMARY report
		// MM TODO - double coding? this.crunchTargetSummary with
		// BRD.crunchNumbersTargets - get rid of this? parameterize?
		ReportElement targetsum = crunchTargetSummary(criteria);

		// run Target Detail report
		Hashtable<Long, TargetDetail> targethash = new Hashtable<Long, TargetDetail>();
		// get Targets for year
		Target target = targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetail> details = target.getTargetdetails();
		for (TargetDetail det : details) {
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}
		// swap out end date with current date
		Date origenddate = criteria.getDateEnd();
		criteria.setDateEnd(new Date());
		ReportElement targetdet = crunchNumbersYearlyTargetProgress(criteria,
				lastdatetag, iscurrentyear, targethash, percentageofyear);
		criteria.setDateEnd(origenddate);
		
		// run month comparison
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		criteria.setCompareType(CompareType.CalendarYear);
		ReportElement monthcompare = crunchNumbersMonthlyComp(criteria, true,
				"MMM");

		// run year comparison
		criteria.clearCategoryLevelList();
		criteria.clearCategoryLists();
		criteria.setCategory(null);
		criteria.setCompareType(CompareType.CalendarYear);
		ReportElement yearcompare = crunchNumbersYearlyComp(criteria, true);

		// run all category info - all years
		List<ReportElement> categoryallyears = crunchNumbersYearlyCategories(criteria);

		// run all category info
		List<ReportElement> categoryyear = crunchNumbersAllCategories(criteria);

		// run detailed information for other
		// MM fix this ReportElement detailedother = crunchNumbersOtherDetail(criteria);

		// Put Together Elements
		// list of category summary objects - pie graph for entire year all
		// expenses
		summary.setTag("summary");
		addElement(summary);

		// target summary (bargraph of targets with over, under, met and chart
		// of details)
		targetsum.setTag("targetsumm");
		addElement(targetsum);

		// target progress graph - line graph showing spending and target levels
		// over year
		// MM TODO - not working well now - used in other parts of the app - put
		// in BRD??
		targetdet.setTag("targetprogress"); // was targetdetsummaryimg
		addElement(targetdet);

		// month comparison - vertical bar graph showing total spending in bar,
		// with sections by category
		monthcompare.setTag("monthcomparison");
		addElement(monthcompare);

		// year comparison - vertical bar graph showing total spending by year
		// in bar
		// just like month comparison, but by year
		yearcompare.setTag("yearcomparison");
		addElement(yearcompare);

		// this year category detail
		ReportElement catforyear = new ReportElement();
		catforyear.setTag("categoryyear");
		catforyear.setMembers(categoryyear);
		addElement(catforyear);

		// this year category detail
		ReportElement catallyears = new ReportElement();
		catallyears.setTag("categoryallyears");
		catallyears.setMembers(categoryallyears);
		addElement(catallyears);

		// this year other detail
		/*detailedother.setTag("detailedother");
		addElement(detailedother);
*/
		// labels
		ReportLabel ltitle = new ReportLabel("lbltitle",
				BankReportData.YEARLY_SUMMARY_REPORT + " - " + year);
		addLabel(ltitle);
	}

	@Override
	public String getXslTransformFilename() {
		return "yearlyreport.xsl";
	}

	@Override
	public String getJspViewname() {
		return "yearlyoutput";
	}

	private List<ReportElement> crunchNumbersAllCategories(
			ExpenseCriteria criteria) {
		// initialize List of ReportElements
		List<ReportElement> reports = new ArrayList<ReportElement>();

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();

		// initialize MonthTag list and lkup
		List<String> monthtags = getMonthTagList(cpycriteria, "MM-yyyy", false );
		Hashtable<String, Integer> monthtaglkup = new Hashtable<String, Integer>();

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String month : monthtags) {
			Integer key =headers.getColumnCount();
			monthtaglkup.put(month, key);
			headers.addColumn(month);
		}
		int monthcount = monthtags.size();
		int totalscolumn = headers.getColumnCount();
		int avgpermonthcol = totalscolumn + 1;
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// get all top-level categories for year
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// loop through categories, creating ReportElement for each category,
		// and adding to list
		if (categories != null) {
			for (Iterator<CategoryLevel> iter = categories.iterator(); iter
					.hasNext();) {
				CategoryLevel catlvl = iter.next();
				Category category = catlvl.getCategory();
				if (category.getNonexpense() != null
						&& category.getNonexpense().booleanValue()) {
					continue;
				}

				ReportElement re;
				// check if we want to run a direct subcat version here
				Integer allSubcatCount = categoryService.getAllSubcategoriesCount(category);
				if (allSubcatCount > 10) {
					// run for direct subcategories only
					re = processMonthlySubCats(cpycriteria.clone(), headers, monthtags,
							monthtaglkup, catlvl, totalscolumn, monthcount,
							avgpermonthcol);
					// add ReportElements object to List
					reports.add(re);

				}
				re = processDetailedMonthlySubCats(cpycriteria, headers,
						monthtags, monthtaglkup, catlvl, totalscolumn,
						monthcount, avgpermonthcol);

				if (re != null) {
					// add ReportElements object to List
					reports.add(re);
				}

			}
		}
		// end category loop

		return reports;
	}

	private ReportElement processDetailedMonthlySubCats(
			ExpenseCriteria cpycriteria, ChartRow headers,
			List<String> monthtags, Hashtable<String, Integer> monthtaglkup,
			CategoryLevel catlvl, int totalscolumn, int monthcount,
			int avgpermonthcol) {
		Category category = catlvl.getCategory();
		// initialize working calendar
		Calendar cal = Calendar.getInstance();

		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}
		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize bargraph results
		HashMap<String, List<CategorySummary>> bargraphres = new HashMap<String, List<CategorySummary>>();

		// initialize piegraph results
		List<CategorySummary> piegraphres = new ArrayList<CategorySummary>();

		// get all subcategories for category, adding the category
		// itself
		List<CategoryLevel> subcats;
		subcats = categoryService.getAllSubcategories(category);
		subcats.add(catlvl);
		/*
		 * if (category.getId()==57) { subcats =
		 * categoryService.getSubcategoriesToLevel (category.getId(), 2);
		 * subcats.add(catlvl); } else { //MM just for testing subcats = new
		 * ArrayList<CategoryLevel>(); }
		 */

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (CategoryLevel subcat : subcats) {

			// initialize summary CategoryDisp (bargraph)
			CategorySummary cattotal = new CategorySummary();

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategory(subcat.getCategory());

			// initialize chartrow
			ChartRow row = new ChartRow();
			String catname = subcat.getCategory().getName();
			row.addColumn(catname);

			// loop through all months, retrieving expense results for
			// each
			for (String month : monthtags) {
				// set dates in criteria
				Date rundate = null;
				Date startdate;
				Date enddate;
				try {
					rundate = mthyearformat.parse(month);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(rundate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startdate = cal.getTime();
				cal.add(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				enddate = cal.getTime();

				// set dates in criteria
				cpycriteria.setDateStart(startdate);
				cpycriteria.setDateEnd(enddate);
				// retrieve monthly expense total
				/*
				 * MM orig - List<CategorySummary> rawresults =
				 * searchService .getExpenseTotal(cpycriteria,
				 * catlvl.getCategory().getName());
				 */
				List<CategorySummary> rawresults = searchService
						.getExpenseTotal(cpycriteria);

				if (rawresults != null && rawresults.size() > 0) {
					CategorySummary raw = rawresults.get(0);
					raw.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummary> fromhash = bargraphres.get(month);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummary>();
					}
					fromhash.add(raw);
					bargraphres.put(month, fromhash);
					// add value to month column
					Integer key = monthtaglkup.get(month);
					String amount = nf.format(raw.getSum() * -1);
					row.addColumn(amount, key.intValue());
					// add to summary CategoryDisp (pie chart)
					cattotal.addExpenseAmt(raw.getSum() * -1);
				}
			}
			// end month loop

			// add totals column to end of row
			double rowsum = cattotal.getSum();
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / 12; // MM make current year smart
			row.addColumn(nf.format(rowavg), avgpermonthcol);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}

			// add CategoryDisp total to piegraph results
			if (cattotal.getSum() > 0) {
				cattotal.setCatName(catname);
				piegraphres.add(cattotal);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		if (rows != null && rows.size() > 0) {
			for (ChartRow row : rows) {
				// loop through all columns, summing values
				for (int i = 1; i < totalscolumn; i++) {
					String val = row.getColumn(i);
					if (!val.equals("")) {
						Integer colkey = new Integer(i);
						// convert to double
						Double colamt = 0D;
						try {
							Number amount = nf.parse(val);
							colamt = amount.doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// add to columntotal
						Double coltotal = columntotals.get(colkey);
						if (coltotal == null) {
							coltotal = 0D;
						}
						coltotal += colamt;
						columntotals.put(colkey, coltotal);
					}
				}
			}
			ChartRow totalsrow = new ChartRow();
			totalsrow.addColumn("TOTAL");
			double rowtotal = 0;
			for (int i = 1; i < totalscolumn; i++) {
				Integer colkey = new Integer(i);
				Double value = columntotals.get(colkey);
				if (value != null) {
					String valuestr = nf.format(value);
					totalsrow.addColumn(valuestr);
					rowtotal += value.doubleValue();
				} else {
					totalsrow.addColumn("");
				}
			}
			double avgpermonth = rowtotal / monthcount;
			totalsrow.addColumn(nf.format(rowtotal));
			totalsrow.addColumn(nf.format(avgpermonth));
			chartdata.addRow(totalsrow);
		}
		// make piegraph
		String graphurl = generateCategoryGraph(piegraphres,
				category.getName(), 0, 250,250);

		// make bargraph
		String bargraphurl = generateYearToDateGraph(null, bargraphres,
				UtilityComparator.Sort.ByMonthYearStr);

		// assemble ReportElements object
		ReportElement report = new ReportElement();
		report.setChart(chartdata);
		report.addUrl(bargraphurl);
		report.addUrl(graphurl);
		

		return report;
	}

	private ReportElement crunchTargetSummary(ExpenseCriteria criteria) {
		ReportElement targetsumm = new ReportElement();

		String year = getReportCriteria().getYear();

		// Storage lists
		List<TargetProgressDisp> graphdata = new ArrayList<TargetProgressDisp>();
		Hashtable<Long, TargetDetail> targethash = new Hashtable<Long, TargetDetail>();

		// get Targets for year
		Target target = targetService.loadTargetForYear(year);
		// place targets in Hashtable by categoryid
		List<TargetDetail> details = target.getTargetdetails();
		for (Iterator<TargetDetail> iter = details.iterator(); iter
				.hasNext();) {
			TargetDetail det = iter.next();
			Long catid = det.getCatid();
			targethash.put(catid, det);
		}

		// process categories
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// initialize chartdata
		ChartData chartdata = new ChartData();
		ChartRow headers = new ChartRow();
		headers.addColumn("Category");
		headers.addColumn("SPENT");
		headers.addColumn("TARGETED");
		headers.addColumn("Status");
		chartdata.setHeaders(headers);

		// loop through categories
		double totalspent = 0;
		double totaltargeted = 0;

		for (Iterator<CategoryLevel> iter = categories.iterator(); iter
				.hasNext();) {

			CategoryLevel catlvl = iter.next();
			List<CategoryLevel> subcats = categoryService
					.getAllSubcategories(catlvl.getCategory());

			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);
			criteria.setShowSubcats(true);
			// retrieve totals
			/*
			 * MM orig - List results = searchService.getExpenseTotal(criteria,
			 * catlvl.getCategory().getName());
			 */
			List<CategorySummary> results = searchService
					.getExpenseTotal(criteria);

			// loop through results
			if (results != null && results.size() > 0) {
				// if results are available, insert totals in Hashtable, add to
				// category total
				TargetProgressDisp cat = new TargetProgressDisp();
				cat.setCatName(catlvl.getCategory().getName());
				// get info from results
				CategorySummary spentsum = results
						.get(0);
				cat.setAmountSpent(spentsum.getSum());
				// add target info
				TargetDetail detail = targethash
						.get(catlvl.getCategory().getId());
				if (detail != null) {
					cat.setAmountTargeted(detail.getAmount().doubleValue());
				}
				graphdata.add(cat);

				// gather info and make row
				// get amtspent
				double amtspent = cat.getAmountSpent();
				// get amttargeted
				double amttargeted = cat.getAmountTargeted();
				// get status
				String status = cat.getStatusMessage();

				// add row
				ChartRow row = new ChartRow();
				row.addColumn(catlvl.getCategory().getName());
				row.addColumn(nf.format(amtspent));
				row.addColumn(nf.format(amttargeted));
				row.addColumn(status);
				chartdata.addRow(row);

				// add sums
				totalspent += amtspent;
				totaltargeted += amttargeted;
			}
		}

		// get url for graph
		String graphurl = generateTargetSummGraph("Target Status", graphdata,
				false);

		// now totals to list
		String status;
		if (totaltargeted == 0) {
			status = "No Target";
		} else if (totaltargeted == totalspent) {
			status = "Target Met";
		} else if (totaltargeted > totalspent) {
			double percentage = totalspent / totaltargeted;
			percentage = Math.round(percentage * 100);
			status = percentage + "% of Target spent";
		} else {
			double percentage = totalspent / totaltargeted;
			percentage = Math.round(Math.abs(1 - percentage) * 100);

			status = "Target exceeded by "
					+ nf.format(totalspent - totaltargeted) + " Euros ("
					+ percentage + "%)";
		}
		ChartRow row = new ChartRow();
		row.addColumn("TOTAL");
		row.addColumn(nf.format(totalspent));
		row.addColumn(nf.format(totaltargeted));
		row.addColumn(status);
		chartdata.addRow(row);

		// put together report elements to return them...
		targetsumm.setChart(chartdata);
		targetsumm.addUrl(graphurl);

		return targetsumm;

	}





	protected String generateTargetSummGraph(String graphname, List<TargetProgressDisp> results,
			boolean ishoriz) {
		final String exclabel = "Exceeded Target";
		final String tarlabel = "Target";
		final String spentlabel = "SPENT";
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
		for (Iterator<TargetProgressDisp> iter = results.iterator(); iter.hasNext();) {
			TargetProgressDisp target = iter.next();
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
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtils.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getReportCriteria().getImageLink() + filename;

	}

	private ReportElement crunchNumbersOtherDetail(ExpenseCriteria criteria) {
/*

			// initialize List of ReportElements
		ReportElement report;

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();

		// initialize MonthTag list and lkup
		List<String> monthtags = getMonthTagList(cpycriteria, "MM-yyyy");
		Hashtable<String, Integer> monthtaglkup = new Hashtable<String, Integer>();

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String month : monthtags) {
			Integer key = new Integer(headers.getColumnCount());
			monthtaglkup.put(month, key);
			headers.addColumn(month);
		}
		int monthcount = monthtags.size();
		int totalscolumn = headers.getColumnCount();
		int avgpermonthcol = totalscolumn + 1;
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// run numbers for Other category
		// TODO MM - automatic breakout handlind
		/*Category othercat = categoryService
				.getCategoryByName(CategoryService.OTHERCATEGORYNAME);
		CategoryLevel catlvl = categoryService.getAsCategoryLevel(othercat
				.getId());

		report = processDetailedMonthlySubCats(criteria, headers, monthtags,
				monthtaglkup, catlvl, totalscolumn, monthcount, avgpermonthcol);

		return report;

	*/
return null;
	}


	protected ReportElement crunchNumbersYearComparison(
			ExpenseCriteria origcriteria) {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.setExcludeNonExpense(origcriteria.getExcludeNonExpense());
		criteria.setTransactionType(TransactionType.Debits);

		// set start and end dates
		// end date is first of next month
		// start date depends upon the comparetype, and is
		// either 12 months earlier, the last calendar year, or all
		Date enddate = origcriteria.getDateEnd();
		Calendar cal = Calendar.getInstance();
		cal.setTime(enddate);

		Date firstdate = searchService.getFirstTransDate();

		Date startdate = DateUtils.getStartDateByCompareType(enddate,firstdate,origcriteria.getCompareType());

		criteria.setDateEnd(enddate);
		criteria.setDateStart(startdate);

		// Storage lists
		Hashtable<String, List<CategorySummary>> results = new Hashtable<String, List<CategorySummary>>();
		List<CategorySummary> disptotals = new ArrayList<CategorySummary>();
		int yearcount = 0;
		// get all level 1 categories
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// loop through categories
		for (CategoryLevel catlvl : categories) {
			List<CategoryLevel> subcats = categoryService
					.getAllSubcategories(catlvl.getCategory());
			// set categories in criteria
			subcats.add(catlvl);
			criteria.setCategoryLevelList(subcats);

			// retrieve totals
			List<CategorySummary> totals = searchService
					.getExpenseTotalByYear(criteria);

			// loop through results
			if (totals != null && totals.size() > 0) {
				// if results are available, insert totals in Hashtable, add to
				// category total
				CategorySummary cattotal = new CategorySummary();
				cattotal.setCatName(catlvl.getCategory().getName());
				for (CategorySummary catsum : totals) {
					cattotal.addExpenseAmt(catsum.getSum());
					// month key
					// get date formatter
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
					String yearkey = dateformat.format(catsum.getSummaryDate());
					// insert in result hash
					if (!results.containsKey(yearkey)) {
						yearcount++;
					}
					List<CategorySummary> listfordate = results.get(yearkey);
					if (listfordate == null) {
						listfordate = new ArrayList<CategorySummary>();
					}
					listfordate.add(catsum);
					results.put(yearkey, listfordate);

				}
				// add category total to list
				disptotals.add(cattotal);
			}
		}

		// get url for graph
		String graphurl = generateMMYearToDateGraph(results);

		// count months and add to display totals (for showing average per
		// month)
		CategorySummary totsum = new CategorySummary();
		totsum.setCatName("TOTAL");
		totsum.setAveragePerDivisor(yearcount);
		for (Iterator<CategorySummary> iter = disptotals.iterator(); iter.hasNext();) {
			CategorySummary catsum = iter.next();
			double avgpermonth = catsum.getSum() / yearcount;
			catsum.setAveragePerDivisor(avgpermonth);
			totsum.addExpenseAmt(catsum.getSum());
		}
		double avgpermonth = totsum.getSum() / yearcount;
		totsum.setAveragePerDivisor(avgpermonth);
		disptotals.add(totsum);

		// put together return objects
		ReportElement re = new ReportElement();
		ChartRow headers = new ChartRow();
		headers.addColumn(BankReportData.SUBCATEGORY);
		headers.addColumn(BankReportData.AMOUNT);
		ChartData chartdat = catSummaryToChart(disptotals, headers);
		re.setChart(chartdat);
		re.addUrl(graphurl);

		return re;
	}

	private List<ReportElement> crunchNumbersYearlyCategories(
			ExpenseCriteria criteria) {
		// initialize List of ReportElements
		List<ReportElement> reports = new ArrayList<ReportElement>();

		// initialize working calendar
		Calendar cal = Calendar.getInstance();
		// set report run date
		Integer reportrun = new Integer(getReportCriteria().getYear());
		int desireddate = reportrun.intValue();
		// start date is earliest possible, or 5 years ago
		Date firstdate = searchService.getFirstTransDate();
		cal.setTime(firstdate);
		int firstpossyear = cal.get(Calendar.YEAR);
		int beginyear = desireddate - 5 > firstpossyear ? desireddate - 5
				: firstpossyear;
		cal.set(Calendar.YEAR, beginyear);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date startdate = cal.getTime();
		cal.set(Calendar.YEAR, desireddate + 1);
		Date enddate = cal.getTime();
		int yearcount = desireddate - beginyear + 1;

		// make copy of criteria
		ExpenseCriteria cpycriteria = criteria.clone();
		cpycriteria.setDateEnd(enddate);
		cpycriteria.setDateStart(startdate);

		// initialize Yeartag list and lkup
		List<String> yeartags = new ArrayList<String>();
		for (int i = 0; i < yearcount; i++) {
			yeartags.add(beginyear + i + "");
		}
		Hashtable<String, Integer> yeartaglkup = new Hashtable<String, Integer>();

		// get start and end dates

		// create headers row, find values for monthcount, and set totalscolumn
		// and avgpermonth column
		ChartRow headers = new ChartRow();
		headers.addColumn("Sub-Category");
		for (String year : yeartags) {
			Integer key = headers.getColumnCount();
			yeartaglkup.put(year, key);
			headers.addColumn(year);
		}
		int totalscolumn = headers.getColumnCount();
		headers.addColumn("Total");
		headers.addColumn("Avg Per Month");

		// get all top-level categories for year
		List<CategoryLevel> categories = categoryService
				.getCategoriesUpToLevel(1);

		// loop through categories, creating ReportElement for each category,
		// and adding to list
		if (categories != null) {
			for (Iterator<CategoryLevel> iter = categories.iterator(); iter.hasNext();) {
				CategoryLevel catlvl = iter.next();

				ReportElement report = processYearlySubCategories(catlvl,
						cpycriteria, headers, yeartaglkup, yeartags, yearcount,
						totalscolumn);

				// add ReportElements object to List
				if (report != null) {
					reports.add(report);
				}

			}
			// end category loop

		}

		return reports;

	}

	private ReportElement processYearlySubCategories(CategoryLevel catlvl,
			ExpenseCriteria cpycriteria, ChartRow headers,
			Hashtable<String, Integer> yeartaglkup, List<String> yeartags,
			int yearcount, int totalscolumn) {
		Category category = catlvl.getCategory();
		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}

		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);


		// initialize bargraph results
		HashMap<String, List<CategorySummary>> bargraphres = new HashMap<String, List<CategorySummary>>();

		// get direct subcategories for category
		List<Category> subcats;
		subcats = categoryService.getDirectSubcategories(category.getId());
		subcats.add(category);

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (Category subcat : subcats) {
			// get all subcategories for this subcategory
			List<CategoryLevel> detailcats;
			if (subcat.getId() != category.getId()) {
				detailcats = categoryService.getAllSubcategories(subcat);
			} else {
				detailcats = new ArrayList<CategoryLevel>();
			}
			CategoryLevel lvl = categoryService.getAsCategoryLevel(subcat
					.getId());
			detailcats.add(lvl);

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategoryLevelList(detailcats);
			cpycriteria.setShowSubcats(true);

			// initialize chartrow, rowsum
			ChartRow row = new ChartRow();
			String catname = subcat.getName();
			row.addColumn(catname);
			double rowsum = 0;

			// get results by year
			List<CategorySummary> rawresults = searchService
					.getExpenseTotalByYear(cpycriteria);

			// loop through all years, retrieving expense results for
			// each
			for (String year : yeartags) {
				// get matching year from results
				long matchyear = new Long(year).longValue();
				CategorySummary thisyear = null;
				for (CategorySummary test : rawresults) {
					if (test.getYear().longValue() == matchyear) {
						thisyear = test;
						break;
					}
				}

				if (thisyear != null) {
					thisyear.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummary> fromhash = bargraphres.get(year);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummary>();
					}
					fromhash.add(thisyear);
					bargraphres.put(year, fromhash);
					// add value to year column
					Integer key = yeartaglkup.get(year);
					String amount = nf.format(thisyear.getSum() * -1);
					row.addColumn(amount, key.intValue());
					rowsum += thisyear.getSum() * -1;
				}
			}
			// end year loop

			// add totals column to end of row
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / yearcount / 12; // MM make current
														// year smart
			row.addColumn(nf.format(rowavg), totalscolumn + 1);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		if (rows != null) {
			for (ChartRow row : rows) {
				// loop through all columns, summing values
				for (int i = 1; i < totalscolumn; i++) {
					String val = row.getColumn(i);
					if (!val.equals("")) {
						Integer colkey = new Integer(i);
						// convert to double
						Double colamt = 0D;
						try {
							Number amount = nf.parse(val);
							colamt = amount.doubleValue();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// add to columntotal
						Double coltotal = columntotals.get(colkey);
						if (coltotal == null) {
							coltotal = 0D;
						}
						coltotal += colamt;
						columntotals.put(colkey, coltotal);
					}
				}
			}
			ChartRow totalsrow = new ChartRow();
			totalsrow.addColumn("TOTAL");
			double rowtotal = 0;
			for (int i = 1; i < totalscolumn; i++) {
				Integer colkey = new Integer(i);
				Double value = columntotals.get(colkey);
				if (value != null) {
					String valuestr = nf.format(value);
					totalsrow.addColumn(valuestr);
					rowtotal += value.doubleValue();
				} else {
					totalsrow.addColumn("");
				}
			}

			double avgperyear = rowtotal / yearcount / 12;
			totalsrow.addColumn(nf.format(rowtotal));
			totalsrow.addColumn(nf.format(avgperyear));
			chartdata.addRow(totalsrow);
		}
		// make bargraph
		String title = category.getName() + " - Yearly Comparison";
		String bargraphurl = generateYearToDateGraph(title, bargraphres,
				UtilityComparator.Sort.ByYearStr);

		// assemble ReportElements object
		ReportElement report = new ReportElement();
		report.setChart(chartdata);
		report.addUrl(bargraphurl);

		return report;

	}

	private ReportElement processMonthlySubCats(ExpenseCriteria cpycriteria,
			ChartRow headers, List<String> monthtags,
			Hashtable<String, Integer> monthtaglkup, CategoryLevel catlvl,
			int totalscolumn, int monthcount, int avgpermonthcol) {
		Category category = catlvl.getCategory();
		// initialize working calendar
		Calendar cal = Calendar.getInstance();

		if (category.getNonexpense() != null
				&& category.getNonexpense().booleanValue()) {
			return null;
		}
		// initialize ChartData - with headers
		ChartData chartdata = new ChartData();
		chartdata.setHeaders(headers);

		// initialize bargraph results
		HashMap<String, List<CategorySummary>> bargraphres = new HashMap<String, List<CategorySummary>>();

		// initialize piegraph results
		List<CategorySummary> piegraphres = new ArrayList<CategorySummary>();

		// get direct subcategories for category
		List<Category> subcats;
		subcats = categoryService.getDirectSubcategories(category.getId());
		subcats.add(category);

		// loop through subcategories, retrieving monthly info for each,
		// and
		// summing monthly info into CategoryDisp object for summary
		// graph
		for (Category subcat : subcats) {
			// get all subcategories for this subcategory
			List<CategoryLevel> detailcats;
			if (subcat.getId() != category.getId()) {
				detailcats = categoryService.getAllSubcategories(subcat);
			} else {
				detailcats = new ArrayList<CategoryLevel>();
			}
			CategoryLevel lvl = categoryService.getAsCategoryLevel(subcat
					.getId());
			detailcats.add(lvl);

			// initialize summary CategoryDisp (bargraph)
			CategorySummary cattotal = new CategorySummary();

			// set category in criteria
			cpycriteria.clearCategoryLevelList();
			cpycriteria.clearCategoryLists();
			cpycriteria.setCategory(null);
			cpycriteria.setCategoryLevelList(detailcats);

			// initialize chartrow
			ChartRow row = new ChartRow();
			String catname = subcat.getName();
			row.addColumn(catname);

			// loop through all months, retrieving expense results for
			// each
			for (String month : monthtags) {
				// set dates in criteria
				Date rundate = null;
				Date startdate;
				Date enddate;
				try {
					rundate = mthyearformat.parse(month);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(rundate);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startdate = cal.getTime();
				cal.add(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				enddate = cal.getTime();

				// set dates in criteria
				cpycriteria.setDateStart(startdate);
				cpycriteria.setDateEnd(enddate);
				cpycriteria.setShowSubcats(true);
				// retrieve monthly expense total
				/*
				 * orig List<CategorySummary> rawresults = searchService
				 * .getExpenseTotal(cpycriteria,
				 * catlvl.getCategory().getName());
				 */
				List<CategorySummary> rawresults = searchService
						.getExpenseTotal(cpycriteria);

				if (rawresults != null && rawresults.size() > 0) {
					CategorySummary raw = rawresults.get(0);
					raw.setCatName(catname);
					// add CategoryDisp to bargraph chart
					List<CategorySummary> fromhash = bargraphres.get(month);
					if (fromhash == null) {
						fromhash = new ArrayList<CategorySummary>();
					}
					fromhash.add(raw);
					bargraphres.put(month, fromhash);
					// add value to month column
					Integer key = monthtaglkup.get(month);
					String amount = nf.format(raw.getSum() * -1);
					row.addColumn(amount, key.intValue());
					// add to summary CategoryDisp (pie chart)
					cattotal.addExpenseAmt(raw.getSum() * -1);
				}
			}
			// end month loop

			// add totals column to end of row
			double rowsum = cattotal.getSum();
			row.addColumn(nf.format(rowsum), totalscolumn);

			// add avg column to end of row
			double rowavg = rowsum / 12; // MM make current year smart
			row.addColumn(nf.format(rowavg), avgpermonthcol);

			// add chart row to chartdata
			if (rowsum > 0) {
				chartdata.addRow(row);
			}

			// add CategoryDisp total to piegraph results
			if (cattotal.getSum() > 0) {
				cattotal.setCatName(catname);
				piegraphres.add(cattotal);
			}
		}
		// end subcategory loop

		// make totals row for the chart
		// sum up columns
		// add to row
		Hashtable<Integer, Double> columntotals = new Hashtable<Integer, Double>();
		List<ChartRow> rows = chartdata.getRows();
		for (ChartRow row : rows) {
			// loop through all columns, summing values
			for (int i = 1; i < totalscolumn; i++) {
				String val = row.getColumn(i);
				if (!val.equals("")) {
					Integer colkey = new Integer(i);
					// convert to double
					Double colamt = 0D;
					try {
						Number amount = nf.parse(val);
						colamt = amount.doubleValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// add to columntotal
					Double coltotal = columntotals.get(colkey);
					if (coltotal == null) {
						coltotal = 0D;
					}
					coltotal += colamt;
					columntotals.put(colkey, coltotal);
				}
			}
		}
		ChartRow totalsrow = new ChartRow();
		totalsrow.addColumn("TOTAL");
		double rowtotal = 0;
		for (int i = 1; i < totalscolumn; i++) {
			Integer colkey = new Integer(i);
			Double value = columntotals.get(colkey);
			if (value != null) {
				String valuestr = nf.format(value);
				totalsrow.addColumn(valuestr);
				rowtotal += value.doubleValue();
			} else {
				totalsrow.addColumn("");
			}
		}
		double avgpermonth = rowtotal / monthcount;
		totalsrow.addColumn(nf.format(rowtotal));
		totalsrow.addColumn(nf.format(avgpermonth));
		chartdata.addRow(totalsrow);

		// make piegraph
		String graphurl = generateCategoryGraph(piegraphres,
				category.getName(), 0, 250, 250);

		// make bargraph
		String bargraphurl = generateYearToDateGraph(null, bargraphres,
				UtilityComparator.Sort.ByMonthYearStr);

		// assemble ReportElements object
		ReportElement report = new ReportElement();
		report.setDisplay(BankReportData.Detail + " - " + category.getName());
		report.setChart(chartdata);
		report.addUrl(graphurl);
		report.addUrl(bargraphurl);

		return report;
	}

	private String generateMMYearToDateGraph(
			Hashtable<String, List<CategorySummary>> results) {
		// row keys... are categories
		// column keys... months - sort to prepare
		Set<String> months = results.keySet();
		List<String> monthlist = new ArrayList<String>();
		monthlist.addAll(months);

		UtilityComparator comp = new UtilityComparator();
		comp.setSortType(UtilityComparator.Sort.ByYearStr);

		Collections.sort(monthlist, comp);

		// fill in dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Iterator<String> iter = monthlist.iterator(); iter.hasNext();) {
			String monthkey = iter.next();
			List<CategorySummary> catsumdisps = results.get(monthkey);
			// iterate through displays
			if (catsumdisps != null) {
				for (Iterator<CategorySummary> iterator = catsumdisps.iterator(); iterator
						.hasNext();) {
					CategorySummary catsum = iterator
							.next();
					dataset.addValue(Math.abs(catsum.getSum()),
							catsum.getCatName(), monthkey);
				}
			}
		}

		// create the chart...
		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Year Comparison", // chart title
				"Year", // domain axis label
				"Expenditure", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
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
		plot.setForegroundAlpha(.5f);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 6.0));

		// save graph and return url
		String filename = "fullreport_comparison_" + (new Date()).getTime()
				+ ".png";
		File imagefile = new File(getReportCriteria().getImageDir() + filename);
		try {
			ChartUtils.saveChartAsPNG(imagefile, chart, 550, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getReportCriteria().getImageLink() + filename;
	}
}