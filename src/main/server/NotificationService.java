package main.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.TimerTask;


public class NotificationService extends TimerTask{
    private HotelManagement hotelManagement;
    private MulticastSocket notificationSocket;
    private InetAddress addr;
    private int port;

    /**
     * Constructor
     * 
     * @param hotelManagement the current HotelManagement
     * @param addr address to stream to 
     * @param port port to stream to
     * @throws IOException if unexpected error
     */
    public NotificationService (HotelManagement hotelManagement, InetAddress addr, int port) throws IOException {
        this.hotelManagement = hotelManagement;
        this.notificationSocket = new MulticastSocket();
        this.addr = addr;
        this.port = port;
    }

    /**
     * running action when the time interval expires
     * send a notification to all logged user if the ranking changed 
     */
    @Override
    public void run () {
        try {
            System.out.println("Updating rankings...");
            Map<String, String> temp1 = this.hotelManagement.firstLocalHotels();
            this.hotelManagement.updateRanking();
            Map<String, String> temp2 = this.hotelManagement.firstLocalHotels();
            for (String city : temp1.keySet()) {
                if(temp1.get(city) == temp2.get(city)) {
                    temp2.remove(city);
                }
            }
            if(!temp2.isEmpty()) {
                System.out.println("Notify update...");
                String msgtt = Integer.toString(temp2.size()) + " new first place in local rankings";
                DatagramPacket packet = new DatagramPacket(msgtt.getBytes(), (msgtt.getBytes()).length, this.addr, this.port);
                this.notificationSocket.send(packet);
                for (String city : temp2.keySet()) {
                    // city : new first place in local ranking
                    String msg = "\t" + city + " : " + (this.hotelManagement.getHotels()).get(temp2.get(city));
                    byte[] buffer = msg.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, this.addr, this.port);
                    this.notificationSocket.send(packet);
                }
                System.out.println("Notification done.");
            } else {
                System.out.println("No new update");
            } 
        } catch (Exception e) {
            System.out.println("Error during notification service: " + e);
        }
    }

    public void close () {
        this.cancel();
        this.notificationSocket.close();
    }
}
