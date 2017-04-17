package meg.swapout.expense.services;

import meg.swapout.expense.domain.Category;
import meg.swapout.reporting.CompareType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class ExpenseCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Category category;


	public final class SortType {
		public static final String Date = "Date";
		public static final String Category = "Cat";
		public static final String Detail = "Detail";
		public static final String Amount ="Amount";
	}
	
	public static final class SortDirection {
		public static final Long Asc = 1L;
		public static final Long Desc= 2L;
	}	
	
	

	private static Hashtable<Integer, Long> weekhelper;

	static {
		weekhelper = new Hashtable<Integer, Long>();
		weekhelper.put(new Integer(Calendar.SUNDAY), new Long(-8));
		weekhelper.put(new Integer(Calendar.MONDAY), new Long(-2));
		weekhelper.put(new Integer(Calendar.TUESDAY), new Long(-3));
		weekhelper.put(new Integer(Calendar.WEDNESDAY), new Long(-4));
		weekhelper.put(new Integer(Calendar.THURSDAY), new Long(-5));
		weekhelper.put(new Integer(Calendar.FRIDAY), new Long(-6));
		weekhelper.put(new Integer(Calendar.SATURDAY), new Long(-7));
	}

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private Date startdate;

	private Date enddate;

	private CategorizedType categorizedtype;

	private Long categoryid;

	private int dayCount;
	
	private DateRangeType daterangetype;

	private List<CategoryLevel> categoryLevelList;

	private Boolean excludeNonExpense;
	
	private TransactionType transactionType;
	private Long source;

	private CompareType compareType;

	private Boolean showsubcategories;
	
	private String sorttype;
	
	private Long sortdir;

	public void setDateRangeByType(DateRangeType dateRangeType) {
		this.daterangetype = dateRangeType;
		setDateRangeFromType(dateRangeType);

	}
	
	public DateRangeType getDateRangeByType() {
		return daterangetype;
	}
	
	public void setCategorizedType(CategorizedType categorizedType) {
		if (categorizedType!=null ) {
			categorizedtype = categorizedType;
		}

	}

	public CategorizedType getCategorizedType() {
		return categorizedtype;
	}

	public void setCategory(Category category) {
		this.category = category;

	}

	public Category getCategory() {
		return category;
	}

	public void setDateEnd(Date end) {
		enddate = end;

	}

	public Date getDateEnd() {
		return enddate;
	}

	public void setDateStart(Date start) {
		startdate = start;

	}

	public Date getDateStart() {
		return startdate;
	}

	public String getDateStartAsString() {
		return dateformat.format(startdate);
	}

	public String getDateEndAsString() {
		return dateformat.format(enddate);
	}



	public int getDayCount() {
		return dayCount;
	}

	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}

	public void clearCategoryLists() {
		this.categoryLevelList=null;
		this.showsubcategories=false;

	}

	public List<CategoryLevel> getCategoryLevelList() {
		return categoryLevelList;
	}

	public void setCategoryLevelList(List<CategoryLevel> subcategories) {
		this.categoryLevelList = subcategories;
	}

	public void clearCategoryLevelList() {
		this.categoryLevelList = null;
	}

	public void setExcludeNonExpense(Boolean excludeNonExpense) {
this.excludeNonExpense=excludeNonExpense;
		
	}

	public Boolean getExcludeNonExpense() {
		return excludeNonExpense;
	}

	public Long getSource() {
		return source;
	}

	public void setSource(Long source) {
		if (source>0) {
			this.source = source;	
		}
		
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public CompareType getCompareType() {
		return compareType;
	}

	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}

	public void setShowSubcats(Boolean showSubcats) {
		this.showsubcategories = showSubcats;
	}

	public Boolean getShowSubcats() {
		return showsubcategories;
	}	
	
	
	
	public String getSortfield() {
		return sorttype;
	}

	public void setSortfield(String sorttype) {
		this.sorttype = sorttype;
	}


	public Long getSortdir() {
		return sortdir;
	}

	public void setSortdir(Long sortdir) {
		this.sortdir = sortdir;
	}

	public boolean showSingleCategory() {
		// single category set??
		if (getCategory()!=null) {
			if (getShowSubcats()!=null) {
				return !getShowSubcats();
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean showListOfCategories() {
		// single category set, and show subcategories not set
		if (getShowSubcats()!=null && getShowSubcats().booleanValue()) {
			boolean showcategorylist =(getCategoryLevelList() != null
					&& getCategoryLevelList().size() > 0);
			return showcategorylist;
		}
		return false;
	}
	
	/*

	public boolean showBySource() {
		return getSource()!=null && getSource().longValue()!=ImportManager.ImportClient.All;
	}
	 */
	


	private void setDateRangeFromType(DateRangeType dateRangeType) {
		
		
		// return if "ALL"
		if (dateRangeType== DateRangeType.All) {
			// clear start date and end date
			setDateStart(null);
			setDateEnd(null);
			return;
		}		
		// set start and end dates
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		if (dateRangeType== DateRangeType.CurrentMonth) {
			end.add(Calendar.MONTH, 1);
		} else if (dateRangeType== DateRangeType.LastMonth) {
			start.add(Calendar.MONTH, -1);
		}  else if (dateRangeType== DateRangeType.MonthBeforeLast) {
			start.add(Calendar.MONTH, -2);
			end.add(Calendar.MONTH, -1);
		} else if (dateRangeType== DateRangeType.ThisWeek) {
			// get current day
			int currentday = start.get(Calendar.DAY_OF_WEEK);
	
			// get last Saturday
			Long toroll = (Long) weekhelper.get(new Integer(currentday));
			start.add(Calendar.DAY_OF_MONTH, (int) toroll.longValue());
			// get next Saturday
			end = (Calendar) start.clone();
			end.add(Calendar.DAY_OF_MONTH, 7);
	
		} else if (dateRangeType== DateRangeType.LastWeek) {
			// get current day
			int currentday = start.get(Calendar.DAY_OF_WEEK);
	
			// get last Saturday
			Long toroll = (Long) weekhelper.get(new Integer(currentday));
			start.add(Calendar.DAY_OF_MONTH, (int) toroll.longValue());
			start.add(Calendar.DAY_OF_MONTH, -7);
			// get next Saturday
			end = (Calendar) start.clone();
			end.add(Calendar.DAY_OF_MONTH, 7);
	
		}
	
		if (dateRangeType!= DateRangeType.ThisWeek
				&& dateRangeType!= DateRangeType.LastWeek) {
			// set dates to first of month
			start.set(Calendar.DAY_OF_MONTH, 1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		
		if (dateRangeType== DateRangeType.CurrentYear) {
			// set date to january 1st
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.MONTH, Calendar.JANUARY);
			end.set(Calendar.DAY_OF_MONTH, 1);
			end.add(Calendar.MONTH, 1);
		}
		
		if (dateRangeType== DateRangeType.LastYear) {
			// set date to january 1st
			start.add(Calendar.YEAR, -1);
			start.set(Calendar.DAY_OF_MONTH, 1);
			start.set(Calendar.MONTH, Calendar.JANUARY);
			end.set(Calendar.DAY_OF_MONTH, 31);
			end.set(Calendar.MONTH, Calendar.DECEMBER);
			end.set(Calendar.YEAR, start.get(Calendar.YEAR));
		}		
	
		// set dates
		setDateStart(start.getTime());
		setDateEnd(end.getTime());
	
	}

	public ExpenseCriteria clone()  {
		ExpenseCriteria newobj = new ExpenseCriteria();
		newobj.startdate= this.startdate;
		newobj.enddate = this.enddate;
		newobj.categorizedtype = this.categorizedtype;
		newobj.categoryid = this.categoryid;
		newobj.dayCount = this.dayCount;
		newobj.categoryLevelList = this.categoryLevelList;
		newobj.excludeNonExpense = this.excludeNonExpense;
		newobj.transactionType = this.transactionType;
		newobj.source = this.source;
		newobj.compareType = this.compareType;
		newobj.showsubcategories = this.showsubcategories;
		return newobj;
	}

	
}
