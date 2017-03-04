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
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.registerForm.render(userForm)
        );
    }

    /**
     * Handle the 'new user form' submission
     */
    public Result saveUser() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if(userForm.hasErrors()) {
            return badRequest(views.html.registerForm.render(userForm));
        }
        userForm.get().save();

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
}
