package com.fix_me;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.lang.NullPointerException;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private String brokerId;
	private String marketId;
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
		brokerId = Router.brokerId;
		marketId = Router.marketId;
		try {
			while (true) {
				out.println(brokerId);
				String request = in.readLine();
				request = "49=" + brokerId + "|56=" + marketId + request;
				handleRequest(request);
				if (!client.isConnected()) {
					System.out.println("[ROUTER] "+id+": closed");
				}

			}
		} catch (NullPointerException e) {
			closeConnections();
		} catch (IOException e) {
			closeConnections();
		}
	}

	private void handleRequest(String request) {

		MessageHandler check = null;
		check = new MessageHandler(request);
		String checksum = check.CalculateChecksum(request);
		String FIXMsg = request + "10=" + checksum + "|";
		String id = check.getMarket();

		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(id)) {
				value.getValue().out.println(FIXMsg);
			}
		}
		System.out.println("[ROUTER] Received from Broker: " + FIXMsg);
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
}