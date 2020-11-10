package com.fix_me;

import java.io.DataInputStream;
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
	private static final Map<String, ClientHandler> clients = new HashMap<>();
	private static final ExecutorService pool = Executors.newFixedThreadPool(4);

	private static int brokerCount = 0;
	private static int marketCount = 0;

	public static String brokerId;
	public static String marketId;

	public static void main(String[] args) {
		try {
			listener = new ServerSocket(PORT);
			listenerM = new ServerSocket(PORTM);
			Socket client = null;
			Socket clientM = null;
			ClientHandler clientThread = null;
			System.out.println("[ROUTER] is running.");
			while (true) {
				if (clientM == null) {
					System.out.println("[ROUTER] Waiting for Market to connect...");
					clientM = listenerM.accept();
					marketId = generateID(clientM);
					System.out.println("[ROUTER] new Market (" + marketId + ") connected.");
					clientThread = new ClientHandler(clientM, clients);
					addTORoutingTable(marketId, clientThread);
//					System.out.println("Map: " + clients);
				} else {
					System.out.println("[ROUTER] Waiting for Broker to connect...");
					client = listener.accept();
					brokerId = generateID(client);
					System.out.println("[ROUTER] new Broker (" + brokerId + ") connected.");
					clientThread = new ClientHandler(client, clients);
					addTORoutingTable(brokerId, clientThread);
//					System.out.println("Map: " + clients);
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

	public static String generateID(Socket channel) {
		StringBuilder id = new StringBuilder();
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
//		 addTORoutingTable(uniqueId, channel);
	}

	private static void addTORoutingTable(String id, ClientHandler channel) {
		// TODO: change back to id, not port
		clients.put(id, channel);
	}

	public static String numberPadding(StringBuilder id, int count) {
		while (id.length() + Integer.toString(count).length() < 6) {
			id.append("0");
		}
		id.append(count);
		return id.toString();
	}

	public static int nextBrokerId() {
		return (++brokerCount);
	}

	public static int nextMarketId() {
		return (++marketCount);
	}
}
