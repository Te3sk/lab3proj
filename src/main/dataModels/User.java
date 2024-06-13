package main.dataModels;

public class User {
    private String username;
    private String password;
    private int reviews;
    private int points;

    public User(String username, String psw) {
        this.username = username;
        this.password = psw;
        this.reviews = 0;
        this.points = 0;
    }
    
    /**
     * @return username parameters
     */
    public String getUsername (){
        return this.username;
    }

    /**
     * @param username you want to set
     */
    public void setUsername (String username){
        this.username = username;
    }

    /**
    * @return password parameter
    */
    public String getPassword (){
        return this.password;
    }

    /**
    * @password you want to set
    */
    public void setPassword (String password){
        this.password = password;
    }

    /**
    * @return reviews parameter
    */
    public int getReviews (){
        return this.reviews;
    }

    /**
    * @reviews you want to set
    */
    public void setReviews (int reviews){
        this.reviews = reviews;
    }

    /**
    * @return badge parameter
    */
    public String getBadge (){
        // todo - understand level stuff
        if (this.points == 0) return "TO_UNLOCK";
        else if (this.points <= 10) return "LEVEL_1";
        else if (this.points <= 20) return "LEVEL_2";
        else if (this.points <= 30) return "LEVEL_3";
        else if (this.points <= 40) return "LEVEL_4";
        else return "LEVEL_EXPERT";
    }

    /** increments experience points of this user */
    public void updatePoints() {this.points++;}
}