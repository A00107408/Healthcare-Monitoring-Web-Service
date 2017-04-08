package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Objects;

/**
 * Created by eoghan on 06/03/2017.
 */

public class SMSController extends Controller {

    private String user = "NONE";
    private String msg = "OK";

    /**
     * Construct SMS warning content based on JSon values
     * POSTed from the front end.
     * @return The SMS content to the app.
     */
    public Result Warning(){

        //System.out.println("json posted in");
        JsonNode json = request().body().asJson();
        if(json == null) {
            System.out.println("Expecting Json data.");
        } else {
            user = json.findPath("user").textValue();
            if(user == null) {
                System.out.println("Missing parameter [user]");
            } else {
                msg = json.findPath("status").textValue();
            }
        }
        return (ok());
    }

    /**
     * Android App Volley Library listens to this method
     * for a response. The response will form the SMS body.
     * It will consist of the uersername and warning message.
     * @return the SMS content to the app for transmission.
     */
    public Result volleyResponse() {

        String Warning = user;
        Warning = Warning.concat(msg);

        user = "NONE";
        msg = "OK";
        return(ok(Warning));
    }
}
