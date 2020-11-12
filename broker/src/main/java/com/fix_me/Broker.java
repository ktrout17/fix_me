package com.fix_me;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Broker 
{
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5000;
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static Socket socket = null;
    private static Scanner scan = null;
    private static String brokerId;

    public static void main( String[] args )
    {
            initializeConnections();
            scan = readFile();
            if (scan == null) {
                System.err.println("Was unable to read file.");
                System.exit(1);
            }
            String msg = null;
            while (true) {
                if (!scan.hasNextLine()) {
                    break;
                }
                Sleep(2);
                msg = scan.nextLine();
                out.println(msg);
                String fullRequest = constructFIXmsg(msg);
                System.out.println("[BROKER] sending to Market: " + fullRequest);
            }

//            String responses = null;
//
//            try {
//                while (true) {
//                    responses = in.readLine();
//                    System.out.println("[BROKER] response from Market: " + responses);
//                }
//            } catch (NullPointerException e) {
//                System.err.println("Lost connection to server.");
//                closeConnections();
//            } catch( IOException e){
//                System.err.println("Failed to read input stream");
//                closeConnections();
//                System.exit(1);
//            }
            closeConnections();
    }

    private static void initializeConnections() {
        try {
            socket = new Socket(serverIp, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            brokerId = in.readLine();
            System.out.println("[BROKER " + brokerId + "] connected to Router.");
        } catch (IOException e) {
            System.err.println("Failed to create connection");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static void closeConnections() {
        try {
            scan.close();
            socket.close();
            System.out.println("Broker disconnecting..");
            Sleep(1);
            System.out.println("Broker disconnected.");
        } catch (IOException e) {
            System.err.println("Failed to close connections");
            System.exit(1);
        }
    }

    private static Scanner readFile() {
        try {
            File fix_message = new File("FIXMessage.txt");
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

    private static String addChecksum(String message) {
        BrokerMessageHandler check = new BrokerMessageHandler(message);
        String checksum = check.CalculateChecksum(message);
        return checksum;
    }

    private static String constructFIXmsg(String message) {
        String marketId = "|56=M00001";
        String brokerString = "49=" + brokerId;
        String fullMessage = brokerString + marketId + message;
        String checksum = addChecksum(fullMessage);
        String FIXmsg = fullMessage + "10=" + checksum;
        return FIXmsg;
    }
}
