package com.fix_me;

import java.io.IOException;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import javax.swing.JOptionPane;
// import java.io.
public class Market {
    private static final String serverIp = "192.168.0.25";
    private static final int serverPort = 5001;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            // Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            Boolean ifTrue = false;

            String command = "market";
            while (true){

                // System.out.println("> ");
                // String command = keyBoard.readLine();

                if (command.toLowerCase().equals("quit"))
                break ;

                if (command.equals("market")){
                    out.println(command);
                    command = null;
                }
                
                String serverResponse = input.readLine();
                // "Mouse", "KeyBoard", "Tv", "Computer Screen"
                // System.out.println("Server Says: "+serverResponse);
                if (!serverResponse.equals("Are you the broker or market?") || serverResponse.equals(null)){

                    System.out.println("Server Says: "+serverResponse);
                    if (serverResponse.toLowerCase().equals("mouse")){
                        System.out.println("Accept");
                        command = "Accept";
                        out.println(command);
                        serverResponse = null;
                    }else if (serverResponse.toLowerCase().equals("keyboard")){
                        System.out.println("Accept");
                        command = "Accept";
                        out.println(command);
                        serverResponse = null;
                    }else if (serverResponse.toLowerCase().equals("tv")){
                        System.out.println("Decline");
                        command = "Decline";
                        out.println(command);
                        serverResponse = null;
                    }else if (serverResponse.toLowerCase().equals("computer screen")){
                        System.out.println("Decline");
                        command = "Decline";
                        out.println(command);
                        serverResponse = null;
                    }else if (serverResponse.toLowerCase().equals("quit")){
                        break;
                    }else{
                        // System.out.println("Decline"); 
                        command = "Decline";
                        out.println(command);
                        serverResponse = null;
                    }
                }
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
}
