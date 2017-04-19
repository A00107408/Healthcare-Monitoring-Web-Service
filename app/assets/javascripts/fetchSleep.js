/*
    A00107408 20-02-17.
    Code seed taken from FitBit resources:
    https://dev.fitbit.com/docs/community-resources/   *Javascript*
    https://github.com/jeremiahlee/fitbit-web-demo

    FitBit only accept sleep log from a watch.
    FitBit log starts and ends with sleep value of 1 = asleep. ???
*/

var fitbitAccessToken;

// Get FitBit OAuth 2.0 token.
// Using my app id = 2282KN.
// Request access to all data entry points and redirect to localhost/dashboard.
// If user hasn't authed with Fitbit, redirect to Fitbit OAuth Implicit Grant Flow
if (!window.location.hash) {
    window.location.replace('https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=2282KN&redirect_uri=http%3A%2F%2Flocalhost:9000/dashboard&scope=activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight&expires_in=604800');
} else {
    var fragmentQueryParameters = {};
    window.location.hash.slice(1).replace(
        new RegExp("([^?=&]+)(=([^&]*))?", "g"),
        function($0, $1, $2, $3) { fragmentQueryParameters[$1] = $3; }
    );

    fitbitAccessToken = fragmentQueryParameters.access_token;
}

// Check for response from the cloud.
// Ensure Json body content.
var processResponse = function(res) {
    if (!res.ok) {
        throw new Error('Fitbit API request failed: ' + res);
    }
    var contentType = res.headers.get('content-type');
    if (contentType && contentType.indexOf("application/json") !== -1) {
        return res.json();
    } else {
        throw new Error('JSON expected but received ' + contentType);
    }
};

//Is wearer awake or asleep?
var sleepStatus;

// Extract sleep status and time from JSon received.
var extractStatus = function(timeSeries) {

    //FitBit API sleep log starts AND ends with sleep state.
    //For Demo, generate random awake state.
    var x = Math.random(); //between 0 & 1.
    if(x > 0.5){ //50% chance of wearer waking up.
        sleepStatus = 3; // generate random awake for demo.
    }else{
        return timeSeries.sleep[0].minuteData.map(
            function(measurement) {
                //return JSON.stringify({time:measurement.dateTime,value:measurement.value});

                //iterate array to latest sleep status and return it.
                //will be either awake or asleep during live recording.
                sleepStatus =(measurement.value); //will be asleep during demo.
                return sleepStatus;
            }
        );
    }
};

//Curried function takes in returned value.
var curSleepStatus = function(timeSeries){

    var r = jsRoutes.controllers.SMSController.Warning();
    var uName = document.getElementById('uName').innerHTML;
    var Json;

    //allow user AND automated status change.
    if(document.getElementById('asleep').checked){ //jQuery not working here
        asleep = true;
    }
    if(document.getElementById('awake').checked){  //jQuery not working here
        asleep = false;
    }

    //FitBit API documentation. 1 = asleep.
    if(sleepStatus == 1){
        if(asleep === false){
            Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"ASLEEP\"}";
            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
            console.log("" +uName +" fell asleep. Message sent.");
        }
        $('#asleep').prop('checked', true);
        asleep = true;
        console.log(""+uName +" is asleep.");
    }

    if(sleepStatus == 3){ // 3 = awake.
        if (asleep === true){
            //send sms
            Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"AWAKE\"}";
            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
            console.log("" +uName +" woke up. Message sent.");
        }
        $('#awake').prop('checked', true);
        asleep = false;
        console.log(""+uName +" is awake.");
    }
};


// Use new fetch API to GET Sleep from cloud.
// fetch not compatible with IE.
// Use token in header for OAuth 2.0 authentication.
var fetchSleep = function(){

  /*  var dateObj = new Date();
    var month = dateObj.getUTCMonth() + 1; //months from 1-12
    var day = dateObj.getUTCDate();
    var year = dateObj.getUTCFullYear();

    var date = year + "-" + month + "-" + day;*/
    //console.log("sleep date: " +date);

    fetch(
       'https://api.fitbit.com/1/user/-/sleep/date/2017-03-17.json',
      //'https://api.fitbit.com/1/user/-/2017-03-22/date/today.json',
        {
            headers: new Headers({
                'Authorization': 'Bearer ' + fitbitAccessToken
            }),
            mode: 'cors',
            method: 'GET'
        }
    ).then(processResponse)             //Currying of functions
    .then(extractStatus)                //returns Json response
    .then(curSleepStatus)               //to next function for
    .catch(function(error) {            //processing, catches
        console.log(error);             //any errors.
    });

     //check sleep status every 25 seconds. If wearer doesn't
     //move at all for 25 seconds they are probably asleep.
     //OR WORSE ?!?
     //FitBit API allows 150 calls per hour.
     setTimeout(function() { fetchSleep(); },30000);
};

/*$( document ).ready(function() {
    fetchSleep();
});*/