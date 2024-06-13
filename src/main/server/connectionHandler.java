// package main.server;

// import java.io.IOException;
// import java.io.StringReader;
// import java.net.ProtocolException;
// import java.nio.ByteBuffer;
// import java.nio.channels.ClosedChannelException;
// import java.nio.channels.SelectionKey;
// import java.nio.channels.Selector;
// import java.nio.channels.SocketChannel;
// import java.nio.charset.StandardCharsets;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import com.google.gson.stream.JsonReader;

// import main.dataModels.Hotel;
// import main.dataModels.Review;

// // public class ConnectionHandler implements Runnable {
// //     private HotelHandler hotelHandler;
// //     private UserHandler userHandler;
// //     private SocketChannel connection;
// //     private String username = null;
// //     private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
// //     private Selector selector;
// //     private Server server;

// public class connectionHandler implements Runnable{
//     private SocketChannel socketChannel;
//     private ByteBuffer buffer;
//     private Selector selector;
//     private UserManagement userManagement;
//     private HotelManagement hotelManagement;
//     private String username = null;
    
//     /**
//     * @return socketChannel parameter
//     */
//     public SocketChannel getSocketChannel (){
//         return this.socketChannel;
//     }

//     @Override
//     public void run(){
//         try {
//             String msg = this.readAsString();
//             String op = this.processMsg(msg);
//             // If the operation is quit then the socketChannel is closed.
//             if(!op.equals("quit")){
//                 this.socketChannel.keyFor(this.selector).interestOps(SelectionKey.OP_READ);
//             }
//             System.out.println("done");
//         } catch (ClosedChannelException e) {
//             // todo - quit method
//             // this.quit();
//             System.out.println("a client quit");
//         } catch (IOException e) {
//             this.socketChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
//             e.printStackTrace();
//         }
//     }

//     /**
//      * Reads data from the connection channel, print it and return it as a string
//      * @return the string readed from the connection
//      * @throws IOException if an I/O error occours
//      */
//     protected String readAsString() throws IOException {
//         // create a StringBuilder to store the read data
//         StringBuilder stringBuilder = new StringBuilder();
//         // initialize the buffer with a specific capacity
//         // todo - understand how big 
//         buffer = ByteBuffer.allocate(1924);

//         // Continuosly read data from the connection util no more data is avaiabile
//         // todo - try-catch statemente? in or out the while?
//         while (true) {
//             // clear the buffer to prepare for reading
//             buffer.clear();
//             // read data from the connection into the buffer
//             int bytesRead = this.socketChannel.read(buffer);
            
//             // if the end of the stream is reached, throw a ClosedChannelException
//             if (bytesRead == -1) throw new ClosedChannelException();
//             // if no data was read, break the loop
//             if (bytesRead == 0) break;

//             // prepare the buffer for reading bu flipping it
//             buffer.flip();

//             // create a byte array to hold the read data
//             byte[] responseBytes = new byte[buffer.remaining()];
//             // read data from the buffer into the byte array
//             buffer.get(responseBytes);

//             // convert the byte to a string using UTF-8 encoding and append it to the StringBuilder
//             stringBuilder.append(new String(responseBytes, StandardCharsets.UTF_8));
//         }
        
//         // convert the stringBuilder to a string
//         String msg = stringBuilder.toString();

//         // todo - ? 
//         // print the received msg and its source address
//         System.out.println("New message from " + this.socketChannel.getRemoteAddress().toString() + ":\n\t" + msg);

//         return msg;
//     }

//     /**
//      * writes the given message on the socket
//      * 
//      * @param msg the message to be sent
//      * @throws IOException throw if an I/O error occours
//      */
//     protected void write(String msg) throws IOException {
//         // open a new selector
//         Selector mySelector = Selector.open();

//         // register the connection with the selector for write operation
//         socketChannel.register(mySelector, SelectionKey.OP_WRITE);

//         // wait until the selector is ready for I/O operations
//         while(mySelector.select() == 0) {}

//         // get the selected keys from the selector
//         Set<SelectionKey> keys = mySelector.selectedKeys();

//         // iterate over the selected keys and remove them from the set
//         Iterator<SelectionKey> iterator = keys.iterator();
//         while(iterator.hasNext()) {
//             iterator.next();
//             iterator.remove();
//         }

//         byte[] msgByte = msg.getBytes(StandardCharsets.UTF_8);

//         // calculate the number of chunks needed to send the msg
//         int numChunks = (int) Math.ceil((double) msgByte.length / 1024);

