package com.fix_me;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

import java.lang.NullPointerException;

public class ClientHandler implements Runnable {
	// private static String[] Products = { "Mouse", "KeyBoard", "Tv", "Computer Screen" };
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private Map<String, ClientHandler> clients;
	private String id = null;

	public ClientHandler(String id, Socket clientSocket, Map<String, ClientHandler> clients) {
		this.client = clientSocket;
		this.clients = clients;
		this.id = id;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Failed to Create input and output streams");
			System.exit(1);
		}
	}

	public void run() {
		try {
			while (true) {
				String request = in.readLine();
				handleRequest(request);
				if (!client.isConnected()) {
					System.out.println("[SERVER] "+id+": closed");
				}
				// if (request.contains("broker")) {
				// // out.println( Router.getRandomName() );
				// name = "broker";
				// if (market > 0) {
				// // out.println("market is online");
				// outToAll();
				// } else {
				// // out.println("market is offline");
				// }
				// } else if (request.contains("quit")) {
				// outToMarketQuit(market);
				// // quitCorrect(market);
				// } else if (request.contains("market")) {
				// name = "market";
				// out.println("you are the market");
				// market++;
				// } else if (request.contains("Accept")) {
				// outToBrokerAccept();
				// // outToAll();
				// } else if (request.contains("Decline")) {
				// outToBrokerDecline();
				// } else
				// out.println("Are you the broker or market?");
			}
		} catch (NullPointerException e) {
			closeConnections();
		} catch (IOException e) {
			closeConnections();
		}
	}

	private void handleRequest(String request) {
		// TODO: handle FIX message
		String id = "M00001";
		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();

		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(id)) {
				value.getValue().out.println(request);
			}
		}
	}

	private void closeConnections() {
		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(id)) {
				clients.remove(value.getKey());
			}
		}
		try {
			in.close();
			out.close();
			client.close();
			System.out.println("[SERVER] Connection Closing...");
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}

	// private void quit(int market) {
	// for (ClientHandler aClient : clients) {
	// if (aClient.name == "market")
	// market--;
	// }
	// }

	// private void outToAll() {
	// for (ClientHandler aClient : clients) {
	// if (aClient.name == "market")
	// aClient.out.println(getRandomName());
	// }
	// }

	// private void outToMarketQuit(int market) {
	// for (ClientHandler aClient : clients) {
	// if (aClient.name == "market")
	// aClient.out.println("quit");
	// market--;
	// }
	// }

	// private void outToBrokerAccept() {
	// for (ClientHandler aClient : clients) {
	// if (aClient.name == "broker")
	// aClient.out.println("Accepted");
	// }
	// }

	// private void outToBrokerDecline() {
	// for (ClientHandler aClient : clients) {
	// if (aClient.name == "broker")
	// aClient.out.println("Decline");
	// }
	// }

	// public static String getRandomName() {
	// Random random = new Random();
	// int count = Products.length;
	// count--;
	// int randomNumber = random.nextInt(count);
	// // System.out.println(randomNumber);
	// String randomName = Products[randomNumber];
	// return randomName;
	// }
}