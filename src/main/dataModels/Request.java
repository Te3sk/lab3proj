package main.dataModels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import main.server.HotelManagement;
import main.server.UserManagement;

// register - SIGNIN
// login - LOGIN
// logout - LOGOUT
// searchHotel - HOTEL
// searchAllHotels - ALLHOTEL
// insertReview - REVIEW
// showMyBadges - BADGE

// todo - read from socket reader method in server 
/*
 *     protected String processMessage (String json) throws IOException, ProtocolException {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        String op;
        if (reader.nextName().equals("operation")) {
            op = reader.nextString();
            switch (op) {
                case "register":
                    this.register(reader);
                    break;

                case "login":
                    this.login(reader);
                    break;

                case "search":
                    this.singleSearch(reader);
                    break;

                case "searchAll":
                    this.mulSearch(reader);
                    break;

                case "review":
                    this.newReview(reader);
                    break;

                case "badge":
                    this.getBadge();
                    break;

                case "logout":
                    this.logout();
                    break;

                case "quit":
                    this.quit();
                    break;

                default: 
                    reader.close();
                    throw new ProtocolException();
            }
        } else {
            reader.close();
            throw new ProtocolException();
        }
        reader.endObject();
        reader.close();
        return op;
    }
 */

public class Request {
    private String type;
    private SocketChannel callerAddress;
    private String username;
    private String psw;
    private String hotelName;
    private String cityName;
    private Review review;

    private ByteBuffer buffer;
    private Selector selector;

    private UserManagement userManagement;
    private HotelManagement hotelManagement;

    // CONSTRUCTORS

    /**
     * review constructor
     * 
     * @param type          REVIEW
     * @param callerAddress socketChannel address of the caller
     * @param username      username of the caller
     * @param review        review object filled by the user
     */
    public Request(String type, SocketChannel callerAddress, String username, Review review) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = null;
        this.review = review;

        // todo - this.giveReview();
    }

    /**
     * searchHotel constructor
     * 
     * @param type          HOTEL
     * @param callerAddress socketChannel address of the caller
     * @param username      username of the caller
     * @param hotelName     name of the hotel to search
     * @param cityName      city of the hotel to search
     */
    public Request(String type, SocketChannel callerAddress, String username, String hotelName, String cityName) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.review = null;

        // todo - this.searchHotel();
    }

    /**
     * logout constructor
     * 
     * @param type          LOGOUT
     * @param callerAddress socketChannel address of the caller
     * @param username      username of the caller
     */
    public Request(String type, SocketChannel callerAddress, String username) {
        this.type = type;
        this.callerAddress = callerAddress;
        this.username = username;
        this.psw = null;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        // todo - this.logout();
    }

    /**
     * register(1), login(2), searchAllHotels(3) constructor
     * 
     * @param type          SIGNIN, LOGIN
     * @param callerAddress socketChannel address of the caller
     * @param username      username of the caller
     * @param param1        psw of the caller(1,2) OR name of the city where
     *                      search(3)
     */
    public Request(String type, SocketChannel callerAddress, String username, String param1) throws Exception {
        if (type == "SIGNIN" || type == "LOGIN") {
            this.type = type;
            this.callerAddress = callerAddress;
            this.username = username;
            this.psw = param1;
            this.hotelName = null;
            this.cityName = null;
            this.review = null;

            if (type == "SIGNIN") {
                this.signIn();
            } else {
                // todo - this.login();
            }
        } else if (type == "ALLHOTEL") {
            this.type = type;
            this.callerAddress = callerAddress;
            this.username = username;
            this.psw = null;
            this.hotelName = null;
            this.cityName = param1;
            this.review = null;

            // todo - this.searchAll
        } else {
            throw new Exception("Error during request dispatching: " + type + " invalid");
        }
    }

    // HANDLING METHODS

    /**
     * signin method: register the new user in the db, handle exceptions
     */
    public void signIn() {
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

    public void logIn(){
        try {
            userManagement.login(this.username, this.psw);
            this.write("User " + this.username + " logged in");
        } catch (Exception e) {
            try{
                this.write(e.getMessage());
            } catch (IOException f) {
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
