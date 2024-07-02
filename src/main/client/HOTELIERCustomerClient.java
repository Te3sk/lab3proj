package main.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.dataModels.Review;

import java.nio.channels.Selector;

public class HOTELIERCustomerClient {
    private CLI cli;
    private boolean logged;
    private String username;
    private SocketChannel socketChannel;
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private MulticastSocket notificator;
    private Map<Integer, String> op = new HashMap<Integer, String>();
    private Boolean isConnect;

    private Lock lock;
    private Thread notificationThread;
    private NotificationReciever notificationReciever;

    private Set<String> errors = new HashSet<String>();

    /**
     * The HOTELIERCustomerClient class represents a client for the HOTELIER
     * customer system.
     * It provides functionality for managing user authentication and handling
     * errors.
     */
    @SuppressWarnings("deprecation") // port = tcpPort, multicastPort = udpPort
    public HOTELIERCustomerClient(InetAddress tcpAddr, InetAddress udpAddr, Integer tcpPort, Integer udpPort)
            throws IOException, Exception {
        // get the lock
        this.lock = new ReentrantLock();

        try { // initialize attributes and other stuff
            this.username = null;
            this.logged = false;
            // initialize the selector and the socket channel
            this.selector = Selector.open();
            // open a socket channel
            this.socketChannel = SocketChannel.open();
            // connect to the server
            this.isConnect = this.socketChannel.connect(new InetSocketAddress(tcpAddr, tcpPort));
            System.out.println("Try to connect with the server...");
            // configure the socket channel
            this.socketChannel.configureBlocking(false);
            // register the socket channel with the selector
            this.socketChannel.register(this.selector, 0);

            if (this.isConnect == false) {
                // * Log message *
                throw new Exception("Connection with the server failed");
            } else {
                // * Log message *
                System.out.println("Connection successful\n");

                // initialize the multicast socket
                this.notificator = new MulticastSocket(udpPort);
                // join the multicast group
                this.notificator.joinGroup(udpAddr);
            }

            // initialize notification thread
            this.notificationReciever = new NotificationReciever(udpAddr, udpPort, this.lock, this.logged);
            this.notificationThread = new Thread(this.notificationReciever);
            this.notificationThread.start();
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            // System.out.println("Error during client initialization:\n" + e.getMessage());
            throw new IOException(e.getMessage());
        }

        if (this.isConnect == true) { // if connect initialize cli, errors and operations
            this.cli = new CLI();

            // set error messages
            this.errors.add("USERN_Y");
            this.errors.add("USERN_N");
            this.errors.add("EMPTYF");
            this.errors.add("WRONGPSW");
            this.errors.add("HOTEL");
            this.errors.add("CITY");
            this.errors.add("FORMAT");

            // set operations map
            this.op.put(1, "SIGNIN");
            this.op.put(2, "LOGIN");
            this.op.put(3, "HOTEL");
            this.op.put(4, "ALLHOTEL");
            this.op.put(5, "REVIEW");
            this.op.put(6, "BADGE");
            this.op.put(7, "LOGOUT");
            this.op.put(8, "QUIT");
        }

    }

    /**
     * start the client and the CLI
     * 
     */
    public void start() {
        System.out.println("Client is running...");

        // check if the key is null
        if (this.socketChannel.keyFor(this.selector) == null) {
            System.out.println("key is null");
        } else {
            System.out.println("key is not null");
        }

        this.handleUser();
    }

    // COMUNICATION METHODS