//         // iterate over the msg chunks and send
//         for (int i = 0; i < numChunks; i++) {
//             int start = i * 1024;
//             int end = Math.min(start + 1924, msgByte.length);
//             // clear the buffer and put the msg chunk in it
//             buffer.clear();
//             buffer.put(msgByte, start, end);
//             // flip the buffer
//             buffer.flip();
//             // write the buffer to the connection
//             while(buffer.hasRemaining()) {
//                 System.out.println("write " + this.socketChannel.write(buffer) + " byte");
//             }
//         }

//         // close the selector
//         mySelector.close();
//     }
    
//     /**
//      * Processes a msg received in JSON format
//      * 
//      * @param json json the json obj to process
//      * @return the operation specified in the msg
//      * @throws IOException throw if an I/O error occours
//      * @throws ProtocolException throw if the received msg is not in JSON format
//      */
//     protected String processMsg(String json) throws IOException, ProtocolException {
//         // create a jsonreader to read the json string from the input string
//         JsonReader reader = new JsonReader(new StringReader(json));
//         // begin reading the json object
//         reader.beginObject();

//         String op;
//         // check if the next name in the json obj is "op"
//         if (reader.nextName().equals("op")) {
//             // if "op" is found, read the next string as the op
//             op = reader.nextString();
//             // based on the op, perform corrisponding action
//             switch (op) {
//                 case "signing":
//                     // todo - dev method                    
//                     break;

//                 case "login":
//                     // todo - dev method                    
//                     break;
                
//                 case "search":
//                     // todo - dev method                    
//                     break;

//                 case "searchAll":
//                     // todo - dev method                    
//                     break;

//                 case "review":
//                     // todo - dev method                    
//                     break;

//                 case "badge":
//                     // todo - dev method                    
//                     break;

//                 case "logout":
//                     // todo - dev method                    
//                     break;

//                 case "quit":
//                     // todo - dev method                    
//                     break;

//                 default:
//                     // if the op is not found, close the reader and throw a ProtocolException
//                     reader.close();
//                     throw new ProtocolException("Missing 'operationì' field in the recieved message.");
//             }
//         } else {
//             // if "op" is not found, close the reader and throw a ProtocolException
//             reader.close();
//             throw new ProtocolException("Missing 'operationì' field in the recieved message.");
//         }

//         // end reading the JSON obj
//         reader.endObject();
//         // close the reader
//         reader.close();
//         // return the op specified in the msg
//         return op;
//     }

//     /**
//      * deserialize JSON data from the JsonReader based on the provided nameList
//      * 
//      * @param nameList an array of string representing the expected names of json properties
//      * @param reader the JsonReader obj containing the JSON data
//      * @return an array of strings containing the deserialized values corresponding to the names in nameList
//      * @throws ProtocolException throw if the JSON data is not in the expected format
//      * @throws IOException throw if an I/O error occours
//      */
//     protected String[] getDeserialized (String[] nameList, JsonReader reader)
//         throws ProtocolException, IOException {
//         // create an array to store the deserialized values
//         String[] res = new String[nameList.length];
//         // iterate over the expected names in nameList
//         for (int i = 0; i < nameList.length; i++) {
//             // check if the next name in the json data matches the expected one
//             if(!reader.nextName().equals(nameList[i])) {
//                 throw new ProtocolException("Unexpected JSON property name: " + reader.nextName());
//             }
//             // read the next string value from the JsonReader and store it in the result array
//             res[i] = reader.nextString();
//         }

//         // check if there are more elements remaining in the JSON data
//         if (reader.hasNext()) {
//             throw new ProtocolException("Unexpected additional JSON properties.");
//         }
        
//         return res;
//     }
    
//     /**
//      * Registers a user with the provided username and password.
//      * 
//      * @param reader The JSON reader used to read the username and password from the client.
//      * @throws IOException If an I/O error occurs while reading from or writing to the client.
//      */
//     protected void register (JsonReader reader) throws IOException {
//         try {
//             // deserialize username and psw from JSON file
//             String[] values = this.getDeserialized(new String[] {"username", "psw"}, reader);
//             // register the user with the provided username and password
//             this.userManagement.register(values[0], values[1]);
//             // send a message to the client indicating that the registration was successful
//             this.write("correct username and psw");
//             // set the username for this connection
//             this.username = values[0];
//         } catch (ProtocolException e) {
//             this.write("PROTOCOL EXCPETION: " + e.getMessage());
//         }
//         // TODO - username and pwd exception
//     }

