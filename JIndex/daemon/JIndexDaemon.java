package daemon;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/*
 * Created on Sep 21, 2005
 */

public class JIndexDaemon {
    static List filequeue = new LinkedList();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        // Automaticly updates the index, every 5 seconds it scans the queue for files    
        new IndexFiles().start();
        
        try {
            serverSocket = new ServerSocket(44441);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            e.printStackTrace();
            System.exit(-1);
        }

        while (listening)
            new JIndexSearchAppender(serverSocket.accept()).start();

        serverSocket.close();
    }

    public static synchronized void appendToQueue(String inputLine) {
    		File appendfile = new File(inputLine);
    		Iterator ite = filequeue.iterator();
    		boolean added = false;
    		while(ite.hasNext()) {
    			File file = (File) ite.next();
    			if(file.getAbsoluteFile().equals(appendfile.getAbsoluteFile()))
    					added = true;
    		}
        if(!added) {
        		System.out.println("Adding file '"+inputLine+"' to appendQueue");
        		filequeue.add(appendfile);
        }
    }

    public static synchronized List getFileFromQueue() {
        List value = new LinkedList();
        value.addAll(filequeue);
        for (int i = 0; i < value.size(); i++) {
			System.out.println("Processing file " + value.get(i));
		}
        
        filequeue.clear();
        return value;
    }
}
