package meg.swapout.imp;

import meg.swapout.common.imp.FieldFormat;
import meg.swapout.common.imp.FileConfig;

class SocGenFileConfig implements FileConfig {

	public String getFieldDelim() {
		return "\t";
	}

	public int getFileType() {
		return FileConfig.FileType.Delimited;
	}

	public int getStartLine() {
		return 4;
	}

	public FieldFormat[] getFieldFormats() {
		FieldFormat[] formats = new FieldFormat[5];

		formats[0] = new FieldFormat();
		formats[1] = new FieldFormat();
		formats[2] = new FieldFormat();
		formats[3] = new FieldFormat();
		formats[4] = new FieldFormat();

		formats[0].setFieldType(FieldFormat.Type.DateTime);
		formats[0].setFieldTag("field1");
		formats[0].setInputPattern("dd/MM/yyyy");

		formats[1].setFieldType(FieldFormat.Type.String);
		formats[1].setFieldTag("field2");

		formats[2].setFieldType(FieldFormat.Type.String);
		formats[2].setFieldTag("field3");

		formats[3].setFieldType(FieldFormat.Type.Double);
		formats[3].setFieldTag("field4");
		formats[3].setInputPattern("######.##");

		formats[4].setFieldType(FieldFormat.Type.Ignore);
		formats[4].setFieldType(FieldFormat.Type.Ignore);
		formats[4].setFieldTag("field4");

		return formats;

	}

}
