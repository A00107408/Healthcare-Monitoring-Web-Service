package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Result;

import java.util.Objects;

import static play.mvc.Controller.request;
import static play.mvc.Results.ok;

/**
 * Created by eoghan on 15/03/2017.
 */
public class Message extends Thread{

    String msg = "OK";

    @Override
    public void run(){
        synchronized(this){
            System.out.println("called run");

            while(Objects.equals("OK", msg)) {
                try {
                    System.out.println("sleeping for 5secs");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("couldnt sleep.");
                    e.printStackTrace();
                }
            }
            notify();
        }
    }

    public Result makeSMS(){
        System.out.println("json posted in");
        JsonNode json = request().body().asJson();
        if(json == null) {
            System.out.println("Expecting Json data.");
        } else {
            String bpm = json.findPath("bpm").textValue();
            if(bpm == null) {
                System.out.println("Missing parameter [bpm]");
            } else {
                System.out.println("sending SMS");
                msg = "Cardiac Arrest";
                run();
            }
        }
        return (ok());
    }
}
