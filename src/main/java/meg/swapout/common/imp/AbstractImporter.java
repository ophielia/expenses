package meg.swapout.common.imp;

import java.text.*;

public abstract class AbstractImporter implements Importer {

	public Object formatField(String field, FieldFormat format)
			throws ParseException {
		switch (format.getFieldType()) {
		case FieldFormat.Type.String:
			return field;
		case FieldFormat.Type.Double:
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(format.getLocale());
			symbols.setGroupingSeparator('.');
			symbols.setDecimalSeparator(',');
			DecimalFormat numberformat = new DecimalFormat(format.getInputPattern(),symbols);
			String fieldToParse = stripSpaces(field);
			return numberformat.parse(fieldToParse);
		case FieldFormat.Type.DateTime:
			DateFormat dateformat = new SimpleDateFormat(format.getInputPattern());
			return dateformat.parse(field);

		default:
			return null;
		}
	}

	private String stripSpaces(String field) {
		StringBuilder stripped = new StringBuilder();
		StringBuilder fieldbuf = new StringBuilder(field);
		for (int 	i = 0; i < field.length(); i++) {
			String check =fieldbuf.substring(i,i+1);
			if (check!=null && (check.equals("+") || check.equals(" "))) continue;
			stripped.append(fieldbuf.substring(i,i+1));
		}
		return stripped.toString();
	}

}
