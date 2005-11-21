package daemon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ConfigReader {
	static ArrayList list;
	public static ArrayList getWatches() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("watch", Watch.class);
		try {
			list = (ArrayList) xstream.fromXML(new FileReader("config.xml"));
			Iterator ite = list.iterator();
			while(ite.hasNext()) {
				System.out.println(ite.next().toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
}
