package documents;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellReferenceHelper;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "contents", "name" };

	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();
//		CSV(f, doc);
		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));
//
		doc.add(Field.Keyword("name", f.getName()));
//
		doc.add(Field.Text("type", "application/msword"));
//		doc.add(Field.Text("icon", "icon data"));
//		doc.add(Field.Text("url", "url data"));
//		doc.add(Field.Text("from", ""));
//		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
//
//		FileInputStream is = new FileInputStream(f);
//		Reader reader = new BufferedReader(new InputStreamReader(is));
//		doc.add(Field.Text("contents", reader));

		try {
			if(f.getName().endsWith(".xls"))
				return CSV(f, doc);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
		return doc;
	}

	
	private static Document CSV(File f, Document doc) throws BiffException, IOException {
		String encoding = "UTF8";
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
		doc.add(Field.Text("contents", list.toString()));
		doc.add(Field.Keyword("name", name));
		return doc;
	}


	public ExcelDocument() {
		try {

			try {
				File file = new File("/home/sorenm/Documents/masterproduct.xls");
				
				Magic parser = new Magic() ;
//				 getMagicMatch accepts Files or byte[], 
//				 which is nice if you want to test streams
				MagicMatch match = null;
				try {
					match = parser.getMagicMatch(file);
				} catch (MagicParseException e) {
					e.printStackTrace();
				} catch (MagicMatchNotFoundException e) {
					e.printStackTrace();
				} catch (MagicException e) {
					e.printStackTrace();
				}
				System.out.println(match.getMimeType()) ;
				Workbook w = Workbook.getWorkbook(file);
				CSV(w, "afe", true);
				features(w, "UTF-8");
			} catch (BiffException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		
		new ExcelDocument();
	}

	public void CSV(Workbook w, String encoding, boolean hide) throws IOException {
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

	public void features(Workbook w, String encoding) throws IOException {
		if (encoding == null || !encoding.equals("UnicodeBig")) {
			encoding = "UTF8";
		}

		ArrayList parseErrors = new ArrayList();

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
