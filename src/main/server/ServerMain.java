package main.server;


import java.util.Properties;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerMain {
    /**
     * take parameters from config file and start the server
     * @param args
     */
    public static void main (String args[]) {
        // try-with-resources block to ensure the input stream is closed after use
        try(InputStream input = Files.newInputStream(Paths.get("../../../config/serverConfig.properties"))) {
            Properties properties = new Properties();
            // load properties from conf file
            properties.load(input);
            // retrive and parse configuration arguments
            InetAddress tcpAddr = InetAddress.getByName(properties.getProperty("server.tcp.ip"));
            InetAddress updAddr = InetAddress.getByName(properties.getProperty("server.udp.ip"));
            Integer tcpPort = Integer.parseInt(properties.getProperty("server.udp.port"));
            Long interval = Long.parseLong(properties.getProperty("update.interval"));
            Integer udpPort = Integer.parseInt(properties.getProperty("server.udp.port"));
            String hotelFile = properties.getProperty("hotel.file.path");
            String userFile = properties.getProperty("user.file.path");

            // create a new server instance
            HOTELIERServer server = new HOTELIERServer(tcpAddr, updAddr, tcpPort, interval, udpPort, hotelFile, userFile);

            // start the server
            server.start();
            System.out.println("Server is running...");
        } catch (Exception e) {
            System.out.println("Error during server start: " + e);
        }
    }
}
