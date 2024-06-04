package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.dataModels.Hotel;
import main.dataModels.JsonUtil;
import main.dataModels.User;

public class DataPersistence {
    // todo - attributes

    /**
     * constructor
     * todo
     */
    public DataPersistence(){
        // todo - DataPersistence.constructor
    }

    /**
     * Save user datas in a JSON file
     * todo
     * @param users
     * @param filePath
     */
    public void saveUsers(Map<String, User> users, String filePath) {
        // todo - DataPersistence.saveUsers
    }

    /**
     * save hotels datas in a JSON file
     * todo
     * @param hotels
     * @param filePath
     */
    public void saveHotels(List<Hotel> hotels, String filePath) {
        // todo - DataPersistence.saveHotels
    }
    
    /**
     * load users datas from a JSON file
     * todo
     * @param filePath
     * @return
     */
    public Map<String, User> loadUsers(String filePath){
        // todo - NotificationService.loadUsers
        return new HashMap<>();
    }

    /**
     * load hotels datas from a JSON file
     * todo
     * @param filePath
     * @return
     */
    public Map<String, Hotel> loadHotels(String filePath) {
        // todo - NotificationService.loadHotels
        try {
            Map<String, Hotel> hotels = new HashMap<String, Hotel>();
            List<Hotel> temp = new ArrayList<Hotel>();
            temp = JsonUtil.deserializeListFromFile(filePath, Hotel.class);
            // insert each hotel in the map as (key:id, value:Hotel)
            for (Hotel hotel : temp) {
                hotels.put(hotel.getId(), hotel);
            }
            return hotels;
        } catch (IOException e) {
            System.out.println("Error loading hotels: " + e);
            return null;
        }
    }
}
