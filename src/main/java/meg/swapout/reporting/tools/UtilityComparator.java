package meg.swapout.reporting.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class UtilityComparator implements Comparator<String> {

	public final static class Sort {
		public final static int ByYearStr=1;
		public final static int ByMonthYearStr=2;
		public final static int ByMonthDayYearStr=3;
		public final static int ByMonth=4;
	}
	
	private int sortby = Sort.ByMonthYearStr;
	private static SimpleDateFormat monthyearformat = new SimpleDateFormat("MM-yyyy");
	private static SimpleDateFormat monthdayyearformat = new SimpleDateFormat("MM-dd-yyyy");
	private static SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthformat = new SimpleDateFormat("MMM",Locale.US);
	
	public int compare(String disp1, String disp2) {


		if (sortby==Sort.ByMonthYearStr) {
			return sortbymonthyearstr(disp1,disp2);
		} else if (sortby == Sort.ByMonthDayYearStr) {
			return sortbymonthdayyearstr(disp1,disp2);
		} else if (sortby == Sort.ByYearStr) {
			return sortbyyearstr(disp1,disp2);
		} else if (sortby == Sort.ByMonth) {
			return sortbymonthstr(disp1,disp2);
		}
		
		
		// default
		return 0;
	}
	
	public void setSortType(int sortkey) {
		this.sortby=sortkey;
	}

	private int sortbymonthyearstr(String strdate1,String strdate2) {
		try {
			Date date1 = monthyearformat.parse(strdate1);
			Date date2 = monthyearformat.parse(strdate2);
			
			if (date1.after(date2)) {
				return 1;
			} else if (date2.after(date1)) {
				return -1;
			}
			return 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private int sortbymonthstr(String strdate1,String strdate2) {
		try {
			Date date1 = monthformat.parse(strdate1);
			Date date2 = monthformat.parse(strdate2);
			
			if (date1.after(date2)) {
				return 1;
			} else if (date2.after(date1)) {
				return -1;
			}
			return 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}	
	
	private int sortbymonthdayyearstr(String strdate1,String strdate2) {
		try {
			Date date1 = monthdayyearformat.parse(strdate1);
			Date date2 = monthdayyearformat.parse(strdate2);
			
			if (date1.after(date2)) {
				return 1;
			} else if (date2.after(date1)) {
				return -1;
			}
			return 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	private int sortbyyearstr(String strdate1,String strdate2) {
		try {
			Date date1 = yearformat.parse(strdate1);
			Date date2 = yearformat.parse(strdate2);
			
			if (date1.after(date2)) {
				return 1;
			} else if (date2.after(date1)) {
				return -1;
			}
			return 0;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}	

}
