package main.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.dataModels.Hotel;
import main.dataModels.Capitals;
import main.dataModels.Review;

public class HotelManagement {
    /** path of the JSON file with all the hotels */
    private String hotelPath;
    /** map as (key:hotel.id, value:Hotel) */
    private Map<String, Hotel> hotels;

    private DataPersistence dataPersistence;

    /**
     * constructor
     */
    public HotelManagement(String hotelPath) {
        this.hotelPath = hotelPath;
        this.dataPersistence = new DataPersistence();
        this.hotels = dataPersistence.loadHotels(hotelPath);
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
    * @return hotels parameter
    */
    public Map<String, Hotel> getHotels() {
        return hotels;
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
            List<Hotel> cityHotels = hotelsByCity.get(currentCity);
            // max number of review of an hotel in that city (is 1 in case of the list is
            // empty)
            int maxReviewCount = cityHotels.stream().mapToInt(h -> h.getReviews().size()).max().orElse(1);
            double maxRecentWeight = 0.0;

            // calculate weights for recency

            for (Hotel hotel : cityHotels) {
                for (Review review : hotel.getReviews()) {
                    double recentWeight = calculateRecentWeight(hotel);
                    if (maxRecentWeight < recentWeight) {
                        maxRecentWeight = recentWeight;
                    }
                }
            }

            // compute rank value for each hotel
            Map<String, Double> rankValues = new HashMap<String, Double>();
            for (Hotel hotel : cityHotels) {
                // get the average rate
                double avgRate = hotel.getRate();
                // get the number of review
                int reviewCount = hotel.getReviewsNumber();
                // get the recent weight
                double recentWeight = calculateRecentWeight(hotel);
                // todo - levare da qui e scrivere nella doc
                // - avgRating è la valutazione media delle recensioni dell'hotel.
                // - 5.0 è il massimo punteggio possibile per una recensione (assumendo una
                // scala da 1 a 5).
                // - reviewsCount è il numero totale di recensioni per l'hotel.
                // - maxReviewsCount è il numero massimo di recensioni tra tutti gli hotel della
                // stessa città.
                // - recentWeight è il peso totale delle recensioni recenti per l'hotel,
                // calcolato con il metodo calculateRecentWeight.
                // - maxRecentWeight è il massimo peso delle recensioni recenti tra tutti gli
                // hotel della stessa città.
                //
                // Ogni componente della formula è moltiplicata per un peso (0.5 per la qualità,
                // 0.3 per la quantità, 0.2 per l'attualità) per dare l'importanza relativa
                // desiderata a ciascun fattore nel calcolo del rank finale dell'hotel.
                double rankValue = ((avgRate / 0.5) * 0.5) + ((reviewCount / maxReviewCount) / 0.3)
                        + ((recentWeight / maxRecentWeight) * 0.2);
                rankValues.put(hotel.getId(), rankValue);
            }
            
            // sort by rank value
            cityHotels.sort((hotel1, hotel2) -> {
                Double rank1 = rankValues.get(hotel1.getId());
                Double rank2 = rankValues.get(hotel2.getId());
                return rank1.compareTo(rank2);
            });
            int i = 1;
            for (Hotel hotel : cityHotels) {
                this.hotels.get(hotel.getId()).setRank(i);
                i++;
            }

        }
        // todo - send notification to logged users
    }

    /**
     * @return a map <key:city, value:hotelId> that associated a city with the most high rank hotel
     */
    public Map<String, String> firstLocalHotels() {
        // this list will contain all the first local Hotel ids 
        Map<String, String> hotelsId = new HashMap<String, String>();
        Map<String, List<Hotel>> hotelByCity = this.groupByCity();
        for (String city : hotelByCity.keySet()) {
            List<Hotel> cityHotels = hotelByCity.get(city);
            for (Hotel hotel : cityHotels) {
                if (hotel.getRank() == 1) {
                    hotelsId.put(city, hotel.getId());
                    break;
                }
            }
        }

        return hotelsId;
    } 

    /**
     * take the Map<String, Hotel> hotels (attribute) and gruop them by city
     * 
     * @return a Map<String, List<Hotel>> associating a list of hotel with a city
     *         (string)
     */
    private Map<String, List<Hotel>> groupByCity() {
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

    private double calculateRecentWeight(Hotel hotel) {
        double weight = 0.0;
        long currentTime = System.currentTimeMillis();
        for (Review review : hotel.getReviews()) {
            long reviewTime = review.getDate().getTime();
            long timeDifference = currentTime - reviewTime;
            // the factor decrease when the time elapsed since the review increases
            // ((1000.0 * 60 * 60 * 24 * 30) converted time from millisecond to months)
            double recencyFactor = 1.0 / (1.0 + timeDifference / (1000.0 * 60 * 60 * 24 * 30));
            weight += recencyFactor;
        }

        return weight;
    }

    /**
     * save the hotels infos in the JSON file
     */
    public void saveHotel () {
        List<Hotel> temp = new ArrayList<Hotel>(this.hotels.values());
        dataPersistence.saveHotels(temp, this.hotelPath);
    }
}
