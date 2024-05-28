package main.data;

import java.util.Map;

public class Hotel {
    private String name;
    private String city;
    private double globalScore;
    private Map<String, Integer> scores;
    private int reviews;
    private int rank;

    
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
     * @return globalScore parameter
     */
    public double getGlobalScore() {
        return this.globalScore;
    }

    /**
     * @param globalScore you want to set
     */
    public void setGlobalScore(double globalScore) {
        this.globalScore = globalScore;
    }

    /**
     * @return scores parameter
     */
    public Map<String, Integer> getScores() {
        return this.scores;
    }

    /**
     * @param scores you want to set
     */
    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
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