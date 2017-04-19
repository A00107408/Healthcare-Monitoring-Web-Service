package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.ArrayList;

/**
 * Created by eoghan on 06/03/2017.
 * This class has two methods. Method Warning()
 * is where the warning is posted in from the front end as
 * JSON and added to a queue for sending to the App.
 * The volleyResponse() method send the warning to the app
 * for a given user.
 */

public class SMSController extends Controller {

    private String user = "NONE";
    private String msg = "OK";

    private ArrayList<String> userQueue = new ArrayList<>();
    private ArrayList<String> msgQueue = new ArrayList<>();

    /**
     * Construct SMS warning content based on JSon values
     * POSTed from the front end.
     * userQueue for .equals comparison
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
                if(msg == null) {
                    System.out.println("Missing parameter [msg]");
                }else {
                    if(userQueue.size() == 0){
                        userQueue.add(user);
                        msgQueue.add(msg);
                    }else{
                        //warning might already be queued.
                        boolean add = true;
                        for (int i = 0; i < userQueue.size(); i++) {
                            if (user.equals(userQueue.get(i))) {
                                String check = (msgQueue.get(i));
                                if (msg.equals(check)) {
                                    add = false;
                                }
                            }
                        }
                        if(add == true){
                            //append warning to end of Queues.
                            userQueue.add(user);
                            msgQueue.add(msg);
                        }
                    }
                }
            }
        }
        System.out.println("userQ: " +userQueue);
        System.out.println("msgQ: " +msgQueue);
        user = "NONE";
        msg = "OK";
        return (ok());
    }

    /**
     * Android App Volley Library polls this method
     * for a response. The response will form the SMS body.
     * It will consist of the uersername and warning message.
     * @return the SMS content to the app for transmission.
     */
    public Result volleyResponse(String username) {

        String warning = username;
        try {
            for (int i = 0; i < userQueue.size(); i++) {
                if (username.equals(userQueue.get(i))) {
                    warning = warning.concat(msgQueue.get(i));
                        userQueue.remove(i);
                        msgQueue.remove(i);
                    return ok(warning); //Exit loop with warning message for user.
                }
            }
            return (ok(user+msg));
        }catch(Exception e){
            System.out.println("Exception: "+e);
            return (ok(user+msg));
        }
    }

    /**
     * Clear Queue of warnings for given user when clear
     * button clicked in Android App.
     * @param username The Android User.
     * @return HTTP 200 to Android volley listener.
     */
    public Result Clear(String username){

        try {
            for (int i = 0; i < userQueue.size(); i++) {
                if (username.equals(userQueue.get(i))) {
                    userQueue.remove(i);
                    msgQueue.remove(i);
                }
            }
            System.out.println("userQ: "+userQueue);
            System.out.println("msgQ: "+msgQueue);
            return ok();
        }catch(Exception e){
            System.out.println("Exception: "+e);
            return badRequest();
        }
    }
}
