/*
 * Created on Sep 21, 2005
 */
package daemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileChangedNotifier {
	public static void main(String[] args) throws IOException {

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket("localhost", 44441);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to JIndexDaemon");
			System.exit(1);
		}
		out.println(args[0]);
		out.close();
		in.close();
		echoSocket.close();
	}
}
