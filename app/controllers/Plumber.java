package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by eogha on 01/03/2017.
 */
public class Plumber extends Controller {

    public Result stuff(){

        return ok(views.html.plumber.render("plumber via get"));
    }

    public Result stuffb(String i){
        System.out.println("i: " +i);
        return ok(views.html.plumber.render("plumber via post"));
    }
}
