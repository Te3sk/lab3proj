package main.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.dataModels.User;

public class UserManagement {
    /** (key: username, value: User) 2 */
    private Map<String, User> users;
    /** path of the JSON file */
    private String dataFilePath;
    /** set of users (by username) that are actually logged in */
    private Set<String> loggedInUsers;
    private DataPersistence dataPersistence;

    /**
     * constructor
     * 
     * @param dataFilePath path of the JSON file where load infos
     */
    public UserManagement(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        this.dataPersistence = new DataPersistence();
        this.users = this.dataPersistence.loadUsers(dataFilePath);       
        this.loggedInUsers = new HashSet<>();
    }

    /**
     * register a new user
     * 
     * @param username
     * @param psw
     * @throws Exceptions if empty fields or existing username
     */
    public synchronized void register(String username, String psw) throws Exception {
        // check if username and psw is empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            throw new Exception("Register error: username and password cannot be empty");
        }
        // check if the username already exists
        for (String usrnm : this.users.keySet()) {
            if (username.equals(usrnm)) {
                throw new Exception("Register error: username already exists");
            }
        }

        // save new user datas
        User newUser = new User(username, psw);
        this.users.put(newUser.getUsername(), newUser);
    }

    /**
     * log an user
     * 
     * @param username
     * @param psw
     * @return a fail or success message (String)
     */
    public synchronized void login(String username, String psw) throws Exception {
        // check if username or psw are empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            throw new Exception("Login error: username and password cannot be empty");
        }

        // check if username exists
        User user = this.users.get(username);

        if (user == null) {
            throw new Exception("Username not found.");
        }

        // check if psw match with the User
        if (!user.getPassword().equals(psw)){
            throw new Exception("Incorrect psw.");
        }

        this.loggedInUsers.add(username);
        throw new Exception("Login successfull.");
    }

    /**
     * unlog an user
     * 
     * @param username
     * @return a fail or success message (String)
     */
    public void logout(String username) throws Exception {
        if(!loggedInUsers.contains(username)){
            throw new Exception("Logout error: user not found in logged user list");
        }

        loggedInUsers.remove(username);
    }

    /**
     * save users to JSON file
     */
    public void saveUsers(){
        dataPersistence.saveUsers(users, this.dataFilePath);
    }

    /**
    * @return users parameter
    */
    public Map<String, User> getAllUsers (){
        return this.users;
    }


    public User getUser (String username) throws Exception{
        User temp = this.users.get(username);
        if (temp == null) {
            throw new Exception("Error: username not found");
        }
        return temp;    
    }
}
