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
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
import java.io.File;

public class Broker 
{
    private static final String serverIp = "127.0.0.1";
    private static final int serverPort = 5000;
    public static void main( String[] args )
    {
        try {
            Socket s = new Socket(serverIp, serverPort);
            // Scanner scan = new Scanner(new InputStreamReader(s.getInputStream()));
            File fix_message = new File("broker/src/main/java/com/fix_me/Fix_messages.txt");
            Boolean exists = fix_message.exists();
            if (!exists){
                System.out.println("file does not exists, Creating file.");
                fix_message.createNewFile();
            }
            String messageContent = null;
            // Scanner scan = new Scanner(fix_message);
            Scanner scan = new Scanner(fix_message);
            // while(scan.hasNextLine())
            // {
            //     messageContent = scan.nextLine();
            // }
            // System.out.println(messageContent);
            // String[] newString = messageContent.split(" ", 0);
            // System.out.println(newString[0]);
            // System.out.println(newString[1]);

            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            // ServerConnection serverConn = new ServerConnection(socket);
            BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            int count = 0;
            String command = null;
            String serverResponse = null;
            // new Thread(serverConn).start();
            while (true){
                    // System.out.println("> ");
                    // command = keyBoard.readLine();
                    command = scan.nextLine();
                    if (command.toLowerCase().equals("quit")){
                        out.println(command);
                        // TimeUnit.SECONDS.sleep(200);
                        break ;
                    }
                    out.println(command);
                if (count == 0){
                    System.out.println("Waiting for Market to come online.");
                    count++;
                }
                serverResponse = input.readLine();
                while (serverResponse.equals(null)){
                    // if (serverResponse.equals("market is online"))
                    // break;
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
