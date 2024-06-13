// compile: javac -d bin -cp lib/*.jar src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java
// run: java -cp "bin;lib/*" main.server.HOTELIERServer
// compile and run: javac -d bin -cp lib/*.jar src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java && java -cp "bin;lib/*" main.server.HOTELIERServer

package main.server;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import main.dataModels.JsonUtil;
import main.dataModels.Capitals;
import main.dataModels.Hotel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.io.File;
import java.io.IOException;

public class HOTELIERServer implements Runnable {
    private long timeInterval;
    private NotificationService notificationService;
    private Selector selector;
    private int tcpPort;
    private boolean isRunning;
    private ServerSocketChannel serverSocketChannel;
    private UserManagement userManagement;
    private HotelManagement hotelManagement;

    /**
      * HOTELIERServer constructor: to start the server properly, the start method must be called.

      * @param tcpAddr The address for the TCP connection
      * @param udpAddr The address for the UDP connection where data will be streamed
      * @param tcpPort The port number for the TCP socket
      * @param interval The time interval for ranking updates in milliseconds
      * @param broadcastPort The port number for the broadcast connection
      * @param hotelFile The absolute path to the hotel file
      * @param userFile The absolute path to the user file
      */
    public HOTELIERServer (InetAddress tcpAddr, InetAddress udpAddr, int tcpPort, long interval, int broadcastPort, String hotelFile, String userFile){
        try { 
            this.timeInterval = interval;
            this.hotelManagement = new HotelManagement(hotelFile);
            this.userManagement = new UserManagement(userFile);
            this.notificationService = new NotificationService(hotelManagement, udpAddr, broadcastPort);
            this.serverSocketChannel = serverSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.bind(new InetSocketAddress(tcpAddr, tcpPort));
            this.selector.open();
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            System.out.println("Error during server construction: " + e.getMessage());
        }
    }

    /**
     * start the server
     */
    public void start() {
        try {
            // start listening
            Thread listener = new Thread(this);
            listener.start();
            Timer timer = new Timer();
            this.isRunning = true;

            // start broadcasting
            timer.scheduleAtFixedRate(this.notificationService, this.timeInterval, this.tcpPort);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Press any key to stop the server...\n");
            // wait for shutdown request
            scanner.nextLine();
            // shutdown
            System.out.println("shouting down the server...");
            scanner.close();
            this.serverSocketChannel.close();
            listener.interrupt();
            this.notificationService.close();
            timer.cancel();
            this.selector.close();
            this.hotelManagement.saveHotel();
            this.userManagement.saveUsers();
            this.isRunning = false;
        } catch (Exception e) {
            System.out.println("Error during server start: " + e.getMessage());
        }
    }

    /**
     * wait for new msgs or connections
     */
    @Override
    public void run() {
        // Create a ThreadPoolExecutor with a core of pool (size 1, max size 100)
        // and keep-alive time of 300 sec for idle threads
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 100, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        // Loop while the server is running
        while(isRunning) {
            try{
                // perform a non blocking selection operation to check for ready channels
                int readyChannels = selector.selectNow();
                // if no channels ready, skip this ite
                if (readyChannels == 0) {
                    continue;
                }

                // get the set of selection keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                // iterate over selection keys
                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    // if the key's channel is ready to accept a new connection 
                    if (key.isAcceptable()) {
                        // accept the connection
                        SocketChannel connection = this.serverSocketChannel.accept();
                        // configure the connection to be non-blocking
                        connection.configureBlocking(false);
                        // register the new connection with the selector
                        connection.register(this.selector, SelectionKey.OP_READ);
                        // todo - add the connection to the list of connections 

                    }
                }
            } catch (Exception e) {}
        }
        // TODO - implement client handling logic

        // throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}
