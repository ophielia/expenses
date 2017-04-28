package meg.swapout.common;

import meg.swapout.reporting.CompareType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
	static SimpleDateFormat dateformat = new SimpleDateFormat("MM-yyyy");

	private DateUtils() throws IllegalAccessException {
		throw new IllegalAccessException("Utility class");
	}

	public static List<String> getMonthsForSelect(Date oldest, Date newest) {
		List<String> months = new ArrayList<>();

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(oldest);
		end.setTime(newest);
		start.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.DAY_OF_MONTH, 1);

		// cycle from end to start, adding as values to list
		dateformat = new SimpleDateFormat("MM-yyyy");
		while (end.after(start)) {
			String value = dateformat.format(end.getTime());
			months.add(value);
			end.add(Calendar.MONTH, -1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		// add first month
		String value = dateformat.format(end.getTime());
		months.add(value);
		return months;
	}

	public static Date getEndOfCalendarYear(Date start) {
		Calendar end = Calendar.getInstance();
		end.setTime(start);
		end.set(Calendar.DAY_OF_MONTH, Calendar.DECEMBER);
		end.set(Calendar.DAY_OF_MONTH, 31);
		return end.getTime();
		
	}
	public static List<String> getYearsForSelect(Date oldest, Date newest) {
		List<String> years = new ArrayList<>();

		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(oldest);
		end.setTime(newest);
		start.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.DAY_OF_MONTH, 1);

		// cycle from end to start, adding as values to list
		dateformat = new SimpleDateFormat("yyyy");
		while (end.after(start)) {
			String value = dateformat.format(end.getTime());
			years.add(value);
			end.add(Calendar.YEAR, -1);
			end.set(Calendar.DAY_OF_MONTH, 1);
		}
		// add first month
		String value = dateformat.format(end.getTime());
		years.add(value);
		return years;
	}

	public static Date getStartDateByCompareType(Date enddate, Date firsttransdate,CompareType compareType) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(enddate);

		if ((compareType != null)
				&& compareType == CompareType.LastMonths) {
			cal.add(Calendar.MONTH, -13);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		} else if ((compareType != null)
				&& compareType == CompareType.CalendarYear) {
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		} else {
			Calendar first = Calendar.getInstance();
			first.setTime(firsttransdate);
			cal.set(Calendar.YEAR, first.get(Calendar.YEAR));
			cal.set(Calendar.MONTH, first.get(Calendar.MONTH));
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		return cal.getTime();
	}
}
