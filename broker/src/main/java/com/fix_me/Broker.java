package com.fix_me;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Broker 
{
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5000;
    private static int brokerCount = 0;
    private static int marketCount = 0;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            String id = generateID(serverPort);
            System.out.println("[BROKER " + id + "] connected to Router.");
//            startTransaction(s);
//            Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            File fix_message = new File("FIXMessage.txt");
            Scanner scan  = new Scanner(fix_message);
            Boolean exists = fix_message.exists();
            if (!exists){
                System.out.println("File does not exist, Creating file.");
                fix_message.createNewFile();
            }
//             String messageContent = null;
//             while(scan.hasNextLine())
//             {
//                 messageContent = scan.nextLine();
//                 System.out.println(messageContent);
//
//             }
//             String[] newString = messageContent.split(" ", 0);
//             System.out.println(newString[0]);
//             System.out.println(newString[1]);

            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            // ServerConnection serverConn = new ServerConnection(socket);
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            int count = 0;
            String command = null;

            // new Thread(serverConn).start();
            while (scan.hasNextLine()){
                    // System.out.println("> ");
//                    command = keyBoard.readLine();
                command = scan.nextLine();
                out.println(command);
                Sleep(4);
                System.out.println(command);
//                if (command.toLowerCase().equals("quit")){
//                    out.println(command);
//                        // TimeUnit.SECONDS.sleep(200);
//                    break ;
//                }
//                id = input.readLine();
//                System.out.println(id);
                // if (count == 0){
                //     System.out.println("Waiting for Market to come online.");
                //     count++;
                // }
                // while (serverResponse.equals(null)){
                    // if (serverResponse.equals("market is online"))
                    // break;
                // }
            }
//            System.out.println("Server Says: "+ id);
            scan.close();
                // write.println("Hello computer");
                s.close();
            // System.exit(0);
        } catch( IOException e){
            System.out.println("ERROR: "+e);
        } catch (Exception e) {
            System.out.println("ERROR: "+e);
            //TODO: handle exception
        }
    }

//    public static void startTransaction(Socket s) {
//        ArrayList<String> FIXMessages = readFIXMsgs();
//        MessageHandler check;
//        for (int i = 0; i < FIXMessages.size(); i++) {
//            check = new MessageHandler(FIXMessages.get(i));
//            printMsg(FIXMessages.get(i) + "|10=" + check.CalculateChecksum(FIXMessages.get(i)));
//            Sleep(4);
//        }
//    }

    private static ArrayList<String> readFIXMsgs() {
        ArrayList<String> FIXMsgs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader((new FileReader("FIXMessage.txt")))) {
            while (br.ready()) {
                FIXMsgs.add((br.readLine()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FIXMsgs;
    }

    public static void printMsg(String message) {
        System.out.println("[BROKER] " + message);
    }

    private static void Sleep(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
//		 addTORoutingTable(uniqueId, channel);
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
