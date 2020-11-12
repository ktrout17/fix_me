package com.fix_me;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Market {
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5001;
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static Socket socket = null;
    private static Scanner scan = null;
    private static String responses = null;
    private static String marketId;
    private static String brokerId;

    private static int brokerCount = 0;
    private static int marketCount = 0;

    public static void main( String[] args )
    {
        initializeConnections();

        String msg = null;
        int count = 0;
        try {
            while (true){
                msg = in.readLine();

                if (count > 0 && !msg.equals(null)) {
                    System.out.println("[MARKET " + marketId + "] Request from Broker: " + msg);
                    getBrokerId(msg);
                    sendResponses();
                }
                else
                    count++;
            }
//            closeConnections();
        } catch (NullPointerException e) {
            System.err.println("Lost connection to server.");
            e.printStackTrace();
            closeConnections();
        } catch( IOException e){
            System.err.println("Failed to read input stream");
            e.printStackTrace();
            closeConnections();
            System.exit(1);
        }
    }

    private static void initializeConnections() {
        try {
            socket = new Socket(serverIp, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            marketId = generateID(serverPort);
            System.out.println("[MARKET " + marketId + "] connected to Router.");
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

    private static Scanner readFile() {
        try {
            File fix_message = new File("MarketResponses.txt");
            Boolean exists = fix_message.exists();
            if (!exists) {
                System.out.println("File does not exist - Creating file.");
                fix_message.createNewFile();
            }
            Scanner scan = new Scanner(fix_message);
            return scan;
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to create file.");
            System.exit(1);
        }
        return null;
    }

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponses() {
        scan = readFile();
        if (scan == null) {
            System.err.println("Was unable to read file.");
            System.exit(1);
        }
        while (true) {
            if (!scan.hasNextLine()) {
                break;
            }
            Sleep(2);
            responses = scan.nextLine();
            String FIXMsg = constructFIXmsg(responses);
//            String status = getStatus(FIXMsg);
            out.println(FIXMsg);
            System.out.println("[MARKET " + marketId + "] responding to Broker: " + FIXMsg);
        }
    }

    private static void getBrokerId(String request) {

        BrokerMessageHandler check = new BrokerMessageHandler(request);
        brokerId = check.getRouterSenderID();
    }

    private static String getChecksum(String response) {
        BrokerMessageHandler check = new BrokerMessageHandler(response);
        String checksum = check.CalculateChecksum(response);
        return checksum;
    }

    private static String getStatus(String response) {
        BrokerMessageHandler check = new BrokerMessageHandler(response);
        String status = check.getStatus();
        return status;
    }

    private static String constructFIXmsg(String responses) {
        String fullResponse = "56=" + brokerId + "|49=" + marketId + responses;
        String checksum = getChecksum(fullResponse);
        String FIXMsg = fullResponse + "10=" + checksum + "|";
        return FIXMsg;
    }

    private static void closeConnections() {
        try {
            socket.close();
            System.out.println("[MARKET " + marketId + "] connection Closing...");
        } catch (IOException e) {
            System.err.println("Failed to close connections");
            System.exit(1);
        }
    }
}
