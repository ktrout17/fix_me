package com.fix_me;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.lang.NullPointerException;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
//	private BufferedReader brokerIn;
//	private BufferedReader marketIn;
//	private PrintWriter brokerOut;
//	private PrintWriter marketOut;
	private String brokerId;
	private String marketId;
	private Map<String, ClientHandler> clients;
	private String id = null;
//	private Socket brokerSocket;
//	private Socket marketSocket;

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
				String message = in.readLine();
				if (!message.equals(null)) {
					handleRequest(message);
				}
				if (message.contains("39=") && !message.equals(null))
					handleResponse(message);
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
			if (value.getKey().equals(idM) && validateMsg(request))
				value.getValue().out.println(request);
		}
		System.out.println("[ROUTER] Received from Broker: " + request);
	}

	private void handleResponse(String response) {
		BrokerMessageHandler check = new BrokerMessageHandler(response);
		String idB = check.getRouterSenderID();

		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(idB) && validateMsg(response))
				value.getValue().out.println(response);
		}
		System.out.println("[ROUTER] Response from Market: " + response);
	}

	private Boolean validateMsg(String request) {
		MarketMessageHandler validate = new MarketMessageHandler(request);
		Boolean validateChecksum = validate.validateChecksum();

		return validateChecksum;
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
			System.out.println("[ROUTER] Broker " + brokerId + " disconnecting...");
			Sleep(1);
			System.out.println("[ROUTER] Broker " + brokerId + " has disconnected.");
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
}