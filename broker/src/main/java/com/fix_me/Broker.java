package com.fix_me;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

public class Broker {
	private static final String serverIp = "127.0.0.1";
	private static final int serverPort = 5000;
	private static MessageHandler messageHandler = null;
	private static BufferedReader in = null;
	private static PrintWriter out = null;
	private static Socket socket = null;
	private static Scanner scan = null;

	public static void main(String[] args) {
		initilizeConnections();
		scan = readFile();
		if (scan == null) {
			System.err.println("Was unable to read file.");
			System.exit(1);
		}
		String msg = null;
		// BufferedReader keyBoard = new BufferedReader(new
		// InputStreamReader(System.in));
		// String serverResponse = null;
		// new Thread(serverConn).start();
		while (true) {
			if (!scan.hasNextLine()) {
				out.println("quit");
				break;
			}
			// Sleep(2);
			msg = scan.nextLine();
			out.println(msg);
		}
		// msg = keyBoard.readLine();
		// messageHandler = new MessageHandler(msg);
		// out.println(messageHandler);
		// if (command.toLowerCase().equals("quit")){
		// out.println(command);
		// TimeUnit.SECONDS.sleep(200);
		// break ;
		// }
		// out.println(command);
		// if (count == 0){
		// System.out.println("Waiting for Market to come online.");
		// count++;
		// }
		// serverResponse = input.readLine();
		// while (serverResponse.equals(null)){
		// if (serverResponse.equals("market is online"))
		// break;
		// }
		// System.out.println("Server Says: "+serverResponse);
		// serverResponse = null;
		// }
		// write.println("Hello computer");
		closeConnections();
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
			scan.close();
			socket.close();
			System.out.println("Broker connection Closing...");
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}

	private static Scanner readFile() {
		try {
			File fix_message = new File("broker/src/main/java/com/fix_me/Fix_messages.txt");
			Boolean exists = fix_message.exists();
			if (!exists) {
				System.out.println("file does not exists, Creating file.");
				fix_message.createNewFile();
			}
			Scanner scan = new Scanner(fix_message);
			return scan;
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Failed to create file.");
			System.exit(1);
		}
		return null;
	}

	private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
