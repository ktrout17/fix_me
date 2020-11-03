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
public class Broker 
{
    private static final String serverIp = "192.168.0.25";
    private static final int serverPort = 5000;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            // Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

            // ServerConnection serverConn = new ServerConnection(socket);
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            int count = 0;
            String command = null;
            String serverResponse = null;
            // new Thread(serverConn).start();
            while (true){

                    System.out.println("> ");
                    command = keyBoard.readLine();
                    if (command.toLowerCase().equals("quit"))
                    break ;

                    out.println(command);

                serverResponse = input.readLine();
                while (serverResponse.equals(null)){
                }
                System.out.println("Server Says: "+serverResponse);
                serverResponse = null;
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
