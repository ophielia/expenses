package meg.swapout.reporting.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ChartRow {
	private List<String> columns;

	public void addColumn(String colval) {
		if (columns==null) {
			columns=new ArrayList<String>();
		}
		if (colval!=null) {
			columns.add(colval);
		}
	}


	public void addColumn(String colval, int index) {
		if (columns==null) {
			columns=new ArrayList<String>();
		}
		if (colval!=null) {
			while (columns.size()<=index) {
				columns.add("");
			}
			columns.set(index,colval);
		}
	}
	
	@XmlElement
	public List<String> getColumns() {
		return columns;
	}

	public String getColumn(int index) {
		if (columns != null){
			if (columns.size()>index) {
				return (String)columns.get(index);
			}
		}
		return null;
	}


	public int getColumnCount() {
		if (columns!=null) {
			return columns.size();
		}
		return 0;
	}


	public void setColumn(String colval, int index) {
		if (columns==null) {
			columns=new ArrayList<String>();
		}
		if (colval!=null) {
			while (columns.size()<index) {
				columns.add("");
			}
			columns.set(index,colval);
		}
	}

}
