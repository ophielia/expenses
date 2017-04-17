package meg.swapout.reporting.elements;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class ReportLabel {

	public ReportLabel(String tag, String text) {
		this.tag = tag;
		this.text = text;

	}
	public ReportLabel() {
		super();
	}

	private String tag;

	private String text;

	@XmlAttribute(name="tag")
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@XmlElement
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
