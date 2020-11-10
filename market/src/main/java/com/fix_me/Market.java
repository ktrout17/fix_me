package com.fix_me;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Market {
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5001;
    private static int brokerCount = 0;
    private static int marketCount = 0;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            String id = generateID(serverPort);
            System.out.println("[MARKET " + id + "] connected to Router.");
            // Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            // BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            // Boolean ifTrue = false;

            // String command = "market";
            while (true){

                // System.out.println("> ");
                String command = keyBoard.readLine();

                if (command.toLowerCase().equals("quit")) {
					break ;
				}
				out.println(command);

                // if (command.equals("market")){
                //     out.println(command);
                //     command = null;
                // }
                
                // String serverResponse = input.readLine();
                // "Mouse", "KeyBoard", "Tv", "Computer Screen"
                // System.out.println("Server Says: "+serverResponse);
                // if (!serverResponse.equals("Are you the broker or market?") || serverResponse.equals(null)){

                //     System.out.println("Server Says: "+serverResponse);
                //     if (serverResponse.toLowerCase().equals("mouse")){
                //         System.out.println("Accept");
                //         command = "Accept";
                //         out.println(command);
                //         serverResponse = null;
                //     }else if (serverResponse.toLowerCase().equals("keyboard")){
                //         System.out.println("Accept");
                //         command = "Accept";
                //         out.println(command);
                //         serverResponse = null;
                //     }else if (serverResponse.toLowerCase().equals("tv")){
                //         System.out.println("Decline");
                //         command = "Decline";
                //         out.println(command);
                //         serverResponse = null;
                //     }else if (serverResponse.toLowerCase().equals("computer screen")){
                //         System.out.println("Decline");
                //         command = "Decline";
                //         out.println(command);
                //         serverResponse = null;
                //     }else if (serverResponse.toLowerCase().equals("quit")){
                //         break;
                //     }else{
                //         // System.out.println("Decline"); 
                //         command = "Decline";
                //         out.println(command);
                //         serverResponse = null;
                //     }
                // }
                // JOptionPane.showMessageDialog( null, serverResponse);
            }
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
