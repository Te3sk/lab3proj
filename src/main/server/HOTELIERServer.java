package main.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class HOTELIERServer {
    private int port;
    private boolean isRunning;
    private ServerSocket serverSocket;

    /**
     * HOTELIERServer class constructor
     * 
     * @param port specifies which port the server should listen on
     */
    public HOTELIERServer(int port){
        this.port = port;
        this.isRunning = false;
    }

    /**
     * start the server
     */
    void start() {
        // todo - HOTELIERServer.start
        if (isRunning) {
            System.out.println("the server il already started");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on the port " + port + ".");
            isRunning = true;

            // server is listening for incoming connection
            while (isRunning) {
                // accept the client connection
                Socket clientScSocket = serverSocket.accept();
                System.out.println("accepted connection from " + clientScSocket.getInetAddress());

                // start a thread to handle the connection with the client
            }
        }
    }

    /**
     * save user and hotel data in a JSON file
     */
    void saveData(){
        // todo - HOTELIERServer.saveData
    }
}
