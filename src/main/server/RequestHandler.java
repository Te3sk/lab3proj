package main.server;

import java.nio.channels.SocketChannel;

import main.dataModels.*;

// register - SIGNIN
// login - LOGIN
// logout - LOGOUT
// searchHotel - HOTEL
// searchAllHotels - ALLHOTEL
// insertReview - REVIEW
// showMyBadges - BADGE

public class RequestHandler {
    private UserManagement userManagement;
    private HotelManagement hotelManagement;
    private 

    /** empty constructor */
    public RequestHandler () {}

    public void dispatcher(Request request){
        switch(request.getType()) {
            // todo - cases
        }
    }

    public String signIn(SocketChannel callerAddress, String username, String psw) {
        try{
            Request request = new Request("SIGNIN", callerAddress, username, psw);
        } catch (Exception e) {
            
        }

        return null;
    }
}
