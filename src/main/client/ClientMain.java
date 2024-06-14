// compile: javac -d bin -cp lib/* src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java
// run: java -cp "bin;lib/*" main.client.ClientMain
// complete : javac -d bin -cp lib/* src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java && java -cp "bin;lib/*" main.client.ClientMain

package main.client;

public class ClientMain {
    public static void main (String[] args) {
        try {
            // take args (from a config file)
    
            // crating a client instance
            HOTELIERCustomerClient client = new HOTELIERCustomerClient();

            // start the instance 
            client.start();
        } catch (Exception e) {
            System.out.println("Error during client start: " + e.getMessage());
        }
    }
}
