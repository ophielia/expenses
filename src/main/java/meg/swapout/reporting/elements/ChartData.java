package meg.swapout.reporting.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "chart")
public class ChartData {

	private ChartRow headers;
	private List<ChartRow> rows;
	
	@XmlElement
	public ChartRow getHeaders() {
		return headers;
	}
	
	@XmlElement
	public List<ChartRow> getRows() {
		return rows;
	}
	
	public void setNextHeader() {
		
	}
	
	public void addRow(ChartRow row) {
		if (rows == null) {
			rows = new ArrayList<ChartRow>();
		}
		if (row!=null) {
			rows.add(row);	
		}
	}

	public void setHeaders(ChartRow headers) {
		this.headers = headers;		
	}
	
}
