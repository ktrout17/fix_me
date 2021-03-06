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
	public static final String MARKET = "\u001B[93m";
	public static final String BROKER   = "\u001B[96m";
	public static final String RESET  = "\u001B[0m";

	private static final int PORT = 5000;
	private static final int PORTM = 5001;
	private static ServerSocket listener = null;
	private static ServerSocket listenerM = null;
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
					System.out.println(MARKET + "[ROUTER] New Market " + marketId + " connected." + RESET);
					clientThread = new ClientHandler(marketId, clientM, clients);
					addTORoutingTable(marketId, clientThread);
					System.out.println("[ROUTER] Waiting for a Broker to connect...");
				} else {
					client = listener.accept();
					brokerId = generateID(client);
					System.out.println(BROKER + "[ROUTER] New Broker " + brokerId + " connected." + RESET);
					clientThread = new ClientHandler(brokerId, client, clients);
					addTORoutingTable(brokerId, clientThread);
				}
				pool.execute(clientThread);
			}
		} catch (IOException e) {
			System.err.println("ERROR: IOException");
		} catch (Exception e) {
			System.err.println("ERROR: Exception");
			e.printStackTrace();
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
	}

	private static void addTORoutingTable(String id, ClientHandler channel) {
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
