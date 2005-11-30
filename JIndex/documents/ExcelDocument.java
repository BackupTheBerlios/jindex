package documents;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class ExcelDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name" };

	public static Document Document(File f, String mimetype) {

		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("name", f.getName()));

		doc.add(Field.Text("type", mimetype));

		try {
			if(f.getName().endsWith(".xls"))
				return CSV(f, doc);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	
	private static Document CSV(File f, Document doc) throws BiffException, IOException {
		Workbook w = Workbook.getWorkbook(f);
		String name  ="";
		ArrayList list = new ArrayList();
		for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
			Sheet s = w.getSheet(sheet);

			if (!(s.getSettings().isHidden())) {
				name = s.getName();

				Cell[] row = null;

				for (int i = 0; i < s.getRows(); i++) {
					row = s.getRow(i);

					if (row.length > 0) {
						if (!(row[0].isHidden())) {
							list.add(row[0].getContents());
							// Java 1.4 code to handle embedded commas
							// bw.write("\"" +
							// row[0].getContents().replaceAll("\"","\"\"") +
							// "\"");
						}

						for (int j = 1; j < row.length; j++) {
							// System.out.println(',');
							if (!(row[j].isHidden())) {
								list.add(row[j].getContents());
							}
						}
					}
				}
			}
		}
		System.out.println("LIST == "+list.toString());
		doc.add(Field.Text("filecontents", list.toString()));
		doc.add(Field.Keyword("name", name));
		return doc;
	}



	public void CSV(Workbook w, String encoding, boolean hide) {
		if (encoding == null || !encoding.equals("UnicodeBig")) {
			encoding = "UTF8";
		}

		for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
			Sheet s = w.getSheet(sheet);

			if (!(hide && s.getSettings().isHidden())) {
				System.out.println(s.getName());

				Cell[] row = null;

				for (int i = 0; i < s.getRows(); i++) {
					row = s.getRow(i);

					if (row.length > 0) {
						if (!(hide && row[0].isHidden())) {
							System.out.println(row[0].getContents());
							// Java 1.4 code to handle embedded commas
							// bw.write("\"" +
							// row[0].getContents().replaceAll("\"","\"\"") +
							// "\"");
						}

						for (int j = 1; j < row.length; j++) {
							// System.out.println(',');
							if (!(hide && row[j].isHidden())) {
								System.out.println(row[j].getContents());
								// Java 1.4 code to handle embedded quotes
								// bw.write("\"" +
								// row[j].getContents().replaceAll("\"","\"\"")
								// + "\"");
							}
						}
					}
					// System.out.println();
				}
			}
		}
	}

	public void features(Workbook w, String encoding) {
		if (encoding == null || !encoding.equals("UnicodeBig")) {
			encoding = "UTF8";
		}

		for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
			Sheet s = w.getSheet(sheet);

			System.out.println(s.getName());

			Cell[] row = null;
			Cell c = null;

			for (int i = 0; i < s.getRows(); i++) {
				row = s.getRow(i);

				for (int j = 0; j < row.length; j++) {
					c = row[j];
					if (c.getCellFeatures() != null) {
						CellFeatures features = c.getCellFeatures();
						StringBuffer sb = new StringBuffer();
						CellReferenceHelper.getCellReference(c.getColumn(), c.getRow(), sb);

						System.out.println("Cell " + sb.toString() + " contents:  " + c.getContents());

						System.out.println(" comment: " + features.getComment());
					}
				}
			}
		}
	}


    public String[] getSearchFields() {
       return fields;
    }

}
