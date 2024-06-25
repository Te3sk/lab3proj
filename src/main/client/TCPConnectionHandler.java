package main.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPConnectionHandler {
    private Socket socket; // socket for the connection
    private PrintWriter out;  // writer for the socket (send data to server)
    private BufferedReader in; // reader for the socket (recieve data from server)

    /**
     * start the TCP connection with the server
     *  
     * @param serverAddress 
     * @param serverPort
     */
    public void connect(String serverAddress, int serverPort) {
        try {
            // create the socket with address and port
            socket = new Socket(serverAddress, serverPort);

            // PrintWriter initialization
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // BufferedReader initialization
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // print the connection information
            System.out.println("Connected to the server: " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
    
    /**
     * send a request to the server
     * 
     * @param request a string with the request to send
     */
    public void sendRequest(String request) {
        // check if the PrintWriter is not null
        if (out != null) {
            out.println(request); // send request to the server
            System.out.println("Request sent: " + request);
        } else {
            System.out.println("Error: PrintWriter is null");
        }
    }

    /**
     * recieve a response from the server
     * 
     * @return a string with the response from the server
     */
    String recieveResponse(){
        String response = null;

        try {
            // check if the BufferedReader is not null
            if(in != null){
                response = in.readLine(); // read the response from the server
                System.out.println("Response received: " + response);
            } else {
                System.out.println("Error: BufferedReader is null");
            }
        } catch (IOException e) {
            System.out.println("Error reading response from server: " + e.getMessage());
        }

        return response;
    }

    /**
     * disconnect from the server
     */
    void disconnect(){
        try {
            // check if the socket is not null
            if (socket != null) {
                socket.close(); // close the socket
                System.out.println("Disconnected from the server");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        }
    }
}
