package daemon;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
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
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(-1);
        }

        while (listening)
            new JIndexSearchAppender(serverSocket.accept()).start();

        serverSocket.close();
    }

    public static void appendToQueue(String inputLine) {
        filequeue.add(new File(inputLine));
    }

    public static synchronized List getFileFromQueue() {
        List value = new LinkedList();
        value.addAll(filequeue);
        filequeue.clear();
        return value;
    }
}
