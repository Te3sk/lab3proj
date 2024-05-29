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
    private double rate;
    private Map<String, Integer> ratings;
    private int reviews;
    private int rank;

    /**
    * @return id parameter
    */
    public String getId (){
        return this.id;
    }

    /**
    * @id you want to set
    */
    public void setId (String id){
        this.id = id;
    }

    /**
    * @return parameter parameter
    */
    public String getDescription (){
        return this.description;
    }

    /**
    * @return phone parameter
    */
    public String getPhone (){
        return this.phone;
    }

    /**
    * @phone you want to set
    */
    public void setPhone (String phone){
        this.phone = phone;
    }

    /**
    * @return services parameter
    */
    public List<String> getServices (){
        return this.services;
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
     * @return reviews parameter
     */
    public int getReviews() {
        return this.reviews;
    }

    /**
     * @param reviews you want to set
     */
    public void setReviews(int reviews) {
        this.reviews = reviews;
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
}