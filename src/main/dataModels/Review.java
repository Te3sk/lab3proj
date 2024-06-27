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
     * @param rate the global rate of the hotel
     * @param ratings the 
     * @param date the date of the review
     */
    public Review (double rate, Map<String, Integer> ratings, Date date) {
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
    }

    /**
     * constructor, check parameters validity
     * 
     * @param rate the global rate of the hotel
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

        for(String f : ratings.keySet()) {
            // check if the field is one of the valid one
            if(field.contains(f)) {
                field.remove(f);
            }

            // check if the single rating is valid (between 0 and 5)
            if (ratings.get(f) < 0 || ratings.get(f) > 5) {
            // todo - non va bene, capire come tornare un'eccezione/errore e interrompere
            }
        }

        // check if all the valid field was in the review
        if (!field.isEmpty()) {
            // todo - non va bene, capire come tornare un'eccezione/errore e interrompere
        }

        // it's all ok
        this.ratings = ratings;
    }

    /**
    * @return (Double) rate parameter
    */
    public double getRate (){
        return this.rate;
    }

    /**
    * @rate you want to set
    */
    public void setRate (double rate){
        this.rate = rate;
    }

    /**
    * @return ratings parameter
    */
    public Map<String, Integer> getRatings (){
        return this.ratings;
    }

    /**
    * @return date parameter
    */
    public Date getDate (){
        return this.date;
    }

    /**
    * @date you want to set
    */
    public void setDate (Date date){
        this.date = date;
    }

    @Override
    public String toString() {
        String res = new String();
        res += "rate:"+ Double.toString(this.rate) + ";ratings:{";

        for (String key : this.ratings.keySet()) {
            res += key + ":" + Integer.toString(this.ratings.get(key)) + ",";
        }

        res += "};date:" + this.date.toString();

        return res;        
    }

    public static Review fromString(String str) throws ParseException {
        // TODO - fix this methods (fromString)
        Double rate = 0.0;
        Map<String, Integer> ratings = new HashMap<>();
        Date date = new Date();

        // TODO - temp debug print
        System.out.println("* DEBUG - \tReview.fromString - str >" + str + "<");
        
        String[] parts = str.split(";");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "rate":
                    rate = Double.parseDouble(value);
                    break;
                case "date":
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    date = sdf.parse(value);
                    break;
                case "ratings":
                    String[] ratingsParts = value.split(",");
                    for (String ratingPart : ratingsParts) {
                        String[] rating = ratingPart.split(":");
                        ratings.put(rating[0], Integer.parseInt(rating[1]));
                    }
                    break;
                default:
                    // todo - error
            }
        }
        Review review = new Review(rate, ratings, date);
        return review;
    }
}

