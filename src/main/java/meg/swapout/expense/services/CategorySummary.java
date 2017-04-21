package meg.swapout.expense.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CategorySummary {

	private String catName;

	private int averageDivisor;

	private double sum;

	private int expCount;

	private double average;

	private double averagePerDay;

	private Date summaryDate;

	private double percentageTotal;
	
	private String monthyear;
	
	private Long year;
	
	 private SimpleDateFormat monthyearfmt = new SimpleDateFormat("MM-yyyy");
	 private SimpleDateFormat yearfmt = new SimpleDateFormat("yyyy");
	

	public CategorySummary(String catname, int daycount) {
		this.catName = catname;
		this.averageDivisor = daycount;
	}

	public CategorySummary(String monthyear, double sum) {
		setMonthyear(monthyear);
		this.sum = sum;
	}
	
	public CategorySummary(Long year, double sum) {
		setYear(year);
		this.sum = sum;
	}	
	
/*	public CategorySummary(double sum) {
		this.sum = sum;
	}		*/
	public CategorySummary(Double sum) {
		if (sum!=null) {
			this.sum = sum.doubleValue();	
		}
		
	}


	
	public CategorySummary(Long year, String catname, double sum) {
		setYear(year);
		this.catName=catname;
		this.sum = sum;
	}	
	
	public CategorySummary(String monthyear, String catName, double sum) {
		setYear(year);
		this.catName=catName;
		this.sum = sum;
	}		
	
	public CategorySummary() {

	}

	public void addExpenseAmt(Double amount) {
		sum += amount.doubleValue();
		expCount++;
		recalculate();
	}

	private void recalculate() {
		// recalculate
		average = sum / (double) expCount;
		averagePerDay = sum / (double) averageDivisor;
	}
	
	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getAveragePerDivisor() {
		return averagePerDay;
	}

	public void setAveragePerDivisor(double averagePerDay) {
		this.averagePerDay = averagePerDay;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public String getCatName() {
		return catName;
	}

	public void setSummaryDate(Date date) {
		this.summaryDate = date;
	}

	public Date getSummaryDate() {
		return summaryDate;
	}

	public void setCatName(String dispname) {
		this.catName = dispname;
	}

	public void setAverageDivisor(int monthcount) {
		this.averageDivisor = monthcount;
		recalculate();
	}

	public void setPercentageTotal(double total) {
		this.percentageTotal = total;
	}

	public double getPercentageOfTotal() {
		return (this.sum/this.percentageTotal)*100.0;
	}

	public String getMonthyear() {
		return monthyear;
	}

	public void setMonthyear(String monthyear) {
		this.monthyear = monthyear;
		try {
			Date date = monthyearfmt.parse(monthyear);
			setSummaryDate(date);
		} catch (ParseException e) {
			setSummaryDate(new Date());
		}
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
		try {
			Date date = yearfmt.parse(year + "");
			setSummaryDate(date);
		} catch (ParseException e) {
			setSummaryDate(new Date());
		}		
	}
	
	
	
	
}
