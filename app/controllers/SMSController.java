package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by eoghan on 06/03/2017.
 */

public class SMSController extends Controller {

    private String msg = "";

    public Result sendSMS(){

        JsonNode json = request().body().asJson();
        if(json == null) {
            System.out.println("Expecting Json data.");
        } else {
            String bpm = json.findPath("bpm").textValue();
            if(bpm == null) {
                System.out.println("Missing parameter [bpm]");
            } else {
                System.out.println("sending SMS");
                msg = "Warning! User X BPM: " + bpm;
            }
        }
        return (ok(msg));
    }
}
