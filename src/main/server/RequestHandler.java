package main.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import main.dataModels.Hotel;
import main.dataModels.Review;
import main.dataModels.User;

public class RequestHandler implements Runnable {
    private InetAddress udpAddr;
    private int udpPort;

    private String type;
    private SocketChannel callerAddress;
    private String username;
    private String psw;
    private String hotelName;
    private String cityName;
    private Review review;

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;
    // TODO (remove) - private Boolean isRunning = true;

    private HOTELIERServer server;
    private UserManagement userManagement;
    private HotelManagement hotelManagement;
    // TODO (remove) - private DataPersistence dataPersistence;
    private Set<String> errors = new HashSet<String>();

    /**
     * Executes the main logic of the RequestHandler in a separate thread.
     * Continuously reads messages from the client, checks their validity, and
     * dispatches them.
     * If the message is a quit message, the server is terminated.
     * If the message has an incorrect number of parameters, a "FORMAT" response is
     * sent back to the client.
     * If the message is valid, it is dispatched to the appropriate handler.
     * Handles exceptions related to reading from the client and closing the
     * channel.
     */
    @Override
    public void run() {
        // TODO - remove this, put it in HOTELIERServer
        // creating thread for saving data
        // Thread backupThread = new Thread(this.dataPersistence);

        // TODO - remove this, put it in HOTELIERServer
        // initializing that thread
        // backupThread.start();

        // read message from client, check its validity and dispatch it (handle non
        // correct messages)
        try {
            // convert the message in a simple String
            String msg = "";

            // after a successfully request the server recieves infinite empty messages,
            // whitout this while cycle it became crazy and crashes
            while (msg.isEmpty()) {
                msg = this.readAsString();
            }

            // check validity of the message (parameters number)
            int params = (int) msg.chars().filter(c -> c == '_').count();
            if (params == 1 && msg.equals("_QUIT")) { // quit message
                // check if the message is a quit message
                this.quit();
            } else if (params < 2 || params > 6) { // if the message has the wrong number of parameters
                // else check if the message has the right number of parameters
                try {
                    this.write("FORMAT");
                } catch (Exception e) {
                    // ! Error message !
                    System.out.println(e.getMessage());
                }
            } else { // if the message is VALID, dispatch it
                this.dispatcher(msg);
            }
        } catch (ClosedChannelException e) {
            // handle if the channel is closed (and print a msg)
            this.quit();
            // * Log message *
            System.out.println("A client close the connection.");
        } catch (IOException e) {
            // set the focus on read operation
            this.callerAddress.keyFor(this.selector).interestOps(SelectionKey.OP_READ);
            // ! Error message !
            System.out.println(e.getMessage());
        }

        this.quit();
    }

    /**
     * Handles the incoming message and dispatches it to the appropriate method
     * based on the message format.
     * 
     * @param msg the incoming message to be processed
     */
    public void dispatcher(String msg) {
        // split for the "_" character
        String[] parts = msg.split("_");

        if (parts.length == 0) { // if the message is empty
            try { // empty field message
                this.write("EMPTYF");
            } catch (IOException e) {
                // ! Error message !
                System.out.println("Error during writing on socket:" + e.getMessage());
            }
        }

        try { // check if the message has the right number of parameters
            if (parts.length > 6) { // if have too many parameters
                try { // format error
                    this.write("FORMAT");
                } catch (Exception e) {
                    // ! Error message !
                    System.out.println(e.getMessage());
                }
            } else { // right number of parameters
                switch (parts[0]) { // check the type of the request and call the appropriate method
                    case "SIGNIN":
                        this.signIn(parts[0], this.callerAddress, parts[2], parts[3]);
                        break;
                    case "LOGIN":
                        this.logIn(parts[0], this.callerAddress, parts[2],
                                parts[3]);
                        break;
                    case "LOGOUT":
                        this.logOut(parts[0], this.callerAddress, parts[2]);
                        break;
                    case "HOTEL":
                        this.searchHotel(parts[0], this.callerAddress, parts[2],
                                parts[3], parts[4]);
                        break;
                    case "ALLHOTEL":
                        this.searchAllHotels(parts[0], this.callerAddress, parts[2],
                                parts[3]);
                        break;
                    case "REVIEW":
                        try { // try to insert the review
                            this.insertReview(parts[0], this.callerAddress, parts[2],
                                    parts[3], parts[4], Review.fromString(parts[5]));
                        } catch (Exception e) {
                            try {
                                this.write(e.getMessage());
                            } catch (Exception f) {
                                // ! Error message !
                                System.out.println(f.getMessage());
                            }
                        }
                        break;
                    case "BADGE":
                        this.showMyBadges(parts[0], this.callerAddress, parts[2]);
                    default:
                        try { // try to write on the socket (type error)
                            this.write("Error: invalid request type");
                        } catch (Exception e) {
                            // ! Error message !
                            System.out.println(e.getMessage());
                        }
                        break;
                }
            }
        } catch (Exception e) {
            // ! Error message !
            System.out.println("Error when dispatching request: " + e.getMessage());
        }
    }

