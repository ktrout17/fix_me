package com.fix_me;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Broker {
	public static final String MARKET = "\u001B[93m";
	public static final String BROKER = "\u001B[96m";
	public static final String ACCEPT = "\u001B[32m";
	public static final String REJECT = "\u001B[31m";
	public static final String RESET  = "\u001B[0m";

	private static final String serverIp = "127.0.0.1";
	private static final int serverPort = 5000;
	private static BufferedReader in = null;
	private static PrintWriter out = null;
	private static Socket socket = null;
	private static Scanner scan = null;
	private static String brokerId;

	public static void main(String[] args) {
		initializeConnections();
		scan = readFile();
		if (scan == null) {
			System.err.println("Was unable to read file.");
			System.exit(1);
		}
		String msg = null;
		while (true) {
			if (!scan.hasNextLine()) {
				break;
			}
			msg = scan.nextLine();
			String fullRequest = constructFIXmsg(msg);
			out.println(fullRequest);
			Sleep(1);
			System.out.println(BROKER + "[BROKER " + brokerId + "] request to Market: " + fullRequest + RESET);
			Sleep(2);
			receiveResponses();
		}
		closeConnections();
	}

	private static void initializeConnections() {
		try {
			socket = new Socket(serverIp, serverPort);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			brokerId = in.readLine();
			System.out.println(BROKER + "[BROKER " + brokerId + "] connected to Router." + RESET);
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
			System.out.println("[BROKER " + brokerId + "] disconnecting..");
			Sleep(1);
			System.out.println("[BROKER " + brokerId + "] disconnected.");
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}

	private static Scanner readFile() {
		try {
			File fix_message = new File("FIXMessage.txt");
			Boolean exists = fix_message.exists();
			if (!exists) {
				System.out.println("File does not exist - Creating file.");
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

	private static String addChecksum(String message) {
		BrokerMessageHandler check = new BrokerMessageHandler(message);
		String checksum = check.CalculateChecksum(message);
		return checksum;
	}

	private static String constructFIXmsg(String message) {
		String marketId = "|56=M00001";
		String brokerString = "49=" + brokerId;
		String fullMessage = brokerString + marketId + message;
		String checksum = addChecksum(fullMessage);
		String FIXmsg = fullMessage + "10=" + checksum + "|";
		return FIXmsg;
	}

	private static void receiveResponses() {
		try {
			String response = in.readLine();
			if (!response.equals(null)) {
				System.out.println(MARKET + "[BROKER " + brokerId + "] response from Market " + response + RESET);
				if (response.contains("39=2"))
					System.out.println(ACCEPT + "[BROKER " + brokerId + "] order has been ACCEPTED." + RESET);
				if (response.contains("39=8"))
					System.out.println(REJECT + "[BROKER " + brokerId + "] order has been REJECTED." + RESET);
				Sleep(1);
			}
		} catch (IOException e) {
			System.err.println("Failed to read input stream");
			e.printStackTrace();
			closeConnections();
			System.exit(1);
		}
	}
}
