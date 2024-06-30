package main.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
// import java.util.Scanner;
import java.util.Set;
// import java.util.Timer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HOTELIERServer implements Runnable {
    private long timeInterval;
    private Selector selector;
    private InetAddress tcpAddr;
    private int tcpPort;
    private InetAddress udpAddr;
    private int udpPort;
    private boolean isRunning;
    private ServerSocketChannel serverSocketChannel;
    private UserManagement userManagement;
    private HotelManagement hotelManagement;
    private List<RequestHandler> requestHandlers;
    private DataPersistence dataPersistence;
    private Lock lock = new ReentrantLock();
    ThreadPoolExecutor executor;

    /**
     * HOTELIERServer constructor: to start the server properly, the start method
     * must be called.
     * 
     * @param tcpAddr       The address for the TCP connection
     * @param udpAddr       The address for the UDP connection where data will be
     *                      streamed
     * @param tcpPort       The port number for the TCP socket
     * @param interval      The time interval for ranking updates in milliseconds
     * @param broadcastPort The port number for the broadcast connection
     * @param hotelFile     The absolute path to the hotel file
     * @param userFile      The absolute path to the user file
     */
    public HOTELIERServer(InetAddress tcpAddr, InetAddress udpAddr, int tcpPort, long interval, int broadcastPort,
            String hotelFile, String userFile) {
        try { // attributes initialization
            this.tcpAddr = tcpAddr;
            this.tcpPort = tcpPort;
            this.udpAddr = udpAddr;
            this.udpPort = broadcastPort;
            this.timeInterval = interval;
            this.hotelManagement = new HotelManagement(hotelFile);
            this.userManagement = new UserManagement(userFile);
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.bind(new InetSocketAddress(tcpAddr, tcpPort));
            this.selector = Selector.open();
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            this.isRunning = true;
            this.requestHandlers = new ArrayList<>();
            this.executor = new ThreadPoolExecutor(1, 100, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        } catch (Exception e) {
            // ! error message !
            System.out.println("Error during server construction: " + e.getMessage());
        }
    }

    /**
     * Fetches the appropriate RequestHandler based on the given SocketChannel.
     * 
     * @param socketChannel The SocketChannel to match with the RequestHandler's
     *                      SocketChannel.
     * @return The matching RequestHandler, or null if no match is found.
     * @throws IOException If an I/O error occurs.
     */
    public RequestHandler fetchHandler(SocketChannel socketChannel) throws IOException {
        // iter over request handlers
        for (RequestHandler handler : this.requestHandlers) {
            // take the socketchannel of this current handler
            SocketChannel handlerChannel = handler.getCallerAddres();
            if (handlerChannel.getRemoteAddress().equals(socketChannel.getRemoteAddress())) {
                // if the remote address match with that one in the socketchannel in the
                // parameter
                return handler;
            }
        }
        return null;
    }

    public void removeHandler(RequestHandler handler) {
        this.requestHandlers.remove(handler);
    }

    /**
     * wait for new msgs or connections
     */
    @Override
    public void run() {
        // * Log message *
        System.out.println("Server is running...");

        // TODO - remove, already initialized in the constructor
        // Create a ThreadPoolExecutor with a core of pool (size 1, max size 100)
        // and keep-alive time of 300 sec for idle threads
        // // ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 100, 300, TimeUnit.SECONDS,
        // //         new LinkedBlockingDeque<>());

        // Loop while the server is running
        while (isRunning) {
            try { // handles non-blocking I/O operations, accepting new connections and reading
                  // data from ready channels
                  // perform a non blocking selection operation to check for ready channels
                int readyChannels = selector.selectNow();

                // if no channels ready, skip this ite
                if (readyChannels == 0) {
                    continue;
                }

                // get the set of selection keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) { // iterate over selection keys
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) { // if the key's channel is ready to accept a new connection
                        // accept the connection
                        SocketChannel connection = this.serverSocketChannel.accept();

                        // configure the connection to be non-blocking
                        connection.configureBlocking(false);
                        // register the new connection with the selector
                        connection.register(this.selector, SelectionKey.OP_READ);

                        // add new request handler to the list
                        synchronized (this.requestHandlers) {
                            this.requestHandlers.add(new RequestHandler(this.userManagement, this.hotelManagement,
                                    connection, this.selector, this.udpAddr, this.udpPort, this.timeInterval, this.lock,
                                    this));
                        }

                        // TODO - temp debug print
                        System.out.println("* DEBUG - \tnew connection from " + connection.getRemoteAddress());
                    } else if (key.isReadable()) { // else if the key's channel is ready to read data
                        // get the connection from the key
                        SocketChannel connection = (SocketChannel) key.channel();
                        // cancel the key's interest in read operations
                        connection.keyFor(this.selector).interestOps(0);
                        RequestHandler handler;
                        synchronized (this.requestHandlers) {
                            handler = this.fetchHandler(connection);
                        }

                        if (handler != null) {
                            // // // submit the request handler to the executor
                            // // executor.submit(handler);
                            // submit the request to the threadpool
                            executor.submit(new Worker(handler));
                        } else {
                            // Nessun handler trovato, chiudi la connessione
                            connection.close();
                            key.cancel();

                            // TODO - temp debug print
                            System.out.println("* DEBUG - \tclose connection from " + connection.getRemoteAddress());

                            // Rimuovi il RequestHandler dalla lista
                            synchronized (this.requestHandlers) {
                                Iterator<RequestHandler> tempIterator = this.requestHandlers.iterator();
                                while (tempIterator.hasNext()) {
                                    RequestHandler h = tempIterator.next();
                                    try {
                                        if (h.getCallerAddres().equals(connection)) {
                                            tempIterator.remove();
                                        }
                                    } catch (Exception e) {
                                        // ! Error message !
                                        System.out.println("Error during handler removal: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }

                    // remove the key from the iterator
                    keyIterator.remove();
                }
            } catch (Exception e) {
                if (!this.isRunning) {
                    // * Log message *
                    System.out.println("Server is shutting down...");
                    break;
                } else {
                    // ! Error message !
                    System.out.println(e.getMessage());
                }
            }
        }
        executor.close();
        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        } finally {
            try {
                serverSocketChannel.close();
                selector.close();
            } catch (IOException e) {
                System.out.println("Error closing server: " + e.getMessage());
            }
        }
    }

    private class Worker implements Runnable {
        private RequestHandler handler;

        public Worker(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            this.handler.run();
        }
    }

    // TODO - handling server.close
}