    /**
     * Close the connection
     */
    private void quit() {
        // TODO (remove) - this.isRunning = false;
        try {
            this.server.removeHandler(this);
            this.callerAddress.keyFor(this.selector).cancel();
            this.callerAddress.close();
        } catch (IOException e) {
            // ! Error message !
            System.out.println("Error when closing the conncetion: " + e.getMessage());
        }
    }

    // CONSTRUCTORS
    public RequestHandler(UserManagement userManagement, HotelManagement hotelManagement, SocketChannel socketChannel,
            Selector selector, InetAddress udpAddr, int udpPort, long interval, Lock saverLock, HOTELIERServer server) {
        this.userManagement = userManagement;
        this.hotelManagement = hotelManagement;
        this.callerAddress = socketChannel;
        this.selector = selector;
        this.udpAddr = udpAddr;
        this.udpPort = udpPort;

        this.server = server;

        // TODO (remove) - this.dataPersistence = new DataPersistence(interval, saverLock, this.hotelManagement, this.userManagement);
        // Thread backupThread = new Thread();

        // set error messages
        this.errors.add("USERN_Y");
        this.errors.add("USERN_N");
        this.errors.add("EMPTYF");
        this.errors.add("WRONGPSW");
        this.errors.add("HOTEL");
        this.errors.add("CITY");
        this.errors.add("FORMAT");
    }

    // HANDLING METHODS

