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
				if (!request.equals(null)) {
//					request = "49=" + brokerId + "|56=" + marketId + request;
					handleRequest(request);
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
		String id = check.getMarket();

		Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
		for (Map.Entry<String, ClientHandler> value : values) {
			if (value.getKey().equals(id) && validateMsg(request))
				value.getValue().out.println(request);
		}
		System.out.println("[ROUTER] Received from Broker: " + request);
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