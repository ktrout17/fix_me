package com.fix_me;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.NullPointerException;
import java.util.concurrent.TimeUnit;

import lombok.val;

public class ClientHandler implements Runnable {
	public static final String MARKET = "\u001B[93m";
	public static final String BROKER = "\u001B[96m";
	public static final String RESET  = "\u001B[0m";

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
		out.println(id);
	}
	
	public void run() {
		try {
			while (true) {
				String message = in.readLine();
				if (message.contains("39=") && !message.equals(null)) {
					handleResponse(message);
				} else if (!message.equals(null)) {
					handleRequest(message);
				}
			}
		} catch (NullPointerException e) {
			closeConnections();
		} catch (IOException e) {
			closeConnections();
		}
	}

	private void handleRequest(String request) {
		BrokerMessageHandler check = new BrokerMessageHandler(request);
		String idM = check.getRouterReceiverID();

		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(idM) && check.validateChecksum())
				value.getValue().out.println(request);
		}
		System.out.println(BROKER + "[ROUTER] Sending to " + getName(check.getRouterReceiverID()) + ": " + request + RESET);
	}

	private void handleResponse(String response) {
		MarketMessageHandler check = new MarketMessageHandler(response);
		String idB = check.getRouterRecieverId();

		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(idB) && check.validateChecksum()) {
				value.getValue().out.println(response);
			}
		}
		System.out.println(MARKET + "[ROUTER] Sending to " + getName(check.getRouterRecieverId()) + ": " + response + RESET);
	}

	private void closeConnections() {
		try {
			Iterator<Map.Entry<String, ClientHandler>> iterator = clients.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, ClientHandler> entry = iterator.next();
				if (entry.getKey().equals(id)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR: Removing client from routing table.");
			// System.out.println("StackTrace: " + e.getStackTrace());
			// System.out.println("LocalizedMessage: " + e.getLocalizedMessage());
			// System.out.println("Message" + e.getMessage());
		}
		try {
			in.close();
			out.close();
			client.close();
			System.out.println("[ROUTER] " + getName(id) + " " + id + " disconnecting...");
			Sleep(1);
			System.out.println("[ROUTER] " + getName(id) + " " + id + " has disconnected.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}

	private static void Sleep(long time) {
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String getName(String id) {
		if (id.charAt(0) == 'B') {
			return "Broker";
		} else if (id.charAt(0) == 'M') {
			return "Market";
		} else 
			return null;
	}
}