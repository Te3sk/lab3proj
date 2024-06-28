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
    /**
     * constructor for a DataPersistent obj (empty)
     */
    public DataPersistence() {
    } 

    /**
     * Save user datas in a JSON file
     *
     * @param users the datas in this format (used in UserManagement)
     * @param filePath the path of the JSON file where you want to save the datas
     */
    public void saveUsers(Map<String, User> users, String filePath) {
        try {
            // convert the map in a list
            List<User> temp = new ArrayList<>(users.values());
            // save the list in the file
            JsonUtil.serializeListToFile(temp, filePath);
        } catch (Exception e) {
            System.out.println("Error during users save: " + e);
        }
    }

    /**
     * save hotels datas in a JSON file
     *
     * @param hotels the datas in this format (used in HotelManagement)
     * @param filePath the path of the JSON file where you want to save the datas
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
     *
     * @param filePath the path of the JSON file where you want to load the datas
     * @return a mapping <username, user> with all the datas (same format of UserManagement)
     */
    public Map<String, User> loadUsers(String filePath) {
        try {
            Map<String, User> users = new HashMap<String, User>();
            List<User> temp = new ArrayList<User>();
            temp = JsonUtil.deserializeListFromFile(filePath, User.class);
            // if the file is empty, means that there are no users already registered
            if (temp != null) {
                for (User user : temp) {
                    users.put(user.getUsername(), user);
                }
            }
            return users;
        } catch (Exception e) {
            System.out.println("Error during users load: " + e);
            return null;
        }
    }

    /**
     * load hotels datas from a JSON file
     *
     *
     * @param filePath the path of the JSON file where you want to load the datas
     * @return a mapping <hotelId, hotel> with all the datas (same format of HotelManagement)
     */
    public Map<String, Hotel> loadHotels(String filePath) {
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
