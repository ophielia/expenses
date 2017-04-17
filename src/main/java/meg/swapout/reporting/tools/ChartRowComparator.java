package meg.swapout.reporting.tools;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;

import meg.swapout.reporting.elements.ChartRow;

public class ChartRowComparator implements Comparator {

	public final static class Sort {
		public final static int ByAmount = 2;
	}

	public final static class SortOrder {
		public final static int DESC = -1;
		public final static int ASC = 1;
	}
	

	private int sortcolumn = 0;
	
	private int sortorder = 1;

	private NumberFormat nf = null;

	public ChartRowComparator(int sortcolumn, NumberFormat nf,int sortorder) {
		super();
		this.sortcolumn = sortcolumn;
		this.nf = nf;
		this.sortorder=sortorder;
	}

	public int compare(Object arg0, Object arg1) {
		ChartRow disp1 = (ChartRow) arg0;
		ChartRow disp2 = (ChartRow) arg1;

		return sortbyamount(disp1, disp2);
	}

	public void setSortcolumn(int sortcolumn) {
		this.sortcolumn = sortcolumn;
	}

	private int sortbyamount(ChartRow disp1, ChartRow disp2) {
		String row2val = disp2.getColumn(sortcolumn);
		String row1val = disp1.getColumn(sortcolumn);
		if (row1val != null & row2val != null) {
			Number num1;
			try {
				num1 =  nf.parse(row1val);
				Number num2 =  nf.parse(row2val);

				if (num1.doubleValue() < num2.doubleValue()) {
					return -1*sortorder;
				} else if (num1.doubleValue() > num2.doubleValue()) {
					return 1*sortorder;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
}
