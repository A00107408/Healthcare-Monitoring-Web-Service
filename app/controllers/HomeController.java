package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * Created by eoghan on 20/03/2017.
 */

public class HomeController extends Controller {

    public Result index() {

        return ok(index.render());
    }

    public Result aboutUs() {
        return ok(aboutUs.render());
    }

    public Result contactUs(){
        return ok(contactUs.render());
    }
}
