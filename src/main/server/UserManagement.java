package main.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.dataModels.JsonUtil;
import main.dataModels.User;

public class UserManagement {
    /** (key: username, value: User) 2 */
    private Map<String, User> users;
    /** path of the JSON file */
    private String dataFilePath;
    /** set of users (by username) that are actually logged in */
    private Set<String> loggedInUsers;

    /**
     * constructor
     * 
     * @param dataFilePath path of the JSON file where load infos
     */
    public UserManagement(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        this.users = new HashMap<String, User>();
        this.loggedInUsers = new HashSet<>();

        try {
            List<User> temp = new ArrayList<User>();
            temp = JsonUtil.deserializeListFromFile(this.dataFilePath, User.class);
            for (User users : temp) {
                this.users.put(users.getUsername(), users);
            }
        } catch (IOException e) {
            System.out.println("Errore nella deserializzazione: " + e);
        }
    }

    /**
     * register a new user
     * 
     * @param username
     * @param psw
     * @return a fail or success message (String)
     * @throws Exception
     */
    public synchronized String register(String username, String psw) {
        // check if username and psw is empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            return "Register error: username and password cannot be empty";
        }
        // check if the username already exists
        for (String usrnm : this.users.keySet()) {
            if (username.equals(usrnm)) {
                return "Register error: username already exists";
            }
        }

        // save new user datas
        User newUser = new User(username, psw);
        this.users.put(newUser.getUsername(), newUser);

        return "Registration successfull";
    }

    /**
     * log an user
     * 
     * @param username
     * @param psw
     * @return a fail or success message (String)
     */
    public synchronized String login(String username, String psw) {
        // check if username or psw are empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            return "Login error: username and password cannot be empty";
        }

        // check if username exists
        User user = this.users.get(username);

        if (user == null) {
            return "Username not found.";
        }

        // check if psw match with the User
        if (!user.getPassword().equals(psw)){
            return "Incorrect psw.";
        }

        this.loggedInUsers.add(username);
        return "Login successfull.";
    }

    /**
     * unlog an user
     * 
     * @param username
     * @return a fail or success message (String)
     */
    String logout(String username) {
        if(!loggedInUsers.contains(username)){
            return "Logout error: user not found in logged user list";
        }

        loggedInUsers.remove(username);
        return "Logout successfull";
    }
}
