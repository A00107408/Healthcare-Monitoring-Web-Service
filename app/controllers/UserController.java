
package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.Service;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import models.User;
import play.api.libs.json.Json;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.loginForm;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.xml.ws.Response;

import java.util.ArrayList;
import java.util.List;

import static models.User.find;


/**
 * Created by eoghan on 16/02/2017.
 */

public class UserController extends Controller {

    private String UserName = "";
    private int Age;

   // private User dashUser;
    private FormFactory formFactory;

    @Inject
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index(){
        return ok(index.render());
    }

    /**
     * Display the 'new user form'.
     */
    public Result createUser() {
        Form<User> registerForm = formFactory.form(User.class);
        return ok(
            views.html.registerForm.render(registerForm, "Register User Here:")
        );
    }

    /**
     * Handle the 'new user form' submission
     */
    public Result saveUser() {

        List<ValidationError> errors = new ArrayList<>();
        Form<User> registerForm = formFactory.form(User.class).bindFromRequest();

        User user = null;
        String username = registerForm.get().username;
        user = User.IsUnique(username);

        if(user != null){
            System.out.println("Username not unique error");
            errors.add(new ValidationError("username", "username must be unique."));
            registerForm.errors().put("",errors);
        }
        if(registerForm.hasErrors()) {
            System.out.println("User Already Exists.");
            return badRequest(views.html.registerForm.render(registerForm, "" +username +" taken. Try different username."));
        }else {
            registerForm.get().save();
            Form<User> loginForm = formFactory.form(User.class);
            return ok(views.html.loginForm.render(loginForm, "User Created. Log In To Proceed:"));
        }
    }

    /**
     * Handle Android Registration submission.
     * The response to Volley is what is needed here.
     * Play Actions only return Result types which are HTTP codes.
     */
    public Result saveAndroidUser() {
        List<ValidationError> errors = new ArrayList<>();
        Form<User> registerForm = formFactory.form(User.class).bindFromRequest();

        User user = null;
        String username = registerForm.get().username;
        String password = registerForm.get().password;
        user = User.authenticate(username, password); //Reuse authenticate to check unique username.

        if(user != null){
            System.out.println("Username not unique error");
            errors.add(new ValidationError("username", "username must be unique."));
            registerForm.errors().put("",errors);
        }
        if(registerForm.hasErrors()) {
            System.out.println("Username Already Exists.");
            return ok("ERRORS");
        }
        registerForm.get().save();
        System.out.println("Android User added to DB.");
        return ok("USER_CREATED");
    }

    /**
     * To Domonstrate the PUT verb I have to use Ajax
     * Buttons and form only support Get & POST.
     * User is deleted by username which is still sent by Ajax
     * as a parameter in the URL. Not in Json.
     */
    public Result editUser(String username){

        Long id;
        User user = null;
        id = User.EditUser(username);
        System.out.println("in edit: " +username);
        if(id != 0) {
            Form<User> editForm = formFactory.form(User.class).fill(User.find.byId(id));
            return ok(views.html.editForm.render(editForm, "Update User:", id, username));
        }
        return(badRequest());
    }

    public Result updateUser(Long id, String username)throws PersistenceException {
        Form<User> editForm = formFactory.form(User.class).bindFromRequest();
        if(editForm.hasErrors()) {
            return badRequest(views.html.editForm.render(editForm, "Errors. Try Again.", id, username));
        }

        Transaction txn = Ebean.beginTransaction();
        try {
            User savedUser = User.find.byId(id);
            if (savedUser != null) {
                User newUserData = editForm.get();
                savedUser.name = newUserData.name;
                savedUser.age = newUserData.age;
                savedUser.username = newUserData.username;
                savedUser.password = newUserData.password;

                savedUser.update();
                flash("success", "Computer " + editForm.get().name + " has been updated");
                txn.commit();
            }
        } finally {
            txn.end();
        }
        Form<User> loginForm = formFactory.form(User.class);
        return ok(views.html.loginForm.render(loginForm, "Details Updated. Log in to proceed:"));
    }


    /**
     * To Domonstrate the DELETE verb i shave to use Ajax
     * Buttons and form only support Get & POST.
     * User is deleted by username which is still sent by Ajax
     * as a parameter in the URL. Not a Json.
     */
    public Result deleteUser(String user){

        System.out.println("in delete");

        boolean res;

        User Deluser = null;

        res = User.DeleteUser(user);
        if(res == true) {
            System.out.println("" + user + " deleted");
            // Success handled by Ajax.
            return ok("DELETED"); //Return Success.
        }else {
            return ok("Not DELETED");
        }
    }

    /**
     * Display the 'user login form'.
     */
    public Result login() {
        Form<User> loginForm = formFactory.form(User.class);
        return ok(views.html.loginForm.render(loginForm, "Enter Credentials to Log In:"));
    }

    /**
     * Validate User Login Credentials.
     */
    public Result validate(){

        Form<User> loginForm = formFactory.form(User.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(views.html.loginForm.render(loginForm, "Please Try Again:"));
        }
        else {

            User user = null;
            String username = loginForm.get().username;
            String password = loginForm.get().password;

            user = User.authenticate(username, password);

            if (user == null) {
                return ok(views.html.loginForm.render(loginForm, "User Not Found. Try Again:"));
            } else {
                session("username", user.username);
                //send user name to front end as a parameter.
                UserName = user.username;
                Age = user.age;
                return ok(views.html.dashboard.render(user.username, user.age));
            }
        }
    }

    public Result dashboard(){
        String name = UserName;
        UserName = "";
        return ok(views.html.dashboard.render(name, Age));
    }

    /**
     * Validate User Login Credentials from Android App,
     * which listens for expected responses.
     * Play route actions only return HTTP responses.
     */
    public Result androidValidate(){

        Form<User> loginForm = formFactory.form(User.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            System.out.println("Problem with credentials.");
            return (ok("NOT_FOUND"));
        }
        else {
            User user = null;
            String username = loginForm.get().username;
            String password = loginForm.get().password;

            user = User.authenticate(username, password);

            // Return Response to Android Volley.
            if (user == null) {
                System.out.println("User not found.");
                return(ok("NOT_FOUND"));
            } else {
                session("username", loginForm.get().username);
                System.out.println("User Logged in.");
              return (ok(user.username));
            }
        }
    }

    /**
     * Log user off server.
     */
    public Result logout() {
        session().clear();
        System.out.println("User Logged off.");
        return ok();
    }
}