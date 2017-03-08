

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

    private FormFactory formFactory;

    @Inject
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index(){
        return ok(index.render("Please Login or Register to Continue."));
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

        return ok(index.render("You have been directed here."));
    }

    /**
     * Display the 'user login form'.
     */
    public Result login() {
        Form<User> loginForm = formFactory.form(User.class);
        return ok(views.html.loginForm.render(loginForm));
    }

    /**
     * Validate User Login Credentials.
     */
    public Result validate(){

        Form<User> loginForm = formFactory.form(User.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(views.html.loginForm.render(loginForm));
        }
        else {

            User user = null;
            String username = loginForm.get().username;
            String password = loginForm.get().password;

            user = User.authenticate(username, password);

            if (user == null) {
                return ok(views.html.loginForm.render(loginForm));
            } else {
                session("username", loginForm.get().username);
                return redirect(routes.DashController.dashboard());
            }
        }
    }

    /**
     * Validate User Login Credentials from Android App,
     * which listens for expected responses.
     * Play route actions only return HTTP responses.
     */
    public Result androidValidate(){

        SMSController SMS = new SMSController();

        Form<User> loginForm = formFactory.form(User.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            System.out.println("Problem with credentials.");
            return badRequest(views.html.loginForm.render(loginForm));
        }
        else {
            User user = null;
            String username = loginForm.get().username;
            String password = loginForm.get().password;

            user = User.authenticate(username, password);

            if (user == null) {
                System.out.println("User not found.");
                return ok(views.html.loginForm.render(loginForm));
            } else {
                // session("username", loginForm.get().username);
                System.out.println("User Logged in.");
                //return redirect(routes.DashController.dashboard());

              //  Result msg = SMS.sendSMS();

              //  String text = String.valueOf(msg);

              //  return(ok(text));

                return(ok("Cardiac Arrest"));
            }

            //JsonNode json = (JsonNode) Json.parse("{\"success\": \"true\"}");

            //return json;
            //  String bpmMessage = "Cardiac Arrest";

            //return(ok(bpmMessage));p
        }
    }
}