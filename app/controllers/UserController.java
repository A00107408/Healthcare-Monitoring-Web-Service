
package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;


/**
 * Created by eoghan on 16/02/2017.
 */

public class UserController extends Controller {

    private String UserName = "";
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
            views.html.registerForm.render(registerForm)
        );
    }

    /**
     * Handle the 'new user form' submission
     */
    public Result saveUser() {
        Form<User> registerForm = formFactory.form(User.class).bindFromRequest();
        if(registerForm.hasErrors()) {
            return badRequest(views.html.registerForm.render(registerForm));
        }
        registerForm.get().save();
        Form<User> loginForm = formFactory.form(User.class);
        return ok(views.html.loginForm.render(loginForm, "User Created. Log In To Proceed:"));
    }

    /**
     * Handle Android Registration submission.
     * The reponse to Volley is what is needed here.
     * Play Actions only return Result types which are HTTP codes.
     */
    public Result saveAndroidUser() {
        Form<User> registerForm = formFactory.form(User.class).bindFromRequest();
        if(registerForm.hasErrors()) {
            System.out.println("Error in user create form.");
            return ok("ERRORS");
        }
        registerForm.get().save();
        System.out.println("Android User added to DB.");
        return ok("USER_CREATED");
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
                session("username", loginForm.get().username);
                //send user name to front end as a parameter.
                UserName = username;
                return ok(views.html.dashboard.render(username));
            }
        }
    }

    public Result dashboard(){
        String name = UserName;
        UserName = "";
        return ok(views.html.dashboard.render(name));
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
              return (ok("USER_FOUND"));
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