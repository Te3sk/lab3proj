package main.server;

// todo - DELETE ?????

import main.dataModels.Review;
import main.dataModels.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// ├── ReviewManagement.java
// │   ├── void addReview(String nomeHotel, String nomeCittà, Review review) # Aggiunge una recensione per un hotel
// │   ├── List<Review> getReviews(String nomeHotel, String nomeCittà) # Ottiene tutte le recensioni per un hot

public class ReviewManagement {
    private Map<String, Review> hotelReview;
    
    /**
     * add a review for an hotel
     * 
     * @param nomeHotel name of the hotel
     * @param nomeCittà city of the hotel
     * @param review the review (obj) you want to add
     */
    public void addReview(String nomeHotel, String nomeCittà, Review review) {
        // todo - ReviewManagement.addReview
    }

    public List<Review> getReviews(String nomeHotel, String nomeCittà) {
        // todo - ReviewManagement.getReviews
        return new ArrayList<Review>();
    }
}