//     /**
//      * Handles the login process for the client.
//      * 
//      * @param reader The JSON reader used to read the login information from the client.
//      * @throws IOException If an I/O error occurs while reading or writing data.
//      */
//     protected void login (JsonReader reader) throws IOException {
//         try{
//             // deserialize the username and password from the JSON data
//             String[] values = this.getDeserialized(new String[] {"username", "psw"}, reader);
//             // login the user with the provided username and password
//             this.userManagement.login(values[0], values[1]);
//             // send a message to the client indicating that the login was successful
//             this.write("correct username and psw");
//             // set the username for this connection
//             this.username = values[0];
//         } catch (ProtocolException e) {
//             this.write("PROTOCOL EXCPETION: " + e.getMessage());
//         }
//         // todo - no match, username and pwd exception
//     }

//     /**
//      * Performs a single search for a hotel based on the provided name and city.
//      * 
//      * @param reader The JsonReader object used to read the input data.
//      * @throws IOException If an I/O error occurs while reading the input data.
//      */
//     protected void singleSearch (JsonReader reader) throws IOException {
//         try {
//             // deserialize the name and city from the JSON data
//             String[] values = this.getDeserialized(new String[] {"name", "city"}, reader);
//             // search for a hotel with the specified name and city
//             // todo - set right "throw" in HotelManagement when write the exception below
//             Hotel hotel = this.hotelManagement.searchHotel(values[0], values[1]);
//             if (hotel == null) {
//                 // if the hotel is not found, send a message to the client indicating that the hotel was not found
//                 this.write("Hotel not found");
//             } else {
//                 // todo - hotel.toString()
//                 this.write(hotel.toString());
//             }
//         } catch (ProtocolException e) {
//             this.write("PROTOCOL EXCPETION: " + e.getMessage());
//         }
//         // todo - no match, hotel or city not found exception
//     }

//     /**
//      * Performs a multiple search for hotels based on the specified city.
//      * 
//      * @param reader The JSON reader used to deserialize the request.
//      * @throws IOException If an I/O error occurs while reading the request.
//      */
//     protected void mulSearch (JsonReader reader) throws IOException {
//         try{
//             // deserialize the city from the JSON data
//             String city = this.getDeserialized(new String[] {"city"}, reader)[0];
//             // search for hotels in the specified city
//             // todo - set right "throw" in HotelManagement when write the exception below
//             List<Hotel> hotels = this.hotelManagement.searchHotelByCity(city);
//             if (hotels == null) {
//                 // if no hotels are found, send a message to the client indicating that no hotels were found
//                 this.write("No hotels found in the specified city");
//             }
//         } catch (ProtocolException e) {
//             this.write("PROTOCOL EXCPETION: " + e.getMessage());
//         }
//         // todo - no match, city not found exception
//     }

//     /**
//      * Logs out the user from the server.
//      * If the user is not logged in, sends a message to the client indicating that the user is not logged in.
//      * After logging out, sets the username to null and sends a message to the client indicating successful logout.
//      *
//      * @throws IOException if an I/O error occurs while writing to the client
//      */
//     protected void logout () throws IOException {
//         // todo - check if the user is logged in (understand username value if he is not logged in)
//         if(this.username == null) {
//             // if the user is not logged in, send a message to the client indicating that the user is not logged in
//             this.write(username + " is not logged in");
//         }
//         this.username = null;
//         this.write(username + " log out successfully");
//     }

//     // deserializeReview
//     // todo - add invalid review, absent city excp
//     protected void deserializedReview (JsonReader reader) throws ProtocolException, IOException {
//         String name;
//         String city;
//         int globalScore;
//         Map<String, Integer> singleScores = new HashMap<>();
//         if (reader.nextName().equals("name")) {
//             name = reader.nextString();
//         } else {
//             throw new ProtocolException("Missing 'name' field in the recieved message.");
//         }
//         if (reader.nextName().equals("city")) {
//             city = reader.nextString();
//         } else {
//             throw new ProtocolException("Missing 'city' field in the recieved message.");
//         }
//         if (reader.nextName().equals("globalScore")) {
//             globalScore = reader.nextInt();
//         } else {
//             throw new ProtocolException("Missing 'globalScore' field in the recieved message.");
//         }
//         if (reader.nextName().equals("singleScores")) {
//             reader.beginArray();
//             while (reader.hasNext()) {
                
//             }
//             reader.endArray();
//         } else {
//             throw new ProtocolException("Missing 'singleScores' field in the recieved message.");
//         }
//         if (reader.hasNext()){
//             throw new ProtocolException("Unexpected additional JSON properties.");
//         }

//         Review review = new Review(globalScore, singleScores);

//         this.hotelManagement.addReview(name, city, review);
//     }

//     // newReview
    
//     // getBadge
    
//     // quit
    
//     // run
// }
