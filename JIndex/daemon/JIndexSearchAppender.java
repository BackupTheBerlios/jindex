/*
 * Created on Sep 21, 2005
 */
package daemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JIndexSearchAppender extends Thread {
    private Socket socket = null;

    public JIndexSearchAppender(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
            		//System.out.println("Trying to add file '"+inputLine+"' to appendQueue");	
                JIndexDaemon.appendToQueue(inputLine);
            }
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
