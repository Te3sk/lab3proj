package main.client;

import java.io.IOException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.dataModels.Review;

import java.nio.channels.Selector;

// ! RESPONSE TYPES
// success - *success string*
// existing username - USERN_Y
// non existing username - USERN_N
// empty fields - EMPTYF
// wrong password - WRONGPSW
// non existing hotel - HOTEL
// non existing city - CITY

public class HOTELIERCustomerClient {
    private CLI cli;
    private boolean logged;
    private String username;
    private SocketChannel socketChannel;
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private MulticastSocket notificator;
    private ExecutorService executorService;
    private Map<Integer, String> op = new HashMap<Integer, String>();
    private Boolean isConnect;

    private Set<String> errors = new HashSet<String>();

    /**
     * The HOTELIERCustomerClient class represents a client for the HOTELIER
     * customer system.
     * It provides functionality for managing user authentication and handling
     * errors.
     */
    @SuppressWarnings("deprecation") // TODO - temp ignoring warning
    public HOTELIERCustomerClient(InetAddress tcpAddr, InetAddress udpAddr, Integer port, Integer multicastPort)
            throws IOException, Exception {
        try {
            this.username = null;
            this.logged = false;
            // initialize the selector and the socket channel
            this.selector = Selector.open();
            // open a socket channel
            this.socketChannel = SocketChannel.open();
            // connect to the server
            this.isConnect = this.socketChannel.connect(new InetSocketAddress(tcpAddr, port));
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
                this.notificator = new MulticastSocket(multicastPort);
                // join the multicast group
                this.notificator.joinGroup(udpAddr);
                this.executorService = Executors.newSingleThreadExecutor();
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            // System.out.println("Error during client initialization:\n" + e.getMessage());
            throw new IOException(e.getMessage());
        }

        if (this.isConnect == true) {
            this.cli = new CLI();
            // TODO - this.executorService.submit() - classe per ricevere notifiche sulla
            // multicast socket

            // set error messages
            this.errors.add("USERN_Y");
            this.errors.add("USERN_N");
            this.errors.add("EMPTYF");
            this.errors.add("WRONGPSW");
            this.errors.add("HOTEL");
            this.errors.add("CITY");
            // TODO
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
        // TODO - HOTELIERServer.start
        System.out.println("Client is running...");

        // // // TODO - temp debug print
        // // System.out.println("* DEBUG - \t (HOTELIERCustomerClient.isConnect = " +
        // this.isConnect + ")");
        if (this.socketChannel.keyFor(this.selector) == null) {
            System.out.println("key is null");
        } else {
            System.out.println("key is not null");
        }

        this.handleUser();

        // ! CODE BELOW TRANFERED TO handleUser() !
        // // int n = -1;
        // // n = this.cli.homePage(null);
        // // while (true) {
        // // String[] param = null;
        // // String city = null;
        // // Object[] param2 = null;
        // // switch (n) {
        // // // signin
        // // case 1:
        // // String[] creds = this.cli.insertCred();
        // // try {
        // // this.register(creds[0], creds[1]);
        // // } catch (Exception e) {
        // // System.out.println("Error during registration: " + e.getMessage());
        // // }
        // // break;
        // // // login
        // // case 2:
        // // param = this.cli.insertCred();
        // // try {
        // // this.login(param[0], param[1]);
        // // } catch (Exception e) {
        // // System.out.println(e.getMessage());
        // // }
        // // break;
        // // // hotel
        // // case 3:
        // // param = this.cli.searchHotel();
        // // try {
        // // this.searchHotel(param[0], param[1]);
        // // } catch (Exception e) {
        // // System.out.println(e.getMessage());
        // // }
        // // break;
        // // // all hotel
        // // case 4:
        // // city = this.cli.searchAllHotels();
        // // try {
        // // this.searchAllHotels(city);
        // // } catch (Exception e) {
        // // System.out.println(e.getMessage());
        // // }
        // // break;
        // // // review
        // // case 5:
        // // // TODO - check if the Object[].. stuff work
        // // param2 = this.cli.insertReview();
        // // try {
        // // // this.ins
        // // } catch (Exception e) {
        // // System.out.println(e.getMessage());
        // // }
        // // case 7:
        // // // logout
        // // case 8:
        // // // quit
        // // default:
        // // break;
        // // }
        // // }
    }

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

        // TODO - temp debug print
        System.out.println("* DEBUG (handleUser)- \tbefore while");

        while (this.isConnect) {
            int n = -1;

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
                        // TODO - this.insertReview
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                case 7:
                    // TODO - temp debug print
                    System.out.println("* DEBUG - \tLOGOUT CASE");
                    this.logout(this.username);
                    break;
                case 8:
                    this.quit();
                    break;
                default:
                    break;
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
        // TODO - temp debug print
        System.out.println("*DEBUG (CustomerClient.write)\tmsg to print: " + message);

        SelectionKey key = this.socketChannel.keyFor(selector);
        if (key == null) {
            System.out.println("channel not registered or closed");
            return; // exit from the method if the channel is not registered or closed
        }
        // TODO - temp debug print
        System.out.println("*DEBUG (CustomerClient.write)\tselection key ok (not null)");

        key.interestOps(SelectionKey.OP_WRITE);
        // socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);

        // TODO - temp debug print
        System.out.println("*DEBUG (CustomerClient.write)\twait for ready selection...");

        // wait untill there are ready selection
        while (this.selector.select() == 0) {
        }

        // TODO - temp debug print
        System.out.println("*DEBUG (CustomerClient.write)\tready selection found");

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

        // TODO - temp debug print
        System.out.println(
                "*DEBUG (CustomerClient.write)\tmessage converted in bytes and divided in chunks\n\t\t sending them...");

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

        // TODO - temp debug print
        System.out.println("*DEBUG (CustomerClient.write)\tmessage wrote on the socket channel");

        // reset the interesting option for no operation
        socketChannel.keyFor(selector).interestOps(0);
    }

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

        this.handleUser();
    }

    /**
     * do the logout of the user
     * 
     * @param username
     */
    public void logout(String username) {
        // TODO - temp debug print
        System.out.println("* DEBUG (CustomerClient.logout)\tlogout method called");

        String req = "LOGOUT_" + this.socketChannel + "_" + this.username;
        String response = "";

        try {
            this.write(req);
            response = this.readAsString();
        } catch (IOException e) {
            System.out.println("IOError during logout: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during logout: " + e.getMessage());
        }

        // TODO - temp debug print
        System.out.println("* DEBUG (CustomerClient.logout)- \tresponse: " + response);

        if (!this.errors.contains(response)) {
            // TODO - temp debug print
            System.out.println("* DEBUG (CustomerClient.logout)- \tno error finded in response\n\t" + response);
            this.logged = false;
            this.username = null;
        } else {
            // TODO - temp debug print
            System.out.println("* DEBUG (CustomerClient.logout)- \terror finded in response\n\t" + response);
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
                + Double.toString(review.getRate()) + "_";

        Map<String, Integer> ratings = review.getRatings();
        for (String key : ratings.keySet()) {
            req += key + ":" + Integer.toString(ratings.get(key)) + ",";
        }

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
            System.out.println(response);
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
            if (this.notificator != null && !this.executorService.isShutdown()) {
                this.executorService.shutdown();
                try {
                    // wait for the executor service to terminate
                    if (!this.executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        this.executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    this.executorService.shutdownNow();
                }
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
