package main.client;

import java.util.Map;

public class HOTELIERCustomerClient {
    /**
     * start the client and the CLI
     * 
     */
    void start() {
        // todo - HOTELIERServer.start
        // 
    }

    /**
     * register a new user
     * 
     * @param username
     * @param psw
     */
    void register(String username, String psw) {
        // todo - HOTELIERServer.register
    }

    /**
     * do the login of an existing user
     * 
     * @param username
     * @param psw
     */
    void login(String username, String psw) {
        // todo - HOTELIERServer.login
    }

    /**
     * do the logout of the user
     * 
     * @param username
     */
    void logout(String username) {
        // todo - HOTELIERServer.logout
    }

    /**
     * search an hotel by name and city
     * !? search only by name ??????
     * 
     * @param nomeHotel
     * @param città
     */
    void searchHotel(String nomeHotel, String città) {
        // todo - HOTELIERServer.searchHotel
    }

    /**
     * search all the hotel in a city, ordered by ranking
     * 
     * @param città 
     */
    void searchAllHotels(String città) {
        // todo - HOTELIERServer.searchAllHotels
    }

    /**
     * insert a review for an hotel
     * 
     * @param hotel name of the hotel
     * @param città name of the city where the hotel is
     * @param GlobalScore 
     * @param singleScores
     */
    void insertReview(String hotel, String città, double GlobalScore, Map<String, Integer> singleScores) {
        // todo - HOTELIERServer.insertReview
    }

    /**
     * show the badges of the users
     */
    void showMyBadges () {
        // todo - HOTELIERServer.showMyBadges
    }
}
