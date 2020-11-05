package com.fix_me;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.fix_me.*;

import java.util.Date;

public class Router 
{
    private static String[] Products = {"Mouse", "KeyBoard", "Tv", "Computer Screen"};
    private static final int PORT = 5000;
    private static final int PORTM = 5001;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    public static void main( String[] args)
    {
        try {
            ServerSocket listener = new ServerSocket(PORT);
            ServerSocket listenerM = new ServerSocket(PORTM);
            int count = 0;
            Socket client = null;
            Socket clientM = null;
            ClientHandler clientThread = null;
                while(true){
                    System.out.println("[SERVER] is waiting for client connection.");
                    if (clientM == null) {
                        System.out.println("Waiting for Market to connect...");
                        clientM = listenerM.accept();
                        System.out.println("[SERVER] connceted to market");
                        clientThread = new ClientHandler(clientM, clients);
                        clients.add(clientThread);
					}else{

                        client = listener.accept();
                        System.out.println("[SERVER] connceted to client");
                        clientThread = new ClientHandler(client, clients);
                        clients.add(clientThread);
                    }
                    // System.out.println("[SERVER] connceted to client");
                    // clientThread = new ClientHandler(client, clients);
                    // clients.add(clientThread);
                    pool.execute(clientThread);
                }
            // PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            // BufferedReader in = new BufferedReader( new InputStreamReader(client.getInputStream()));
            
        } catch(IOException e){
            System.out.println("ERROR: IOException");
        } catch (Exception e) {
            System.out.println("ERROR: ");
            //TODO: handle exception
        }
    }
    public static String getRandomName(){
        Random random = new Random();
        int count = Products.length;
        count--;
        int randomNumber = random.nextInt(count);
        // System.out.println(randomNumber);
        String randomName = Products[randomNumber];
        return randomName;
    }
}
