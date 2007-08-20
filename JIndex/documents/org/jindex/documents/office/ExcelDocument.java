package org.jindex.documents.office;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.jindex.documents.SearchDocument;

public class ExcelDocument implements SearchDocument {
	Logger log = Logger.getLogger(ExcelDocument.class);
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name" };

	public static Document Document(File f, String mimetype) {

		Document doc = new Document();

		doc.add(getField("path", f.getPath()));
		String path = f.getParent();
		path = path.substring(0, path.length() - 1);
		doc.add(getField("absolutepath", path));

		doc.add(getField("name", f.getName()));

		doc.add(getField("type", mimetype));

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
							// log.debug(',');
							if (!(row[j].isHidden())) {
								list.add(row[j].getContents());
							}
						}
					}
				}
			}
		}
		doc.add(getField("filecontents", list.toString()));
		doc.add(getField("name", name));
		return doc;
	}



	public void CSV(Workbook w, String encoding, boolean hide) {
		if (encoding == null || !encoding.equals("UnicodeBig")) {
			encoding = "UTF8";
		}

		for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
			Sheet s = w.getSheet(sheet);

			if (!(hide && s.getSettings().isHidden())) {
				Cell[] row = null;

				for (int i = 0; i < s.getRows(); i++) {
					row = s.getRow(i);

					if (row.length > 0) {
						if (!(hide && row[0].isHidden())) {
//							log.debug(row[0].getContents());
							// Java 1.4 code to handle embedded commas
							// bw.write("\"" +
							// row[0].getContents().replaceAll("\"","\"\"") +
							// "\"");
						}

						for (int j = 1; j < row.length; j++) {
							// log.debug(',');
							if (!(hide && row[j].isHidden())) {
							//	log.debug(row[j].getContents());
								// Java 1.4 code to handle embedded quotes
								// bw.write("\"" +
								// row[j].getContents().replaceAll("\"","\"\"")
								// + "\"");
							}
						}
					}
					// log.debug();
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

			log.debug(s.getName());

			Cell[] row = null;
			Cell c = null;

			for (int i = 0; i < s.getRows(); i++) {
				row = s.getRow(i);

				for (int j = 0; j < row.length; j++) {
					c = row[j];
					if (c.getCellFeatures() != null) {
						//CellFeatures features = c.getCellFeatures();
						StringBuffer sb = new StringBuffer();
						CellReferenceHelper.getCellReference(c.getColumn(), c.getRow(), sb);

						//log.debug("Cell " + sb.toString() + " contents:  " + c.getContents());

						//log.debug(" comment: " + features.getComment());
					}
				}
			}
		}
	}


    public String[] getSearchFields() {
       return fields;
    }
    private static Field getField(String name, String value) {
        return new Field(name, value.getBytes(), Field.Store.YES);
    }
}
