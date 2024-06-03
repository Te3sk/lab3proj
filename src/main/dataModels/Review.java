package main.dataModels;

import java.util.Date;
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
    * @return rate parameter
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
}