    /**
     * Signs in a user with the provided credentials.
     *
     * @param type          the type of user (e.g., "admin", "customer")
     * @param callerAddress the socket channel of the caller
     * @param username      the username of the user
     * @param psw           the password of the user
     */
    public void signIn(String type, SocketChannel callerAddress, String username, String psw) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = psw;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        try {
            userManagement.register(this.username, this.psw);
            this.write("Registration successfull.");
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (IOException f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Logs in a user with the specified type, username, and password.
     * 
     * @param type          The type of user (e.g., admin, customer).
     * @param callerAddress The socket channel of the caller.
     * @param username      The username of the user.
     * @param psw           The password of the user.
     */
    public void logIn(String type, SocketChannel callerAddress, String username, String psw) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = psw;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        try {
            userManagement.login(this.username, this.psw);
            this.write("User " + this.username + " logged in");
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (IOException f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Logs out the user with the specified username.
     * 
     * @param type          The type of user (e.g., admin, customer).
     * @param callerAddress The socket channel of the caller.
     * @param username      The username of the user.
     */
    public void logOut(String type, SocketChannel callerAddress, String username) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        try {
            userManagement.logout(this.username);
            this.write("User " + this.username + " logged out");
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (IOException f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Searches for a hotel based on the given parameters and sends the result to
     * the caller.
     *
     * @param type          the type of the request
     * @param callerAddress the address of the caller
     * @param username      the username of the caller
     * @param hotelName     the name of the hotel to search for
     * @param cityName      the name of the city to search in
     */
    public void searchHotel(String type, SocketChannel callerAddress, String username, String hotelName,
            String cityName) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.review = null;

        try {
            Hotel hotel = hotelManagement.searchHotel(this.getHotelName(), this.getCityName());
            this.write(hotel.toString());
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (Exception f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Searches for all hotels in a given city and sends the results to the client.
     * 
     * @param type          the type of request
     * @param callerAddress the address of the client making the request
     * @param username      the username of the client
     * @param cityName      the name of the city to search in
     */
    public void searchAllHotels(String type, SocketChannel callerAddress, String username, String cityName) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = cityName;
        this.review = null;

        String response = "";

        try {
            List<Hotel> hotels = hotelManagement.searchHotelByCity(this.getCityName());

            for (Hotel hotel : hotels) {
                response += (hotel.toString() + "\n-------------------\n");
            }

            this.write(response);
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (Exception f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Inserts a review for a hotel.
     * 
     * @param type          The type of the request.
     * @param callerAddress The address of the caller.
     * @param username      The username of the user.
     * @param review        The review to be inserted.
     */
    public void insertReview(String type, SocketChannel callerAddress, String username, String hotelName,
            String cityName, Review review) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.review = review;
        try {
            // TODO - temp debug print
            System.out.println("* DEBUG - \tupdating user " + username + "(current points = "
                    + this.userManagement.getUser(username).getBadge());

            // increment user experience
            this.userManagement.getUser(username).updatePoints();

            Map<String, Hotel> newBest = hotelManagement.addReview(this.getHotelName(), this.getCityName(),
                    this.getReview());

            this.write("Review added correctly.");

            if (newBest != null) {
                String newBestString = "";

                for (Hotel hotel : newBest.values()) {
                    newBestString += "\t" + hotel.getCity() + "\n" + (hotel.toString() + "\n-------------------\n");
                }

                this.sendNotification(newBestString);
            }

        } catch (IOException e) {
            // ! Error message !
            System.out.println(e.getMessage());
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (IOException f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Retrieves and sends the badge of a user to the client.
     *
     * @param type          the type of the request
     * @param callerAddress the address of the client
     * @param username      the username of the user
     */
    public void showMyBadges(String type, SocketChannel callerAddress, String username) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        try {
            String badge = this.userManagement.getUser(this.username).getBadge();
            this.write(badge);
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (Exception f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }
    }

    // COMUNICATIONS METHODS

    /**
     * Sends a notification message using UDP protocol.
     * 
     * @param msg the message to be sent
     */
    protected void sendNotification(String msg) {
        DatagramSocket udpSock = null;

        try {
            udpSock = new DatagramSocket();

            byte[] sendData = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, this.udpAddr, this.udpPort);

            udpSock.send(packet);

            // * Log message *
            System.out.println("Notifica di aggiornamento ranking locali inviata");
        } catch (Exception e) {
            // ! Error message !
            System.out.println("Error during notification sending: " + e.getMessage());
        } finally {
            if (udpSock != null) {
                udpSock.close();
            }
        }
    }

    /**
     * Reads a client message
     * 
     * @return The client message as a String
     * @throws IOException Thrown if an unexpected error occurs
     */
    protected String readAsString() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            this.buffer.clear();
            int bytesRead = this.callerAddress.read(this.buffer);
            if (bytesRead == -1)
                throw new ClosedChannelException();
            if (bytesRead == 0)
                break;
            this.buffer.flip();
            byte[] responseBytes = new byte[this.buffer.remaining()];
            this.buffer.get(responseBytes);
            stringBuilder.append(new String(responseBytes, StandardCharsets.UTF_8));
        }
        String message = stringBuilder.toString();

        return message;
    }

    /**
     * Writes the given message on the socket
     * 
     * @param message The message to be sent
     * @throws IOException Thrown if an unexpected error occurs
     */
    protected void write(String message) throws IOException {
        // open a selector
        Selector mySelector = Selector.open();
        // register the callerAddress with the selector
        callerAddress.register(mySelector, SelectionKey.OP_WRITE);
        // wait for the callerAddress to be ready for writing
        while (mySelector.select() == 0) {
        }

        // iterate over the keys
        Set<SelectionKey> keys = mySelector.selectedKeys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        // build and send the message
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int numChunks = (int) Math.ceil((double) messageBytes.length / 1024);
        for (int i = 0; i < numChunks; i++) {
            int start = i * 1024;
            int end = Math.min(start + 1024, messageBytes.length);
            buffer.clear();
            // put the message bytes in the buffer
            buffer.put(Arrays.copyOfRange(messageBytes, start, end));
            // flip the buffer
            buffer.flip();
            while (buffer.hasRemaining()) {
                // write the buffer to the callerAddress and print the number of byte
                this.callerAddress.write(buffer);
            }
        }
        mySelector.close();
    }

    // GETTER AND SETTER METHODS

    /**
     * @return type parameter
     */
    public String getType() {
        return this.type;
    }

    /**
     * @type you want to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return callerAddress parameter
     */
    public SocketChannel getCallerAddres() {
        return this.callerAddress;
    }

    /**
     * @callerAddress you want to set
     */
    public void setCallerAddress(SocketChannel callerAddress) {
        this.callerAddress = callerAddress;
    }

    /**
     * @return username parameter
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @username you want to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return psw parameter
     */
    public String getPsw() {
        return this.psw;
    }

    /**
     * @psw you want to set
     */
    public void setPsw(String psw) {
        this.psw = psw;
    }

    /**
     * @return hotelName parameter
     */
    public String getHotelName() {
        return this.hotelName;
    }

    /**
     * @hotelName you want to set
     */
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    /**
     * @return cityName parameter
     */
    public String getCityName() {
        return this.cityName;
    }

    /**
     * @cityName you want to set
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * @return review parameter
     */
    public Review getReview() {
        return this.review;
    }

    /**
     * @review you want to set
     */
    public void setReview(Review review) {
        this.review = review;
    }
}