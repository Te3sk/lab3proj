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

// ! OPERATION TYPES
// SIGNIN - LOGIN - LOGOUT - HOTEL - ALLHOTEL - REVIEW - BADGE
// register - SIGNIN
// login - LOGIN
// logout - LOGOUT
// searchHotel - HOTEL
// searchAllHotels - ALLHOTEL
// insertReview - REVIEW
// showMyBadges - BADGE
// QUIT

// ! RESPONSE TYPES
// success - *success string*
// existing username - USERN_Y
// non existing username - USERN_N
// empty fields - EMPTYF
// wrong password - WRONGPSW
// non existing hotel - HOTEL
// non existing city - CITY

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
    private Boolean isRunning = true;

    private UserManagement userManagement;
    private HotelManagement hotelManagement;

    // msg format
    // "_type:value_username:value_param1:StringParam1_param2:String|ReviewParam2_param3:StringParam3"
    // The number of param matter. number of param = number of '_'
    // can be only "_QUIT"
    @Override
    public void run() {
        // TODO - temp debug print
        System.out.println("* DEBUG\tREQUEST HANDLER RUN METHOD");
        while(isRunning){try {
            // convert the message in a simple String
            String msg = this.readAsString();

            // TODO - temp debug print
            System.out.println("* DEBUG\tnew message received: " + msg + " *");

            // check validity of the message (parameters number)
            int params = (int) msg.chars().filter(c -> c == '_').count();
            if (params == 1 && msg.equals("_QUIT")) {
                // TODO - check this quit msg (double check), maybe it's not necessary
                // check if the message is a quit message
                this.quit();
            } else if (params < 3 || params > 6) {
                // else check if the message has the right number of parameters
                try {
                    this.write("Error: invalid message format");
                } catch (Exception e) {
                    // ! Error message !
                    System.out.println(e.getMessage());
                }
            } else {
                // if the message is VALID, dispatch it
                // TODO - temp debug print
                System.out.println("* DEBUG - \tmessage ok, dispatching... *");
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
        }}

        // TODO - temp debug print
        System.out.println("* DEBUG - \tFINE DI QUESTO RUN METHOD\n--------------------------");
    }

    /**
     * Handles the incoming message and dispatches it to the appropriate method
     * based on the message format.
     * 
     * @param msg the incoming message to be processed
     */
    public void dispatcher(String msg) {
        // TODO - temp debug print
        System.out.println("* DEBUG - \tDISPATCHER...");
        String[] parts = msg.split("_");
        // TODO - temp debug print
        System.out.println("* DEBUG - \tPARTS PARTS of message (" + msg + "):");
        if (parts.length == 0) {
            System.out.println("* DEBUG - \tNO PARTS");
        } else {
            for (String part : parts) {
                System.out.println("\t* DEBUG - \t" + part);
            }
        }

        // TODO - check this quit msg (double check), maybe it's not necessary
        // check if the message is a quit message (again)
        if (parts.length == 1) {
            this.quit();
        }

        // TODO - temp debug print
        System.out.println("* DEBUG - \tparts size: " + (parts.length));

        // check if the message has the right number of parameters
        try {
            if (parts.length >= 6) {
                // TODO - temp debug print
                System.out.println("* DEBUG - \tinvalid number of parameters (" + parts.length + ")");
                try {
                    this.write("Error: invalid message format (too many parameters: " + parts.length + ")");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                // TODO - temp debug print
                System.out.println("* DEBUG - \tright number of parameters, type: " + parts[0] + " *");
                // check the type of the request and call the appropriate method

                switch (parts[0]) {
                    case "SIGNIN":
                        // TODO - temp debug print
                        System.out.println("--------------------------\n* DEBUG - \tSIGNIN CASE *\n\t" + msg);
                        System.out.println("\t" + parts.length + " parameters");
                        for (int i = 0; i < parts.length; i++) {
                            System.out.println("\tparameter " + Integer.toString(i) + "- " + parts[i]);
                        }
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
                        try {
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
                        try {
                            this.write("Error: invalid request type");
                        } catch (Exception e) {
                            // ! Error message !
                            System.out.println(e.getMessage());
                        }
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
        this.isRunning = false;
        try {
            // this.server.removeHandler(this)
            this.callerAddress.keyFor(this.selector).cancel();
            this.callerAddress.close();
        } catch (IOException e) {
            // ! Error message !
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
        // TODO - temp debug print
        System.out.println("* DEBUG\tSIGNIN METHOD (RequestHandler) *");

        // TODO - temp debug print
        System.out.println("* DEBUG - \ttype: " + type + " *");
        this.type = type;
        // TODO - temp debug print
        System.out.println("* DEBUG - \tcallerAddress: " + callerAddress + " *");
        this.callerAddress = callerAddress;
        // TODO - temp debug print
        System.out.println("* DEBUG - \tusername: " + username + " *");
        this.username = username;
        // TODO - temp debug print
        System.out.println("* DEBUG - \tpsw: " + psw + " *");
        this.psw = psw;
        this.hotelName = null;
        this.cityName = null;
        this.review = null;

        try {
            userManagement.register(this.username, this.psw);
            // TODO - temp debug print
            System.out.println("* DEBUG\tsending response to the client...");
            this.write("Registration successfull.");
            System.out.println("User " + this.username + " registered successfully.");
        } catch (Exception e) {
            try {
                this.write(e.getMessage());
            } catch (IOException f) {
                // ! Error message !
                System.out.println(f.getMessage());
            }
        }

        // TODO - temp debug print
        System.out.println("* DEBUG - \tEXIT FROM SIGNIN METHOD");
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

        try {
            List<Hotel> hotels = hotelManagement.searchHotelByCity(this.getCityName());
            for (Hotel hotel : hotels) {
                this.write(hotel.toString() + "\n-------------------\n");
            }
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
            hotelManagement.addReview(this.getHotelName(), this.getCityName(), this.getReview());
            this.write("Review added correctly.");
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
                int byteNumber = this.callerAddress.write(buffer);
                // TODO - temp debug print
                System.out.println("* DEBUG - \t" + byteNumber + " bytes written *");
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
