/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * PlayEbean and forms Based On: Lightbeand Activator seed project :-
 * https://github.com/playframework/play-java-ebean-example.git (30-01-2017)
 */

package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * Created by eoghan on 01/02/2017.
 */

@Entity
public class User extends Model {

    @Id
    public Long id;

    public String name;

    @Required
    public int age;

    //@Column(unique=true)
    @Required
    public String username;

    @Required
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
        User user = find.where().eq("username", username).findUnique(); //Crash if not unique!!
        if (user != null) {
            if ( password .equals(user.password) ) {
                return user;
            }
        }
        return null;
    }

    // Check is new user is registering a unique username.
    public static User IsUnique(String username){
        User user = find.where().eq("username", username).findUnique();
        if (user != null) {
            return user;
        }
        return null;
    }

    public static Long EditUser(String username){

        // get the user with username
        User user = find.where().eq("username", username).findUnique(); //Crash if not unique!!
        if (user != null) {
            return user.id;
        }
        return Long.valueOf(0);
    }

    public static boolean DeleteUser(String username){

        // get the user with username
        User user = find.where().eq("username", username).findUnique(); //Crash if not unique!!
        if (user != null) {
            user.find.ref(user.id).delete();
            System.out.println("returning true from model");
            return true;
        }
        return false;
    }
}