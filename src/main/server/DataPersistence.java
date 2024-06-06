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
    public DataPersistence() {
        // todo - DataPersistence.constructor
    }

    /**
     * Save user datas in a JSON file
     * todo
     * 
     * @param users
     * @param filePath
     */
    public void saveUsers(Map<String, User> users, String filePath) {
        try {
            List<User> temp = new ArrayList<>(users.values());
            JsonUtil.serializeListToFile(temp, filePath);
        } catch (Exception e) {
            System.out.println("Error during users save: " + e);
        }
    }

    /**
     * save hotels datas in a JSON file
     * todo
     * 
     * @param hotels
     * @param filePath
     */
    public void saveHotels(List<Hotel> hotels, String filePath) {
        try {
            JsonUtil.serializeListToFile(hotels, filePath);
        } catch (Exception e) {
            System.out.println("Error during hotels saving: " + e);
        }
    }

    /**
     * load users datas from a JSON file
     * todo
     * 
     * @param filePath
     * @return
     */
    public Map<String, User> loadUsers(String filePath) {
        try {
            Map<String, User> users = new HashMap<String, User>();
            List<User> temp = new ArrayList<User>();
            temp = JsonUtil.deserializeListFromFile(filePath, User.class);
            for (User user : temp) {
                users.put(user.getUsername(), user);
            }
            return users;
        } catch (Exception e) {
            System.out.println("Error during users load: " + e);
            return null;
        }
    }

    /**
     * load hotels datas from a JSON file
     * todo
     * 
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
