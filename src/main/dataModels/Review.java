package main.dataModels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

// "rate": 0,
// "ratings": {
//     "cleaning": 0,
//     "position": 0,
//     "services": 0,
//     "quality": 0
// }

public class Review {
    private double rate;
    private Map<String, Integer> ratings;
    private Date date;

    /**
     * constructor
     * 
     * @param rate    the global rate of the hotel
     * @param ratings the
     * @param date    the date of the review
     */
    public Review(double rate, Map<String, Integer> ratings, Date date) {
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
    }

    /**
     * constructor, check parameters validity
     * 
     * @param rate    the global rate of the hotel
     * @param ratings the
     */
    public Review(double rate, Map<String, Integer> ratings) {
        this.rate = rate;

        // check ratings keys and values
        Set<String> field = new HashSet<>();

        field.add("cleaning");
        field.add("position");
        field.add("services");
        field.add("quality");

        for (String f : ratings.keySet()) {
            // check if the field is one of the valid one
            if (field.contains(f)) {
                field.remove(f);
            }

            // check if the single rating is valid (between 0 and 5)
            if (ratings.get(f) < 0 || ratings.get(f) > 5) {
                throw new IllegalArgumentException("Invalid rating value.");
            }
        }

        // check if all the valid field was in the review
        if (!field.isEmpty()) {
            throw new IllegalArgumentException("Missing rating field(s).");
        }

        // it's all ok
        this.ratings = ratings;
    }

    /**
     * @return (Double) rate parameter
     */
    public double getRate() {
        return this.rate;
    }

    /**
     * @rate you want to set
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
     * @return date parameter
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * @date you want to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String res = new String();

        res += Double.toString(this.rate) + ";" + Integer.toString(this.ratings.get("cleaning")) + ";"
                + Integer.toString(this.ratings.get("position")) + ";" + Integer.toString(this.ratings.get("services"))
                + ";" + Integer.toString(this.ratings.get("quality")) + ";" + this.date.toString();

        return res;
    }

    public static Review fromString(String str) throws Exception, ParseException {
        // TODO - fix this methods (fromString)
        Double rate = 0.0;
        Map<String, Integer> ratings = new HashMap<>();
        Date date = new Date();
        int temp = -1;

        // TODO - temp debug print
        System.out.println("* DEBUG (Review.fromString) - \t - str >" + str + "<");

        String[] parts = str.split(";");
        
        for (String part : parts) {
            // TODO - temp debug print
            System.out.println("* DEBUG (Review.fromString) - \tpart >" + part + "<");
        }

        if (parts.length != 6) {
            throw new Exception("Invalid string format for Review object.");
        }

        // get global rate
        rate = Double.parseDouble(parts[0]);
        if (Double.isNaN(rate) || rate < 0 || rate > 5) {
            throw new Exception("Invalid rate value.");
        }
        
        // get cleaning rating
        temp = Integer.parseInt(parts[1]);
        if (temp < 0 || temp > 5) {
            throw new Exception("Invalid cleaning rating value.");
        }
        ratings.put("cleaning", temp);
        
        // get position rating
        temp = Integer.parseInt(parts[2]);
        if (temp < 0 || temp > 5) {
            throw new Exception("Invalid position rating value.");
        }
        ratings.put("position", temp);
        
        // get services rating
        temp = Integer.parseInt(parts[3]);
        if (temp < 0 || temp > 5) {
            throw new Exception("Invalid services rating value.");
        }
        ratings.put("services", temp);
        
        // get quality rating
        temp = Integer.parseInt(parts[4]);
        if (temp < 0 || temp > 5) {
            throw new Exception("Invalid quality rating value.");
        }
        ratings.put("quality", temp);

        // get date
        date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(parts[5]);
        if (date == null) {
            throw new Exception("Invalid date format.");
        }

        // create and return the review
        Review review = new Review(rate, ratings, date);

        // TODO - temp debug print
        System.out.println("* DEBUG - \treview parsed:\n" + review.printReview());
        return review;
    }

    public String printReview() {
        String res = new String();

        res += "Date: " + this.date.toString() + "\n";
        res += "Rate: " + Double.toString(this.rate) + "\n";
        res += "\tCleaning: " + Integer.toString(this.ratings.get("cleaning")) + "\n";
        res += "\tPosition: " + Integer.toString(this.ratings.get("position")) + "\n";
        res += "\tServices: " + Integer.toString(this.ratings.get("services")) + "\n";
        res += "\tQuality: " + Integer.toString(this.ratings.get("quality")) + "\n";

        return res;
    }
}
