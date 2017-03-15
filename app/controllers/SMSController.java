package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Objects;

/**
 * Created by eoghan on 06/03/2017.
 */

public class SMSController extends Controller {

   private String msg = "OK";
   private String bpm = "0";

    /**
     * Construct SMS warning content based on JSon values
     * POSTed from the front end.
     * @return The SMS content to the app.
     */
    public Result makeSMS(){

        System.out.println("json posted in");
        JsonNode json = request().body().asJson();
        if(json == null) {
            System.out.println("Expecting Json data.");
        } else {
            bpm = json.findPath("bpm").textValue();
            if(bpm == null) {
                System.out.println("Missing parameter [bpm]");
            } else {
                System.out.println("sending SMS");
                msg = "Cardiac Arrest";
            }
        }
        return (ok());
    }

    /**
     *
     * @return the SMS content to the app for transmission.
     */
    public Result sendSMS() {

        String Warning;
        Warning = msg;
        msg = "OK";
        return(ok(Warning));
    }
}
