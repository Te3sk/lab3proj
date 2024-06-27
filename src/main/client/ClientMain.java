// compile: javac -d bin -cp lib/* src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java
// run: java -cp "bin;lib/*" main.client.ClientMain
// complete : javac -d bin -cp lib/* src/main/dataModels/*.java src/main/server/*.java src/main/client/*.java && java -cp "bin;lib/*" main.client.ClientMain

// additional param: -Xlint:-deprecation to disable deprecation warnings

package main.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ClientMain {
    public static void main (String[] args) {
        // take args (from a config file)
        // try(InputStream input = Files.newInputStream(Paths.get("../../../config/clientConfig.properties"))) {
        try(InputStream input = Files.newInputStream(Paths.get("config/clientConfig.properties"))) {
        Properties properties = new Properties();
        // load properties from conf file
        properties.load(input);    
        InetAddress tcpAddr = InetAddress.getByName(properties.getProperty("client.tcp.ip"));
        InetAddress udpAddr = InetAddress.getByName(properties.getProperty("client.udp.ip"));
        Integer tcpPort = Integer.parseInt(properties.getProperty("client.tcp.port"));
        Integer udpPort = Integer.parseInt(properties.getProperty("client.udp.port"));

        // todo - temp
        System.out.println("tcpAddr: " + tcpAddr);
        System.out.println("tcpPort: " + tcpPort);
        System.out.println("udpAddr: " + udpAddr);
        System.out.println("udpPort: " + udpPort);
        
        // start the client instance
        HOTELIERCustomerClient client = new HOTELIERCustomerClient(tcpAddr, udpAddr, tcpPort, udpPort);
        System.out.println("start the client...");
        client.start();            
        } catch (UnknownHostException e) {
            System.out.println("Error, invalid ip address: " + e);
        } catch (IOException e) {
            System.out.println("Error, IO issue: " + e);
        } catch (Exception e) {
            System.out.println("Error during client start: " + e);
        }
    }
}
