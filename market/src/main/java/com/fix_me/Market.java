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
    private static final int serverPort = 5000;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            // Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            while (true){

                System.out.println("> ");
                String command = keyBoard.readLine();

                if (command.toLowerCase().equals("quit"))
                break ;


                out.println(command);
                
                String serverResponse = input.readLine();
                System.out.println("Server Says: "+serverResponse);
                if (serverResponse.toLowerCase().equals("hello world")){
                    System.out.println("Accept");
                    command = "Accept";
                    out.println(command);
                }else{
                    System.out.println("Decline"); 
                    command = "Decline";
                    out.println(command);
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
