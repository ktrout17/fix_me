package com.fix_me;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Router {
	private static final int PORT = 5000;
	private static final int PORTM = 5001;
	private static ServerSocket listener = null;
	private static ServerSocket listenerM = null;
	// private static ArrayList<ClientHandler> clients = new ArrayList<>();
	private static Map<String, ClientHandler> clients = new HashMap<>();
	private static ExecutorService pool = Executors.newFixedThreadPool(4);

	private static int brokerCount = 0;
	private static int marketCount = 0;

	public static void main(String[] args) {
		try {
			listener = new ServerSocket(PORT);
			listenerM = new ServerSocket(PORTM);
			Socket client = null;
			Socket clientM = null;
			ClientHandler clientThread = null;
			while (true) {
				System.out.println("[SERVER] is waiting for client connection.");
				if (clientM == null) {
					System.out.println("Waiting for Market to connect...");
					clientM = listenerM.accept();
					System.out.println("[SERVER] connceted to market");
					String id = generateID(clientM);
					clientThread = new ClientHandler(clientM, clients);
					// clients.add(clientThread);
					addTORoutingTable(id, clientThread);
				} else {
					client = listener.accept();
					System.out.println("[SERVER] connceted to client");
					String id = generateID(client);
					clientThread = new ClientHandler(client, clients);
					// clients.add(clientThread);
					addTORoutingTable(id, clientThread);
				}
				pool.execute(clientThread);
			}

		} catch (IOException e) {
			System.out.println("ERROR: IOException");
		} catch (Exception e) {
			System.out.println("ERROR: ");
			// TODO: handle exception
		}
	}

	private static String generateID(Socket channel) {
		StringBuilder id = new StringBuilder("");
		String uniqueId = null;

		switch (channel.getLocalPort()) {
			case 5000:
				id.append("B");
				uniqueId = numberPadding(id, nextBrokerId());
				break;
			case 5001:
				id.append("M");
				uniqueId = numberPadding(id, nextMarketId());
			default:
				break;
		}

		if (!clients.isEmpty()) {
			Set<Map.Entry<String, ClientHandler>> values = clients.entrySet();
			for (Map.Entry<String, ClientHandler> value : values) {
				if (uniqueId.equals(value.getKey())) {
					generateID(channel);
				}
			}
		}
		return uniqueId;
		// addTORoutingTable(uniqueId, channel);
	}

	private static void addTORoutingTable(String id, ClientHandler channel) {
		// TODO: change back to id, not port
		clients.put(id, channel);
	}

	private static String numberPadding(StringBuilder id, int count) {
		while (id.length() + Integer.toString(count).length() < 6) {
			id.append("0");
		}
		id.append(Integer.toString(count));
		return id.toString();
	}

	private static int nextBrokerId() {
		return (++brokerCount);
	}

	private static int nextMarketId() {
		return (++marketCount);
	}
}
