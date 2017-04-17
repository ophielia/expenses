package meg.swapout.reporting.elements;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;



public interface ReportData {

	void crunchNumbers() ;
	
	String getXslTransformFilename();
	
	String getJspViewname();
	
	List<ReportElement> getElements();
	
	List<ReportLabel> getReportLabels();
	
	Map<String,Object> resultsAsHashMap();
	
	String resultsAsXml() throws JAXBException;
	}