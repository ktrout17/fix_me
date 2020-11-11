package com.fix_me;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Market {
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5001;
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static Socket socket = null;

    private static int brokerCount = 0;
    private static int marketCount = 0;

    public static void main( String[] args )
    {
        initializeConnections();
        String msg = null;

        try {
            while (true){
                msg = in.readLine();
                System.out.println("[MARKET] Request from Broker: " + msg);

                if (msg.toLowerCase().equals("quit")) {
					break ;
				}
//				out.println(msg);
                msg = null;


            }
            closeConnections();
        } catch( IOException e){
            System.err.println("Failed to read input stream");
            closeConnections();
            System.exit(1);
        }
    }

    private static void initializeConnections() {
        try {
            socket = new Socket(serverIp, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String id = generateID(serverPort);
            System.out.println("[MARKET " + id + "] connected to Router.");
        } catch (IOException e) {
            System.err.println("Failed to create connection");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public static String generateID(int port) {
        StringBuilder id = new StringBuilder();
        String uniqueId = null;

        switch (port) {
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
        return uniqueId;
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

    private static void closeConnections() {
        try {
            socket.close();
            System.out.println("Market connection Closing...");
        } catch (IOException e) {
            System.err.println("Failed to close connections");
            System.exit(1);
        }
    }
}
