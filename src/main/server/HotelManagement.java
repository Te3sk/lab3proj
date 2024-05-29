package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import main.dataModels.Hotel;
import main.dataModels.JsonUtil;

public class HotelManagement {
    private String hotelPath;
    private Map<String, Hotel> hotels;

    /**
     * constructor
     */
    public HotelManagement(String hotelPath){
        this.hotelPath = hotelPath;
        this.loadHotels();
    }

    /**
     * load the hotel infos from the JSON file
     */
    public void loadHotels(){
        try {
            List<Hotel> temp = new ArrayList<Hotel>();
            temp = JsonUtil.deserializeListFromFile(hotelPath, Hotel.class);
            for (Hotel hotel : temp) {
                this.hotels.put(hotel.getId(), hotel);
            }
        } catch (IOException e) {
            System.out.println("Error loading hotels: " + e);
        }
    }

    /**
     * search hotel by name
     * 
     * @param hotelName name of the hotel you want to search
     * @param city name of the city that contains the hotel
     * @return an Hotel obj with all the info required, null if the hotel not found
     */
    public Hotel searchHotel(String hotelName, String city) {
        for (Hotel hotel : this.hotels.values()) {
            if (city.equals(hotel.getCity()) && hotelName.equals(hotel.getName())) {
                return hotel;
            } 
        }

        return null;
    }

    /**
     * search hotels by city
     * 
     * @param city the city that contains the hotels you want
     * @return a list of Hotel obj with all the info required, null if no hotel is found
     */
    public List<Hotel> searchHotelByCity(String city) {
        // todo - HotelManagement.searchHotelByCity> controlla che la città sia valida

        List<Hotel> hotelInCity = new ArrayList<Hotel>();
        
        for (Hotel hotel : this.hotels.values()) {
            if(city.equals(hotel.getCity())) {
                hotelInCity.add(hotel);
            }
        }

        if(hotelInCity.size() <= 0) {
            return null;
        }
        return hotelInCity;
    }
}
