package main.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import main.dataModels.Hotel;
import main.dataModels.Review;

// SIGNIN - LOGIN - LOGOUT - HOTEL - ALLHOTEL - REVIEW - BADGE
// register - SIGNIN
// login - LOGIN
// logout - LOGOUT
// searchHotel - HOTEL
// searchAllHotels - ALLHOTEL
// insertReview - REVIEW
// showMyBadges - BADGE
// QUIT

// todo - read from socket reader method in server 

public class RequestHandler implements Runnable {
    private String type;
    private SocketChannel callerAddress;
    private String username;
    private String psw;
    private String hotelName;
    private String cityName;
    private Review review;

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;

    private UserManagement userManagement;
    private HotelManagement hotelManagement;

    // msg format
    // "_type:value_username:value_param1:StringParam1_param2:String|ReviewParam2_param3:StringParam3"
    // The number of param matter. number of param = number of '_'
    // can be only "_QUIT"
    @Override
    public void run() {
        try {
            // convert the message in a simple String
            String msg = this.readAsString();
            // check validity of the message (parameters number)
            int params = (int) msg.chars().filter(c -> c == '_').count();
            if (params == 1 && msg.equals("_QUIT")) {
                this.quit();
            } else if (params < 3 || params > 6) {
                try {
                    this.write("Error: invalid message format");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                this.dispatcher(msg);
            }

        } catch (ClosedChannelException e) {
            // handle if the channel is closed (and print a msg)
            this.quit();
            System.out.println("A client close the connection.");
        } catch (IOException e) {
            // set the focus on read operation
            this.callerAddress.keyFor(this.selector).interestOps(SelectionKey.OP_READ);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the incoming message and dispatches it to the appropriate method based on the message format.
     * 
     * @param msg the incoming message to be processed
     */
    public void dispatcher(String msg) {
        String[] parts = msg.split("_");
        if (parts.length == 1) {
            this.quit();
        }
        if (parts.length > 4) {
            try {
                this.write("Error: invalid message format");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            // 

            // SIGNIN - LOGIN - LOGOUT - HOTEL - ALLHOTEL - REVIEW - BADGE
            switch (parts[0].split(":")[1]) {
                case "SIGNIN":
                    this.signIn(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1], parts[2].split(":")[1]);
                case "LOGIN":
                    this.logIn(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1], parts[2].split(":")[1]);
                case "LOGOUT":
                    this.logOut(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1]);
                case "HOTEL":
                    this.searchHotel(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1],
                            parts[2].split(":")[1], parts[3].split(":")[1]);
                case "ALLHOTEL":
                    this.searchAllHotels(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1],
                            parts[2].split(":")[1]);
                case "REVIEW":
                    try {
                        this.insertReview(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1],
                                Review.fromString(parts[2].split(":")[1]));
                    } catch (Exception e) {
                        try {
                            this.write(e.getMessage());
                        } catch (Exception f) {
                            System.out.println(f.getMessage());
                        }
                    }
                case "BADGE":
                    this.showMyBadges(parts[0].split(":")[1], this.callerAddress, parts[1].split(":")[1]);
                default:
                    try {
                        this.write("Error: invalid request type");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
            }
        }
    }

    /**
     * Close the connection
     */
    private void quit() {
        try {
            // this.server.removeHandler(this)
            this.callerAddress.keyFor(this.selector).cancel();
            this.callerAddress.close();
        } catch (IOException e) {
            System.out.println("Error when closing the conncetion: " + e.getMessage());
        }
    }

    // CONSTRUCTORS
    public RequestHandler(UserManagement userManagement, HotelManagement hotelManagement, SocketChannel socketChannel,
            Selector selector) {
        this.userManagement = userManagement;
        this.hotelManagement = hotelManagement;
        this.callerAddress = socketChannel;
        this.selector = selector;
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

        try {
            List<Hotel> hotels = hotelManagement.searchHotelByCity(this.getCityName());
            for (Hotel hotel : hotels) {
                this.write(hotel.toString() + "\n-------------------\n");
            }
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (Exception f) {
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
    public void insertReview(String type, SocketChannel callerAddress, String username, Review review) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = null;
        this.review = review;

        hotelManagement.addReview(this.getHotelName(), this.getCityName(), this.getReview());
        try {
            this.write("Review added correctly.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
                System.out.println(f.getMessage());
            }
        }
    }

    // COMUNICATIONS METHODS

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
        System.out.println("New message from " + this.callerAddress.getRemoteAddress().toString() + ": " + message);
        return message;
    }

    /**
     * Writes the given message on the socket
     * 
     * @param message The message to be sent
     * @throws IOException Thrown if an unexpected error occurs
     */
    protected void write(String message) throws IOException {
        Selector mySelector = Selector.open();
        callerAddress.register(mySelector, SelectionKey.OP_WRITE);
        while (mySelector.select() == 0) {
        }
        Set<SelectionKey> keys = mySelector.selectedKeys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int numChunks = (int) Math.ceil((double) messageBytes.length / 1024);
        for (int i = 0; i < numChunks; i++) {
            int start = i * 1024;
            int end = Math.min(start + 1024, messageBytes.length);
            buffer.clear();
            buffer.put(Arrays.copyOfRange(messageBytes, start, end));
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.println(this.callerAddress.write(buffer));
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
