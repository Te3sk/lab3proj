package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import main.dataModels.Hotel;
import main.dataModels.JsonUtil;
import main.dataModels.Capitals;
import main.dataModels.Review;

public class HotelManagement {
    /** path of the JSON file with all the hotels */
    private String hotelPath;
    /** map as (key:hotel.id, value:Hotel) */
    private Map<String, Hotel> hotels;

    /**
     * constructor
     */
    public HotelManagement(String hotelPath) {
        this.hotelPath = hotelPath;
        this.loadHotels();
    }

    /**
     * load the hotel infos from the JSON file
     */
    public void loadHotels() {
        try {
            List<Hotel> temp = new ArrayList<Hotel>();
            temp = JsonUtil.deserializeListFromFile(hotelPath, Hotel.class);
            // insert each hotel in the map as (key:id, value:Hotel)
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
     * @param city      name of the city that contains the hotel
     * @return an Hotel obj with all the info required, null if the hotel not found
     */
    public Hotel searchHotel(String hotelName, String city) throws Exception {
        if (!Capitals.isValidCapital(city)) {
            throw new Exception("Search Hotel Error: the city must be one of the 20 italian capitals.");
        }

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
     * @return a list of Hotel obj with all the info required, null if no hotel is
     *         found
     */
    public List<Hotel> searchHotelByCity(String city) throws Exception {
        if (!Capitals.isValidCapital(city)) {
            throw new Exception("Search Hotel Error: the city must be one of the 20 italian capitals.");
        }

        List<Hotel> hotelInCity = new ArrayList<Hotel>();

        for (Hotel hotel : this.hotels.values()) {
            if (city.equals(hotel.getCity())) {
                hotelInCity.add(hotel);
            }
        }

        if (hotelInCity.size() <= 0) {
            return null;
        }

        return hotelInCity;
    }

    /**
     * add a review for an hotel
     * 
     * @param nomeHotel name of the hotel
     * @param nomeCittà city of the hotel
     * @param review    the review (obj) you want to add
     */
    public void addReview(String nomeHotel, String nomeCittà, Review review) {
        // check if hotel exists and find it
        String currentId = "empty";

        for (Hotel hotel : this.hotels.values()) {
            if (nomeHotel.equals(hotel.getName()) && nomeCittà.equals(hotel.getCity())) {
                currentId = hotel.getId();
                break;
            }
        }

        if (currentId.equals("empty")) {
            // todo - errore: hotel non trovato
        }

        // todo - capire se così va bene, se current punta allo stesso indirizzo di
        // todo - quello nella lista quindi cambia automaticamente o se va aggiornato
        // todo - quello nella lista
        Hotel current = this.hotels.get(currentId);
        // add the review in the list reviews
        current.addReview(review);

        // calculate and set new average rate and ratings
        int size = current.getReviewsNumber();

        double newRate = (current.getRate() + review.getRate()) / size;

        Map<String, Integer> newRatings = new HashMap<>();
        for (String field : review.getRatings().keySet()) {
            newRatings.put(field, ((current.getRatings().get(field) + review.getRatings().get(field)) / size));
        }

        current.setRate(newRate);
        current.setRatings(newRatings);
    }

    /**
     * get all the reviews of one hotel
     * 
     * @param nomeHotel the name of the hotel
     * @param nomeCittà the city of the hotel
     * @return a list of Review obj
     */
    public List<Review> getReviews(String nomeHotel, String nomeCittà) {
        // check if hotel exists and find it
        String currentId = "empty";

        for (Hotel hotel : this.hotels.values()) {
            if (nomeHotel.equals(hotel.getName()) && nomeCittà.equals(hotel.getCity())) {
                currentId = hotel.getId();
                break;
            }
        }

        if (currentId.equals("empty")) {
            // todo - errore: hotel non trovato
        }

        // todo - capire se così va bene, se current punta allo stesso indirizzo di
        // todo - quello nella lista quindi cambia automaticamente o se va aggiornato
        // todo - quello nella lista
        Hotel current = this.hotels.get(currentId);

        return current.getReviews();
    }

    /**
     * todo - levare da qua, scrivere nella relazione
     * Per implementare il metodo che aggiorna il rank di ogni hotel nella classe
     * HotelManagement, possiamo adottare un algoritmo che combina la qualità, la
     * quantità e l'attualità delle recensioni per ciascun hotel. La qualità può
     * essere rappresentata dal punteggio medio delle recensioni, la quantità dal
     * numero di recensioni totali e l'attualità dalla data delle recensioni
     * (assumendo che ci sia un attributo date nella classe Review).
     * 
     * Ecco un esempio di come potrebbe essere implementato questo metodo:
     * 
     * Qualità delle recensioni: Calcolare il punteggio medio delle recensioni per
     * ogni hotel.
     * Quantità delle recensioni: Considerare il numero totale di recensioni per
     * ogni hotel.
     * Attualità delle recensioni: Pesare le recensioni più recenti più fortemente
     * rispetto a quelle più vecchie.
     * Per calcolare il rank, possiamo usare una formula che combina questi tre
     * fattori, ad esempio:
     * rank = ((media punteggio/5.0)*0.5) + ((numero recensioni/massimo numero
     * recensioni in città)*0.3)+((peso recensioni recenti/massimo peso recensioni
     * recenti in città)*0.2)
     * 
     * La media del punteggio viene normalizzata a un valore tra 0 e 1 dividendo per
     * 5.
     * 
     * Il numero di recensioni viene normalizzato rispetto al massimo numero di
     * recensioni per un hotel nella stessa città.
     * 
     * Il peso delle recensioni recenti viene normalizzato rispetto al massimo peso
     * delle recensioni recenti per un hotel nella stessa città.
     */
    public void updateRanking() {
        // create a map to group hotels by city
        Map<String, List<Hotel>> hotelsByCity = this.groupByCity();

        // calculate weight for recency (for each city)
        for (String currentCity : hotelsByCity.keySet()) {
            List<Hotel> cityHotels = hotelsByCity.get(city);
            // max number of review of an hotel in that city (is 1 in case of the list is empty)
            int maxReviewCount = cityHotels.stream().mapToInt(h -> h.getReviews().size()).max().orElse(1);
            double maxRecentWeight = 0.0;

            // calculate weights for recency
            double weight = 0.0;
            long currentTime = System.currentTimeMillis();
            for (Hotel hotel : cityHotels) {
                for (Review review : hotel.getReviews()) {
                    long reviewTime = review.getDate().getTime();
                    long timeDifference = currentTime - reviewTime;
                    // the factor decrease when the time elapsed since the review increases ((1000.0 * 60 * 60 * 24 * 30) converted time from millisecond to months)
                    double recencyFactor = 1.0 / (1.0 + timeDifference / (1000.0 * 60 * 60 * 24 * 30));
                    weight += recencyFactor;    
                }
            }
        }

        // update rank for each hotel
    }

    public Map<String, List<Hotel>> groupByCity() {
        Map<String, List<Hotel>> hotelsByCity = new HashMap<>();

        for (Hotel h : this.hotels.values()) {
            String currentCity = h.getCity();
            // check if this city is already in the map
            if (!hotelsByCity.containsKey(currentCity)) {
                // if not, add the city and initialize the list
                hotelsByCity.put(currentCity, new ArrayList<>());
                for (Hotel h2 : this.hotels.values()) {
                    // put all the hotel placed in that city in the list
                    if (currentCity.equals(h2.getCity())) {
                        hotelsByCity.get(currentCity).add(h2);
                    }
                }
            }
        }

        return hotelsByCity;
    }
}
