package com.fix_me;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
			System.out.println("[SERVER] is waiting for client connection.");
			while (true) {
				if (clientM == null) {
					System.out.println("Waiting for Market to connect...");
					clientM = listenerM.accept();
					String id = generateID(clientM);
					System.out.println("[SERVER] connceted to market: "+id);
					clientThread = new ClientHandler(id, clientM, clients);
					addTORoutingTable(id, clientThread);
				} else {
					client = listener.accept();
					String id = generateID(client);
					System.out.println("[SERVER] connceted to broker: " + id);
					clientThread = new ClientHandler(id, client, clients);
					addTORoutingTable(id, clientThread);
				}
				pool.execute(clientThread);
			}

		} catch (IOException e) {
			System.out.println("ERROR: in Router.main()1");
		} catch (Exception e) {
			System.out.println("ERROR: in Router.main()");
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
	}

	private static void addTORoutingTable(String id, ClientHandler channel) {
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
