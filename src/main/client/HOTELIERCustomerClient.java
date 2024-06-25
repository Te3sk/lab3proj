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

    private Set<String> errors = new HashSet<String>();

    /**
     * The HOTELIERCustomerClient class represents a client for the HOTELIER customer system.
     * It provides functionality for managing user authentication and handling errors.
     */
     @SuppressWarnings("deprecation") // todo - temp ignoring warning
    public HOTELIERCustomerClient(InetAddress tcpAddr, InetAddress udpAddr, Integer port, Integer multicastPort) throws IOException {
        try {
            this.username = null;
            this.logged = false;
            // initialize the selector and the socket channel
            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            this.socketChannel.connect(new InetSocketAddress(tcpAddr, port));
            this.socketChannel.configureBlocking(false);
            this.socketChannel.register(this.selector, 0);
            // initialize the multicast socket
            this.notificator = new MulticastSocket(multicastPort);
            // join the multicast group
            this.notificator.joinGroup(udpAddr);
            this.executorService = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            // TODO: handle exception
        }
        this.cli = new CLI();
        // todo - this.executorService.submit() - classe per ricevere notifiche sulla multicast socket
        
        // set error messages
        this.errors.add("USERN_Y");
        this.errors.add("USERN_N");
        this.errors.add("EMPTYF");
        this.errors.add("WRONGPSW");
        this.errors.add("HOTEL");
        this.errors.add("CITY");

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

    /**
     * start the client and the CLI
     * 
     */
    public void start() {
        // todo - HOTELIERServer.start
        System.out.println("Client is running...");

        // ! temp test
        // HOME
        // int n = cli.homePage(null);
        // System.out.println("op: " + op.get(n));

        // INSERT CREDENTIAL (SIGNIN AND LOGIN)
        // String[] cred = cli.insertCred();
        // this.username = cred[0];
        // this.psw = cred[1];
        // System.out.println("username: " + this.username + " | psw: " + this.psw);

        // INSERT REVIEW
        // Object[] temp = cli.insertReview();
        // String hotelName = (String) temp[0];
        // Review review = (Review) temp[1];
        // System.out.println("Review of " + hotelName + "\n" + review.toString());

        int n = -1;
        n = this.cli.homePage(null);

        while(true) {
            String[] param = null;
            String city = null;
            Object[] param2 = null;
            switch (n) {
                // signin
                case 1:
                    String[] creds = this.cli.insertCred();
                    try {
                        this.register(creds[0], creds[1]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                // login
                case 2:
                    param = this.cli.insertCred();
                    try {
                        this.login(param[0], param[1]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                //hotel
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
                    // todo -  check if the Object[].. stuff work
                    param2 = this.cli.insertReview();
                    try {
                        // this.ins
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                case 7:
                    // logout
                case 8:
                    // quit            
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
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);

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
        req += "_SIGNIN_" + this.socketChannel.toString() + "_" + username + "_" + psw;

        // send cred
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
                    System.out.println(response);
                    break;
            }
        }
    }

    /**
     * do the login of an existing user
     * 
     * @param username
     * @param psw
     */
    public void login(String username, String psw) throws Exception {
        // todo - HOTELIERServer.login
        String req = "";
        req += "_LOGIN_" + this.socketChannel + "_" + username + "_" + psw;

        this.write(req);

        String response = this.readAsString();
        if (!this.errors.contains(response)) {
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
    }

    /**
     * do the logout of the user
     * 
     * @param username
     */
    public void logout(String username) throws Exception {
        String req = "_LOGOUT_" + this.socketChannel + "_" + this.username;

        this.write(req);

        String response = this.readAsString();

        if (!this.errors.contains(response)) {
            this.logged = false;
            this.username = null;
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
        String req = "_HOTEL_" + this.socketChannel + "_" + this.username + "_" + nomeHotel + "_" + città;

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
        // todo - HOTELIERServer.searchAllHotels

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
        // todo - HOTELIERServer.insertReview
    }

    /**
     * show the badges of the users
     */
    public void showMyBadges() throws Exception {
        // todo - HOTELIERServer.showMyBadges
    }
}