    /**
     * Handles the user interaction in the HOTELIER customer client.
     * This method allows the user to perform various actions based on the selected
     * option.
     * The user can register, login, search for hotels, view all hotels, write
     * reviews, and logout.
     * 
     * @throws Exception if an error occurs during the execution of the selected
     *                   action.
     */
    protected void handleUser() {
        while (this.isConnect) {
            try {
                int n = -1;

                // get the condition variable
                this.lock.lock();

                if (this.logged) {
                    n = this.cli.homePage(this.username);
                } else {
                    n = this.cli.homePage(null);
                }

                String[] param = null;
                String city = null;
                Object[] param2 = null;
                switch (n) {
                    // signin
                    case 1:
                        String[] creds = this.cli.insertCred("REGISTRATION");
                        try {
                            this.register(creds[0], creds[1]);
                        } catch (Exception e) {
                            System.out.println("Error during registration: " + e.getMessage());
                        }
                        break;
                    // login
                    case 2:
                        param = this.cli.insertCred("LOGIN");
                        try {
                            this.login(param[0], param[1]);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    // hotel
                    case 3:
                        param = this.cli.searchHotel();
                        try {
                            this.searchHotel(param[0], param[1]);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    // all hotel
                    case 4:
                        city = this.cli.searchAllHotels();
                        try {
                            this.searchAllHotels(city);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    // review
                    case 5:
                        param2 = this.cli.insertReview();
                        try {
                            this.insertReview((String) param2[0], (String) param2[1], (Review) param2[2]);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 6:
                        try {
                            this.showMyBadges();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 7:
                        this.logout(this.username);
                        break;
                    case 8:
                        this.quit();
                        break;
                    default:
                        break;
                }
            } finally {
                this.lock.unlock();
            }

        }
    }

    /**
     * Reads the response from the server as a string.
     *
     * @return The response from the server as a string.
     * @throws IOException If an I/O error occurs.
     */
    protected String readAsString() throws IOException {
        // set the interest for reading operations
        this.socketChannel.keyFor(this.selector).interestOps(SelectionKey.OP_READ);

        // wait untill there are ready selections
        while (this.selector.select() == 0) {
        }

        // obtain the key set with the ready selections
        Set<SelectionKey> keys = this.selector.selectedKeys();

        // create an interator and iterate over selection keys
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            // remove all the processed selection keys
            iterator.next();
            iterator.remove();
        }

        // string builder to accumulate the readed datas
        StringBuilder stringBuilder = new StringBuilder();

        // cycle to read datas in the channel
        while (true) {
            buffer.clear();
            int bytesRead = this.socketChannel.read(buffer);
            if (bytesRead == -1) {
                // closed channel
                return "";
            }
            if (bytesRead == 0) {
                // no more datas
                break;
            }
            buffer.flip();

            // convert bytes in a string and add them to the string builder
            byte[] responseBytes = new byte[buffer.remaining()];
            buffer.get(responseBytes);
            stringBuilder.append(new String(responseBytes, StandardCharsets.UTF_8));
        }

        // reset the interesting option for no operation
        socketChannel.keyFor(selector).interestOps(0);

        return stringBuilder.toString();
    }

    /**
     * Writes a message to the socket channel.
     *
     * @param message the message to be written
     * @throws IOException if an I/O error occurs while writing to the socket
     *                     channel
     */
    protected void write(String message) throws IOException {
        // set the channel interest for writing operation

        SelectionKey key = this.socketChannel.keyFor(selector);
        if (key == null) {
            System.out.println("channel not registered or closed");
            return; // exit from the method if the channel is not registered or closed
        }

        // set the interest for writing operations
        key.interestOps(SelectionKey.OP_WRITE);

        // wait untill there are ready selection
        while (this.selector.select() == 0) {
        }

        // get the key set of the ready selections
        Set<SelectionKey> keys = this.selector.selectedKeys();

        // create an iterator to iterate over the selection keys
        Iterator<SelectionKey> iterator = keys.iterator();

        // remove all the processed selection keys
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        // convert the msg in a byte array using UTF-8
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // compute how many chunks are needed to send the msg
        int numChunks = (int) Math.ceil((double) messageBytes.length / 1024);

        // Cycle to send every block (1024)
        for (int i = 0; i < numChunks; i++) {
            // compute start and end point
            int start = i * 1024;
            int end = Math.min(start + 1024, messageBytes.length);

            buffer.clear();

            buffer.put(Arrays.copyOfRange(messageBytes, start, end));

            buffer.flip();

            while (buffer.hasRemaining()) {
                this.socketChannel.write(buffer);
            }
        }

        // reset the interesting option for no operation
        socketChannel.keyFor(selector).interestOps(0);
    }

    /**
     * The `NotificationReciever` class is responsible for handling incoming
     * notifications in a separate thread.
     * It listens for incoming UDP packets, extracts the message, and handles the
     * notification.
     * If an error occurs during the notification receiving process, an error
     * message is printed.
     */
    protected class NotificationReciever implements Runnable {
        private InetAddress udpAddr;
        private int udpPort;
        private Lock lock;
        private MulticastSocket notificator;
        private Boolean isLogged;

        /**
         * Constructs a new NotificationReciever object with the specified UDP address
         * and port.
         *
         * @param udpAddr the UDP address to bind the receiver to
         * @param udpPort the UDP port to bind the receiver to
         */
        public NotificationReciever(InetAddress udpAddr, int udpPort, Lock lock, Boolean isLogged) {
            this.udpAddr = udpAddr;
            this.udpPort = udpPort;
            this.lock = lock;
            this.isLogged = isLogged;
        }

        /**
         * Executes the logic for handling incoming notifications.
         * This method runs in a separate thread and listens for incoming UDP packets.
         * When a packet is received, it extracts the message and handles the
         * notification.
         * If an error occurs during the notification receiving process, an error
         * message is printed.
         */
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            while (this.isLogged != null && this.isLogged == true) {
                // wait for the user to log in
                try {
                    this.notificator = new MulticastSocket(udpPort);
                    this.notificator.joinGroup(udpAddr);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

                    this.notificator.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());

                    // take the lock before handling the notification
                    this.lock.lock();

                    try {
                        // * Log message *
                        System.out.println("----------------------------\n New top hotel in local ranking:\n" + msg
                                + "\n----------------------------");
                    } finally {
                        // release the lock after handling the notification
                        this.lock.unlock();
                    }

                } catch (Exception e) {
                    // ! Error message !
                    System.out.println("Error during notification receiving: " + e.getMessage());
                    // ! Cannot assign requested address: bind
                }
            }
        }

        /**
         * Stops the notification receiver.
         */
        public void stop() {
            if (notificator != null) {
                notificator.close();

            }
        }
    }

    /**
     * Stops the notification functionality.
     * If the notification receiver is running, it will be stopped and the
     * notification thread will be joined.
     */
    protected void stopNotification() {
        if (this.notificationReciever != null) {
            this.notificationReciever.stop();
            try {
                this.notificationThread.join();
            } catch (InterruptedException e) {
                // ! Error message !
                System.out.println("Error during stopping notification thread: " + e.getMessage());
            }
        }
    }

    // OPERATION METHODS

    /**
     * register a new user
     * 
     * @param username
     * @param psw
     */
    public void register(String username, String psw) throws Exception {
        String req = "";
        req += "SIGNIN_" + this.socketChannel.toString() + "_" + username + "_" + psw;
        // send credentials to the server
        this.write(req);

        // recieve response
        String response = this.readAsString();
        if (!this.errors.contains(response)) {
            System.out.println(response);
        } else {
            switch (response) {
                case "USERN_Y":
                    System.out.println("Error: username already exists");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println("\tRESPONSE: " + response);
                    break;
            }
        }

        this.handleUser();
    }

    /**
     * do the login of an existing user
     * 
     * @param username
     * @param psw
     */
    public void login(String username, String psw) throws Exception {
        String req = "";
        // build the request string
        req += "LOGIN_" + this.socketChannel + "_" + username + "_" + psw;

        // send the request to the server
        this.write(req);

        // read the response from the server
        String response = this.readAsString();
        if (!this.errors.contains(response)) {
            // if is all ok
            this.logged = true;
            this.username = username;
            System.out.println(response);
        } else {
            switch (response) {
                case "USERN_N":
                    System.out.println("Error: username not founded");
                    break;
                case "WRONGPSW":
                    System.out.println("Error: wrong password");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println(response);
                    break;
            }
        }

        // once the user is logged, start the notification thread
        this.notificationThread.start();

        this.handleUser();
    }

    /**
     * do the logout of the user
     * 
     * @param username
     */
    public void logout(String username) {
        String req = "LOGOUT_" + this.socketChannel + "_" + this.username;
        String response = "";

        try {
            this.write(req);
            response = this.readAsString();
        } catch (IOException e) {
            System.out.println("IOError during logout: " + e.getMessage() + "\n\tCan't logout");
            return;
        } catch (Exception e) {
            System.out.println("Unexpected error during logout: " + e.getMessage() + "\n\tCan't logout");
            return;
        }

        if (!this.errors.contains(response)) {
            this.logged = false;
            this.username = null;

            this.stopNotification();
        } else {
            if (response.equals("USERN_N")) {
                System.out.println("Error: username not found");
            } else {
                System.out.println(response);
            }
        }
    }

    /**
     * search an hotel by name and city
     * 
     * @param nomeHotel
     * @param città
     */
    public void searchHotel(String nomeHotel, String città) throws Exception {
        String req = "HOTEL_" + this.socketChannel + "_" + this.username + "_" + nomeHotel + "_" + città;

        this.write(req);

        String response = this.readAsString();

        if (!this.errors.contains(response)) {
            System.out.println(response);
        } else {
            switch (response) {
                case "HOTEL":
                    System.out.println("Error: hotel not found");
                    break;
                case "CITY":
                    System.out.println("Error: city not found");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println(response);
                    break;
            }
        }

        // * Log message *
        System.out.println("\nPress enter to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("Error during waiting for user input: " + e.getMessage());
        }
    }

    /**
     * search all the hotel in a city, ordered by ranking
     * 
     * @param città
     */
    public void searchAllHotels(String città) throws Exception {
        String req = "ALLHOTEL_" + this.socketChannel + "_" + this.username + "_" + città;

        this.write(req);

        String response = this.readAsString();
        if (!this.errors.contains(response)) {
            System.out.println(response);
        } else {
            switch (response) {
                case "CITY":
                    System.out.println("Error: city not found");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println(response);
                    break;
            }
        }

        // * Log message *
        System.out.println("\nPress enter to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("Error during waiting for user input: " + e.getMessage());
        }
    }

    /**
     * insert a review for an hotel
     * 
     * @param hotel        name of the hotel
     * @param città        name of the city where the hotel is
     * @param GlobalScore
     * @param singleScores
     */
    public void insertReview(String hotel, String città, Review review)
            throws Exception {
        String req = "REVIEW_" + this.socketChannel + "_" + this.username + "_" + hotel + "_" + città + "_"
                + review.toString();

        this.write(req);

        String response = this.readAsString();
        if (!this.errors.contains(response)) {
            System.out.println(response);
        } else {
            switch (response) {
                case "HOTEL":
                    System.out.println("Error: hotel not found");
                    break;
                case "CITY":
                    System.out.println("Error: city not found");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println(response);
                    break;
            }
        }
    }

    /**
     * show the badges of the users
     */
    public void showMyBadges() throws Exception {
        String req = "BADGE_" + this.socketChannel + "_" + this.username;

        this.write(req);

        String response = this.readAsString();

        if (!this.errors.contains(response)) {
            System.out.println("BADGE: " + response);
        } else {
            switch (response) {
                case "USERN_N":
                    System.out.println("Error: username not found");
                    break;
                case "EMPTYF":
                    System.out.println("\tError: empty values");
                    break;
                default:
                    System.out.println(response);
                    break;
            }
        }
    }

    /**
     * Shuts down the client, performing necessary cleanup operations.
     * If the user is logged in, it logs out the user.
     * Closes the socket channel, selector, and multicast socket if they are open.
     * Sets the connection status to false.
     */
    public void quit() {
        // * Log message *
        System.out.println("Shutting down the client...");

        try {
            // logout the user if is logged
            if (this.logged) {
                this.logout(this.username);
            }

            // close the socket channel if is open
            if (this.socketChannel != null && this.socketChannel.isOpen()) {
                this.socketChannel.close();
            }

            // close the selector if is open
            if (this.selector != null && this.selector.isOpen()) {
                this.selector.close();
            }

            // close the multicast socket if is open
            if (this.notificationReciever != null) {
                this.notificationReciever.stop();
            }

            this.isConnect = false;
            // * Log message *
            System.out.println("Client disconnected succesfully");
        } catch (IOException e) {
            System.out.println("Error during client shutdown: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during client shutdown: " + e.getMessage());
        }
    }
}
