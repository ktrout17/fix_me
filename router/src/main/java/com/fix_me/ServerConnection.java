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
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.fix_me.*;

public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;
    public ServerConnection(Socket s) throws IOException{
        this.server = s;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }
    public void run(){
        String serverResponse = null;
        try {
                while (true){
                serverResponse = in.readLine();
                if (serverResponse == null)
                break ;
                System.out.println("Server Says: "+serverResponse);
                }
                
            } catch(IOException e){
                System.out.println("IOException"+ e);
            } catch (Exception e) {
                //TODO: handle exception
            } finally{
                try {
                    in.close();
                    
                } catch (IOException e) {
                    System.out.println("IOException"+ e);
                    //TODO: handle exception
                }
            }
    }
}