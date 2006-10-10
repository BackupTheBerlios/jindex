package org.jindex.daemon.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.jindex.daemon.Watch;

public class ConfigReader {
	static final Logger log = Logger.getLogger(ConfigReader.class);
	static ArrayList list;
	public static ArrayList getWatches() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("watch", Watch.class);
		try {
			list = (ArrayList) xstream.fromXML(new FileReader("config.xml"));
			Iterator ite = list.iterator();
			while(ite.hasNext()) {
				log.debug(ite.next().toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
}
