package com.fix_me;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Market {
	private static final String serverIp = "127.0.0.1";
	private static final int serverPort = 5001;
	private static BufferedReader in = null;
	private static PrintWriter out = null;
	private static Socket socket = null;

	public static void main(String[] args) {
		initilizeConnections();
		String msg = null;
		// BufferedReader keyBoard = new BufferedReader(new
		// InputStreamReader(System.in));
		try {
			while (true) {
				msg = in.readLine();
				System.out.println("> " + msg);
				
				// String command = keyBoard.readLine();
				
				if (msg.toLowerCase().equals("quit")) {
					break;
				}
				// out.println(command);
				msg = null;
			}
				// if (command.equals("market")){
				// out.println(command);
				// command = null;
				// }

				// String serverResponse = input.readLine();
				// "Mouse", "KeyBoard", "Tv", "Computer Screen"
				// System.out.println("Server Says: "+serverResponse);
				// if (!serverResponse.equals("Are you the broker or market?") ||
				// serverResponse.equals(null)){

				// System.out.println("Server Says: "+serverResponse);
				// if (serverResponse.toLowerCase().equals("mouse")){
				// System.out.println("Accept");
				// command = "Accept";
				// out.println(command);
				// serverResponse = null;
				// }else if (serverResponse.toLowerCase().equals("keyboard")){
				// System.out.println("Accept");
				// command = "Accept";
				// out.println(command);
				// serverResponse = null;
				// }else if (serverResponse.toLowerCase().equals("tv")){
				// System.out.println("Decline");
				// command = "Decline";
				// out.println(command);
				// serverResponse = null;
				// }else if (serverResponse.toLowerCase().equals("computer screen")){
				// System.out.println("Decline");
				// command = "Decline";
				// out.println(command);
				// serverResponse = null;
				// }else if (serverResponse.toLowerCase().equals("quit")){
				// break;
				// }else{
				// // System.out.println("Decline");
				// command = "Decline";
				// out.println(command);
				// serverResponse = null;
				// }
				// }
				// JOptionPane.showMessageDialog( null, serverResponse);
			// }
			closeConnections();
		} catch (IOException e) {
			System.err.println("Failed to read input stream");
			closeConnections();
			System.exit(1);
		}
	}

	private static void initilizeConnections() {
		try {
			socket = new Socket(serverIp, serverPort);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Failed to create connection");
			System.exit(1);
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	private static void closeConnections() {
		try {
			socket.close();
			System.out.println("Market connection Closing...");
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}
}
