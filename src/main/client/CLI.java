package main.client;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import main.dataModels.Review;


public class CLI {
    // standard message
    String dash = "---------------------------------";
    String invalidInput = "\tInvalid input, re-try!\n" + this.dash;

    /**
     * home page of the client interface
     * 
     * @param username IF THE USER IS LOGGED the username of the user, else NULL
     * @return the number of the operation chosen by the user
     */
    public int homePage(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.println(this.dash + "\n");
        Map<Integer, String> op = new HashMap<>();

        if (username != null) {
            System.out.println("\tWelcome " + username + "!");
        } else {
            System.out.println("Welcome!");
        }

        // register, login, logout, searchHotel, searchAllHotels, insertReview,
        // showMyBadges
        String msg = "";
        int n = 0;
        Set<Integer> valid = new HashSet<Integer>();

        op.put(1, "Sign In");
        op.put(2, "Login");
        op.put(3, "Search Hotel (by name and city)");
        op.put(4, "Search a list of hotel (by city)");
        op.put(5, "Insert Review");
        op.put(6, "Show My Badges");
        op.put(7, "Logout");
        op.put(8, "Quit");

        if (username == null) {
            // register, login, searchHotel, searchAllHotels
            valid.add(1);
            valid.add(2);
            valid.add(3);
            valid.add(4);
            valid.add(8);

            for (Integer key : op.keySet()) {
                if (!valid.contains(key)) {
                    msg += "(-)" + op.get(key) + "\n";
                } else {
                    msg += "(" + Integer.toString(key) + ")" + op.get(key) + "\n";
                }
            }

        } else {
            // logout, searchHotel, searchAllHotels, insertReview, showMyBadges
            valid.add(2);
            valid.add(3);
            valid.add(4);
            valid.add(5);
            valid.add(6);
            valid.add(7);
            valid.add(8);
            for (Integer key : op.keySet()) {
                if (!valid.contains(key)) {
                    msg += "(-)" + op.get(key) + "\n";
                } else {
                    msg += "(" + Integer.toString(key) + ")" + op.get(key) + "\n";
                }
            }
        }

        System.out.println(msg);

        do {
            System.out.println("Insert the number of the operation:");
            while (!sc.hasNextInt()) {
                System.out.println(this.invalidInput);
                sc.next();
            }

            n = sc.nextInt();
        } while (!valid.contains(n));

        System.out.println("Operazione scelta: " + op.get(n));
        sc.close();


        return n;
    }

    /**
     * registration of a new user
     * 
     * @return an array of 2 strings, the first is the username, the second is the password
     */
    public String[] insertCred() {
        Scanner sc = new Scanner(System.in);
        String username = null;
        String psw = null;

        System.out.println(this.dash + "\n\tREGISTRATION\n" + this.dash);

        System.out.println("Username:");
        username = sc.nextLine();
        System.out.println("Password:");
        psw = sc.nextLine();

        sc.close();

        String[] res = new String[2];
        res[0] = username;
        res[1] = psw;

        return res;
    }

    /**
     * Prompts the user to enter a hotel name and city name, and returns an array containing the entered values.
     *
     * @return an array of strings containing the hotel name and city name entered by the user
     */
    public String[] searchHotel() {
        Scanner sc = new Scanner(System.in);
        String hotelName = null;
        String cityName = null;
        System.out.println(this.dash + "\n\tSEARCHING HOTEL (by name and city)\n" + this.dash);

        System.out.println("Hotel Name:");
        hotelName = sc.nextLine();
        System.out.println("City Name:");
        cityName = sc.nextLine();

        sc.close();

        String [] res = new String[2];
        res[0] = hotelName;
        res[1] = cityName;
        return res;
    }

    /**
     * Searches for hotels in a specific city.
     * 
     * @return the name of the city entered by the user
     */
    public String searchAllHotels(){
        Scanner sc = new Scanner(System.in);
        String cityName = null;
        System.out.println(this.dash + "\n\tSEARCHING HOTEL IN A CITY \n" + this.dash);

        System.out.println("City Name:");
        cityName = sc.nextLine();

        sc.close();

        return cityName;
    }

    /**
     * Prompts the user to enter a review for a hotel and returns the review details.
     * 
     * @return an array containing the hotel name and the review object
     */
    public Object[] insertReview(){
        Scanner sc = new Scanner(System.in);
        String hotelName = "";
        Double rate = -1.0;
        int temp = -1;
        Map<String, Integer> ratings = new HashMap<String, Integer>();

        System.out.println(this.dash + "\n\tINSERT REVIEW\n" + this.dash);

        // get the hotel name
        System.out.println("Hotel Name:");
        hotelName = sc.nextLine();
        
        // get the global rate
        System.out.println("Global Rate (can use decimal point number):");
        while(!sc.hasNextDouble()) {
            System.out.println("\tInsert the rate, re-tr!y");
        }
        rate = sc.nextDouble();

        // get the cleaning rate
        System.out.println("Cleaning Rate:");
        while(!sc.hasNextInt()) {
            System.out.println("\tInsert the rate (integer), re-try!");
        }
        temp = sc.nextInt();
        ratings.put("cleaning", temp);

        // get the position rate
        System.out.println("Position Rate:");
        while(!sc.hasNextInt()) {
            System.out.println("\tInsert the rate (integer), re-try!");
        }
        temp = sc.nextInt();
        ratings.put("position", temp);

        // get the services rate
        System.out.println("Services Rate:");
        while(!sc.hasNextInt()) {
            System.out.println("\tInsert the rate (integer), re-try!");
        }
        temp = sc.nextInt();
        ratings.put("services", temp);

        // get the quality rate
        System.out.println("Quality Rate:");
        while(!sc.hasNextInt()) {
            System.out.println("\tInsert the rate (integer), re-try!");
        }
        temp = sc.nextInt();
        ratings.put("quality", temp);

        // compute the date
        Date date = new Date();

        sc.close();

        Object[] res = new Object[2];
        res[0] = hotelName;
        res[1] = new Review(rate, ratings, date);

        return res; 
    }

}
