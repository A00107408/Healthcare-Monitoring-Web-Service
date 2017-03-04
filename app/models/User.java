package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by eoghan on 01/02/2017.
 */

@Entity
public class User extends Model {

    @Id
    public Long id;

    public String name;
    public int age;
    public String username;
    public String password;

    public static Find<Long,User> find = new Find<Long,User>(){};


    /**
     * Authenticate a User, from a username and clear password.
     *
     * @param username  username
     * @param password  password
     * @return User if authenticated, null otherwise
     */
    public static User authenticate(String username, String password){

        // get the user with username
        User user = find.where().eq("username", username).findUnique();
        if (user != null) {
            if ( password .equals(user.password) ) {
                return user;
            }
        }
        return null;
    }
}