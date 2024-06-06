// compile: javac -d bin -cp lib/*.jar src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java
// run: java -cp "bin;lib/*" main.server.HOTELIERServer
// compile and run: javac -d bin -cp lib/*.jar src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java && java -cp "bin;lib/*" main.server.HOTELIERServer

package main.server;

import java.net.Socket;
import java.util.List;

// import main.dataModels.JsonUtil;
import main.dataModels.JsonUtil;
import main.dataModels.Capitals;
import main.dataModels.Hotel;

import java.net.ServerSocket;
import java.io.File;
import java.io.IOException;

public class HOTELIERServer implements Runnable {
    private int port;
    private boolean isRunning;
    private ServerSocket serverSocket;
    private UserManagement userManagement;
    private HotelManagement hotelManagement;

    private static final String userPath = "../data/users.JSON";
    private static final String hotelPath = "../data/hotel.JSON";
    // private HotelManagement hotelManagement;

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
    public void start() {
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
                this.userManagement = new UserManagement("../data/users.JSON");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run () {
                        // todo - HOTELIERServer.start.run - implement client handling logic
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }

    public void stop () {
        isRunning = false;
        try{
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Server stoped");
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del server: " + e.getMessage());
        }
    }

    /**
     * save user and hotel data in a JSON file
     */
    public void saveData(){
        // todo - HOTELIERServer.saveData
        // save users
        // save hotels
    }

    // todo - check and handle exception
    public static void main(String[] args) throws Exception{
        System.out.println("start execution...");
        
        // initializing hotel manager
        HOTELIERServer HServer = new HOTELIERServer(0); 
        HServer.initalizeHotel();
        
        // test      

    }

    private void initalizeHotel () {
        this.hotelManagement = new HotelManagement("src/main/data/Hotels.json");
        System.out.println("hotelManagement initialized...");
    }

    @Override
    public void run() {
        // TODO - implement client handling logic
        // throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}
