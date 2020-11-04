package com.fix_me;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.ArrayList;
// import java.util.List;
import java.util.Random;
import com.fix_me.*;
import java.lang.NullPointerException;

public class ClientHandler implements Runnable {
    private static String[] Products = {"Mouse", "KeyBoard", "Tv", "Computer Screen"};
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String name = null;
    private ArrayList<ClientHandler> clients;
    private static int broker = 0;
    private static int market = 0;
    
    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException{
            this.client = clientSocket;
            this.clients = clients;
            this.name = name;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
    }
    public BufferedReader getIn(){
        return this.in;
    }

    public void run(){
        try{

            while (true){
                String request = in.readLine();
                if (request.contains("broker")){
                    // out.println( Router.getRandomName() );
                    name = "broker";
                    if (market > 0){
                        // out.println("market is online");
                        outToAll();
                    }else{
                        // out.println("market is offline");
                    }
                }else if (request.contains("quit")){
                    outToMarketQuit(market);
                    // quitCorrect(market);
                }else if (request.contains("market")){
                    name = "market";
                    out.println("you are the market");
                    market++;
                }else if (request.contains("Accept")){
                    outToBrokerAccept();
                    // outToAll();
                }
                else if (request.contains("Decline")){
                    outToBrokerDecline();
                }
                else
                out.println("Are you the broker or market?");
            }
        } catch(NullPointerException e){
            System.out.println("ERROR: Closed connection with out useing quit.");
            quit(market);
        } catch(IOException e){
            System.out.println("ERROR: "+e);
        } finally{
            out.close();
            try {
                in.close();
                
            } catch( IOException e){
                System.out.println("ERROR: "+e);
            } catch (Exception e) {
                //TODO: handle exception
            }
            // System.out.println("[SERVER] sent data, closeing.");
            // client.close();
            // listener.close();
        }
    }

    private void quit(int market){
        for ( ClientHandler aClient: clients){
            if (aClient.name == "market")
            market--;
        }
    }

    private void outToAll(){
        for ( ClientHandler aClient: clients){
            if (aClient.name == "market")
            aClient.out.println(getRandomName());
        }
    }

    private void outToMarketQuit(int market){
        for ( ClientHandler aClient: clients){
            if (aClient.name == "market")
            aClient.out.println("quit");
            market--;
        }
    }

    private void outToBrokerAccept(){
        for ( ClientHandler aClient: clients){
            if (aClient.name == "broker")
            aClient.out.println("Accepted");
        }
    }
    private void outToBrokerDecline(){
        for ( ClientHandler aClient: clients){
            if (aClient.name == "broker")
            aClient.out.println("Decline");
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