package main.dataModels;

import java.util.List;
import java.util.Map;

public class Hotel {
    private String id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private List<String> services;
    private int rank;
    private List<Review> reviews;
    private double rate;
    private Map<String, Integer> ratings;

    /**
     * (ID) Nome hotel
     * rank
     * città - telefono
     * descrizione
     * servizi:
     * - servizio1
     * - servizio2
     * review number
     * avergae rating
     * average rating1
     * average rating2
     * average rating3
     * average rating4
     * average rating5
     */
    @Override
    public String toString() {
        String string = "";
        string += "(ID: " + this.id + ") " + this.name + "\nRANK: " + Integer.toString(this.rank) + "\n" + this.city
                + " - " + this.phone + "\n\t" + description + "\nSERVICES:\n";

        for (String serv : this.services) {
            string += "- " + serv + "\n";
        }

        int reviewsNumber;
        if (this.reviews == null) {
            reviewsNumber = 0;
        } else {
            reviewsNumber = this.reviews.size();
        }

        string += Integer.toString(reviewsNumber) + " reviews:\n";
        string += "\tAverage Global Rate: " + Double.toString(this.rate);
        for (String key : this.ratings.keySet()) {
            string += "\t\t" + key + " " + Integer.toString(this.ratings.get(key));
        }

        return string;
    }

    /**
     * @return id parameter
     */
    public String getId() {
        return this.id;
    }

    /**
     * @id you want to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return parameter parameter
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return phone parameter
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * @phone you want to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return services parameter
     */
    public List<String> getServices() {
        return this.services;
    }

    /**
     * @services you want to set
     */
    public void setServices(List<String> services) {
        this.services = services;
    }

    /**
     * @param description you want to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return name parameter
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name you want to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return city parameter
     */
    public String getCity() {
        return this.city;
    }

    /**
     * @param city you want to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return rate parameter
     */
    public double getRate() {
        return this.rate;
    }

    /**
     * @param rate you want to set
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * @return ratings parameter
     */
    public Map<String, Integer> getRatings() {
        return this.ratings;
    }

    /**
     * @param ratings you want to set
     */
    public void setRatings(Map<String, Integer> ratings) {
        this.ratings = ratings;
    }

    /**
     * @return the list of reviews of this hotel
     */
    public List<Review> getReviews() {
        return this.reviews;
    }

    /**
     * @reviews you want to set
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * add a review to the reviews list of this hotel
     * 
     * @param review the review you want to add
     */
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    /**
     * @return the number of review of this hotel
     */
    public int getReviewsNumber() {
        return reviews.size();
    }

    /**
     * @return rank parameter
     */
    public int getRank() {
        return this.rank;
    }

    /**
     * @param rank you want to set
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return a copy of this hotel
     */
    public Hotel copy() {
        // todo - DELETE ?????
        Hotel clone = new Hotel();

        clone.setId(this.id);
        clone.setName(this.name);
        clone.setDescription(this.description);
        clone.setCity(this.city);
        clone.setPhone(this.phone);
        clone.setServices(this.services);
        clone.setRank(this.rank);
        clone.setReviews(this.reviews);
        clone.setRate(this.rate);
        clone.setRatings(this.ratings);

        return clone;
    }
}