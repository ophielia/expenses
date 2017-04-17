package meg.swapout.reporting.tools;

import meg.swapout.expense.domain.ExpenseDao;

import java.util.Comparator;



public class ExpenseComparator implements Comparator {
	
	public final static class Sort {
		public final static int DispCategory=1;
		public final static int Category=2;
	}
	
	private int sortby = Sort.DispCategory;
	
	public int compare(Object arg0, Object arg1) {
		ExpenseDao disp1=(ExpenseDao)arg0;
		ExpenseDao disp2=(ExpenseDao)arg1;
		
		if (sortby==Sort.DispCategory) {
			return sortbydispcat(disp1,disp2);
		} else if (sortby==Sort.Category) {
			return sortbycat(disp1,disp2);
		}
		
		
		// default
		return 0;
	}
	
	public void setSortType(int sortkey) {
		this.sortby=sortkey;
	}

	private int sortbydispcat(ExpenseDao disp1,ExpenseDao disp2) {
		int test = disp1.getDispCat().compareToIgnoreCase(disp2.getDispCat());
		
		if (test==0) {
			return disp1.getCatName().compareToIgnoreCase(disp2.getCatName());
		} 
		
		return test;
	}
	
	private int sortbycat(ExpenseDao disp1,ExpenseDao disp2) {
		return disp1.getCatName().compareToIgnoreCase(disp2.getCatName());
	}	
}
