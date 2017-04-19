/*
    A00107408 20-02-17.
    Get Oauth 2.0 token and fetch data from cloud.
    Code seed taken from FitBit resources:
    https://dev.fitbit.com/docs/community-resources/   *Javascript*
    https://github.com/jeremiahlee/fitbit-web-demo
*/

var fitbitAccessToken;
var uName;

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

// Extract BPM and time from JSon recieved.
var formatHeartRate = function(timeSeries) {

    //append the username to the data.
    var user = uName;//document.getElementById('uName').innerHTML;

    return timeSeries['activities-heart-intraday'].dataset.map(
        //var Json = Json.stringify(measurement);
        function(measurement) {
            return [
                    ("{ \"user\":\"" +user + "\""),
                    ("\"time\":\"" +measurement.time + "\""),
                    ("\"value\":" +measurement.value + " }")
            ];
        }
    );
};

var wrapSendJson = function(timeSeries){

    // Json.Stringify doesn't add '[ ]'
    // expected by the server.
    // Use JSRouter to POST Json via AJAX.
    var JsonString = "[";
    JsonString = JsonString.concat(timeSeries);
    JsonString = JsonString.concat("]");

    //console.log(JsonString);

    // POST JSon to MongoDB
    var r = jsRoutes.controllers.HRController.createBulkFromJson();
    $.ajax({url: r.url, type: r.type, contentType: "application/json", data: JsonString });

    //var uName = document.getElementById('uName').innerHTML;

    //Once JSon sent to MongoDB, fetch it back out and Graph it.
    //Defined in HRSpline.js.
    getMongoHR(uName);
};



// Use fetch API to GET Heart Rates from cloud.
// fetch not compatible with IE.
// Use token in header for OAuth 2.0 authentication.
fetch(
    'https://api.fitbit.com/1/user/-/activities/heart/date/2016-07-19/1d/1sec/time/06:00:00/6:15:00.json',
   //'https://api.fitbit.com/1/user/-/activities/heart/date/2017-03-22/1d/1sec/time/06:00:00/07:00:00.json',
    {
        headers: new Headers({
            'Authorization': 'Bearer ' + fitbitAccessToken
        }),
        mode: 'cors',
        method: 'GET'
    }
).then(processResponse)             //Currying of functions
.then(formatHeartRate)              //returns Json response
.then(wrapSendJson)                 //to next function for
.catch(function(error) {            //processing, catches
    console.log(error);             //any errors.
});

$( document ).ready(function(){

    uName = document.getElementById('uName').innerHTML;
    var d = jsRoutes.controllers.UserController.deleteUser(uName);
    var u = jsRoutes.controllers.UserController.editUser(uName);


    // Using ajax to demo DELETE verb.
    //Forms and buttons only support GET and POST.
    $('#delete').click(function(){

         if (window.confirm("Are you sure you want to permanently delete " +uName)){
            $.ajax({
                url: d.url ,
                type: d.type,
                success: function(data, textStatus, jqXHR){
                    console.log("deleted successfully");
                    window.location.href="http://localhost:9000/";
                    alert(''+uName +' deleted. \nThank you for using Pulse Services.');
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert('delete user error: ' + textStatus);
                }
            });
        }
    });

    // Using ajax to demo PUT verb.
    //Forms and buttons only support GET and POST.
  /*  $('#update').click(function(){

        var Json = " {\"user\": \"" +uName +"\"}";

            $.ajax({
                url: u.url ,
                type: "PUT",
                contentType: "application/json",
                data: Json,
                success: function(data, textStatus, jqXHR){
                    console.log("updated successfully");
                    window.location.href="http://localhost:9000/editForm";
                   // alert(''+uName +' deleted. \nThank you for using Pulse Services.');
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert('update user error: ' + textStatus);
                }
            });
    });*/
});