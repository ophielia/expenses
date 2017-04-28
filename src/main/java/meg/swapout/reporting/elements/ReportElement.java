package meg.swapout.reporting.elements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ReportElement {

	private String tag;

	private ChartData chart;

	private List<String> urls;

	private String display;

	private List<ReportElement> members;
	
	@XmlAttribute(name = "tag")
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@XmlElement
	public ChartData getChart() {
		return chart;
	}

	public void setChart(ChartData chart) {
		this.chart = chart;
	}

	@XmlElement
	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public void addUrl(String urls) {
		if (urls != null) {
			if (this.urls == null)
				this.urls = new ArrayList<String>();
			this.urls.add(urls);
		}
	}

	public void setDisplay(String name) {
		this.display = name;
		
	}

	@XmlElement
	public String getDisplay() {
		return this.display;
	}

	@XmlElement
	public List<ReportElement> getMembers() {
		return members;
	}
	

	public void setMembers(List<ReportElement> members) {
		this.members = members;
	}
	
	public void addMember(ReportElement member) {
		if (member != null) {
			if (this.members== null)
				this.members = new ArrayList<ReportElement>();
			this.members.add(member);
		}
	}

}
