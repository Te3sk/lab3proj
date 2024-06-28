package main.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.dataModels.User;

public class UserManagement {
    /** (key: username, value: User) 2 */
    private Map<String, User> users;
    /** path of the JSON file */
    private String dataFilePath;
    /** set of users (by username) that are actually logged in */
    private Set<String> loggedInUsers;
    private Lock lock;
    private DataPersistence dataPersistence;

    /**
     * Constructs a new UserManagement object with the specified data file path.
     * 
     * @param dataFilePath the path to the data file containing user information
     */
    public UserManagement(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        this.dataPersistence = new DataPersistence();
        this.users = this.dataPersistence.loadUsers(dataFilePath);
        this.loggedInUsers = new HashSet<>();
        this.lock = new ReentrantLock();
    }

    /**
     * register a new user
     * 
     * @param username
     * @param psw
     * @throws EMPTYF  if the fields is empty
     * @throws USERN_Y if the username already exists
     */
    public synchronized void register(String username, String psw) throws Exception {
        // get the lock
        this.lock.lock();

        // check if username and psw is empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            throw new Exception("EMPTYF");
        }
        // check if the username already exists
        for (String usrnm : this.users.keySet()) {
            if (username.equals(usrnm)) {
                throw new Exception("USERN_Y");
            }
        }

        // save new user datas
        User newUser = new User(username, psw);
        this.users.put(newUser.getUsername(), newUser);

        // TODO - temp debug print
        System.out.println("* DEBUG - \tsave user, now register on json");

        // update json file
        this.dataPersistence.saveUsers(this.users, this.dataFilePath);

        // release the lock
        this.lock.unlock();
    }

    /**
     * log an user
     * 
     * @param username
     * @param psw
     * @return a fail or success message (String)
     * @throws EMPTYF   if the fields is empty
     * @throws USERN_N  if the user not exists
     * @throws WRONGPSW if the password is incorrect
     */
    public synchronized void login(String username, String psw) throws Exception {
        // get the lock
        this.lock.lock();

        // check if username or psw are empty
        if (username == null || username.isEmpty() || psw == null || psw.isEmpty()) {
            throw new Exception("EMPTYF");
        }

        // check if username exists
        User user = this.users.get(username);

        if (user == null) {
            // release the lock
            this.lock.unlock();

            throw new Exception("USERN_N");
        }

        // check if psw match with the User
        if (!user.getPassword().equals(psw)) {
            // release the lock
            this.lock.unlock();

            throw new Exception("Incorrect psw.");
        }

        this.loggedInUsers.add(username);

        // release the lock
        this.lock.unlock();

        throw new Exception("Login successfull.");
    }

    /**
     * unlog an user
     * 
     * @param username
     * @return a fail or success message (String)
     * @throws USERN_N if can't find the username
     */
    public void logout(String username) throws Exception {
        // get the lock
        this.lock.lock();

        if (!loggedInUsers.contains(username)) {
            // release the lock
            this.lock.unlock();

            throw new Exception("USERN_N");
        }

        loggedInUsers.remove(username);

        // release the lock
        this.lock.unlock();
    }
    
    /**
     * Saves the users to a data file.
     * Acquires a lock before saving the users and releases the lock after saving.
     */
    public void saveUsers() {
        // get the lock
        this.lock.lock();

        dataPersistence.saveUsers(users, this.dataFilePath);

        // release the lock
        this.lock.unlock();
    }

    /**
     * Returns the user with the specified username.
     * 
     * @param username the username of the user to return
     * @return the user with the specified username
     * @throws Exception if the user with the specified username does not exist
     */
    public Map<String, User> getAllUsers() {
        return this.users;
    }

    public User getUser(String username) throws Exception {
        // get the lock
        this.lock.lock();

        User temp = this.users.get(username);
        if (temp == null) {
            // release the lock
            this.lock.unlock();

            throw new Exception("Error: username not found");
        }

        // release the lock
        this.lock.unlock();

        return temp;
    }
}
