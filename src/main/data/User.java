package main.data;

public class User {
    private String username;
    private String password;
    private int reviews;
    private String badge;
    
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
        return this.badge;
    }
}