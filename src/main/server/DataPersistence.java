package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.dataModels.Hotel;
import main.dataModels.JsonUtil;
import main.dataModels.User;

import java.util.concurrent.locks.Lock;

public class DataPersistence implements Runnable {
    private long interval;
    private Lock lock;
    private HotelManagement hotelManagement;
    private UserManagement userManagement;

    /**
     * Constructor for a DataPersistence object.
     *
     * @param interval         the interval in milliseconds between each data persistence operation
     * @param lock             the lock used to synchronize access to the data
     * @param hotelManagement  the hotel management object
     * @param userManagement   the user management object
     */
    public DataPersistence(long interval, Lock lock, HotelManagement hotelManagement, UserManagement userManagement) {
        this.interval = interval;
        this.lock = lock;
        this.hotelManagement = hotelManagement;
        this.userManagement = userManagement;
    }

    /**
     * Default constructor for a DataPersistence object.
     */
    public DataPersistence() {
    }

    /**
     * Runs the data persistence thread.
     * This method is responsible for periodically saving the hotel and user data.
     * It acquires a lock before saving the data to ensure thread safety.
     * If an InterruptedException occurs, an error message is printed indicating an interrupted error during data persistence.
     * If any other exception occurs, an error message is printed indicating an unexpected error during data persistence.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(this.interval);

                // acquire the lock
                this.lock.lock();

                this.hotelManagement.saveHotel();
                this.userManagement.saveUsers();

                // release the lock
                this.lock.unlock();
            }
        } catch (InterruptedException e) {
            // ! Error message !
            System.out.println("Interrupted error during data persistence: " + e);
        } catch (Exception e) {
            // ! Error message !
            System.out.println("Unexpected error during data persistence: " + e);
        }

    }

    /**
     * Save user data in a JSON file.
     *
     * @param users     the data in this format (used in UserManagement)
     * @param filePath  the path of the JSON file where you want to save the data
     */
    public void saveUsers(Map<String, User> users, String filePath) {
        try {
            // convert the map to a list
            List<User> temp = new ArrayList<>(users.values());

            // save the list to the file
            JsonUtil.serializeListToFile(temp, filePath);
        } catch (Exception e) {
            System.out.println("Error during users save: " + e);
        }
    }

    /**
     * Save hotel data in a JSON file.
     *
     * @param hotels    the data in this format (used in HotelManagement)
     * @param filePath  the path of the JSON file where you want to save the data
     */
    public void saveHotels(List<Hotel> hotels, String filePath) {
        try {
            JsonUtil.serializeListToFile(hotels, filePath);
        } catch (Exception e) {
            System.out.println("Error during hotels saving: " + e);
        }
    }

    /**
     * Load user data from a JSON file.
     *
     * @param filePath  the path of the JSON file from which you want to load the data
     * @return          a mapping <username, user> with all the data (same format as UserManagement)
     */
    public Map<String, User> loadUsers(String filePath) {
        try {
            Map<String, User> users = new HashMap<String, User>();
            List<User> temp = new ArrayList<User>();
            temp = JsonUtil.deserializeListFromFile(filePath, User.class);
            // if the file is empty, it means that there are no users already registered
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
     * Load hotel data from a JSON file.
     *
     * @param filePath  the path of the JSON file from which you want to load the data
     * @return          a mapping <hotelId, hotel> with all the data (same format as HotelManagement)
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
