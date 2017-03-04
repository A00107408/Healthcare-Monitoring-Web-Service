package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by eoghan on 02/02/2017.
 */

public class DashController extends Controller {

    public Result dashboard(){return ok(views.html.dashboard.render("Welcome to your Dashoard!"));}
}
