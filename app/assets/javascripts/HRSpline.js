/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * HighCharts Interactive Spline :-
 * http://www.highcharts.com/demo/dynamic-update
 *
 * Definition:
 * Tachycardia: https://en.wikipedia.org/wiki/Tachycardia
 */

var plotSpline = function(user, xs, ys, Tachycardia){

    var r = jsRoutes.controllers.SMSController.Warning();

    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    Highcharts.chart('HRSpline', {
       chart: {
           type: 'spline',
           animation: Highcharts.svg, // don't animate in old IE
           marginRight: 10,
           events: {
               load: function () {

                   // set up the updating of the chart each second
                   var series = this.series[0];
                   var index=0;
                   var res = document.getElementById('HRMessage');

                   fetchSleep(); // kick start sleep check.

                   setInterval(function () {

                        var lowerBPM = $('#lowerBPM').val();
                        var upperBPM = $('#upperBPM').val();
                        var Json;

                        var x = (new Date()).getTime(), //xs[index],
                            y = ys[index];

                        res.style.color = '#89C057';
                        res.innerHTML = "BPM in Range.";

                        if (y < lowerBPM ){
                            res.style.color = "blue";
                            res.innerHTML = "Heart Rate Below Set Threshold: SMS Sent.";
                            Json = " {\"user\": \"" +user +"\"," +" \"status\": \"LOW\"}";
                            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                            console.log("BPM outside Range for " +user +". Warning sent.");
                        }
                        if(y > upperBPM){
                            res.style.color = "red";
                            res.innerHTML = "" +uName +"'s BPM Above Set Threshold: SMS Sent.";
                            Json = " {\"user\": \"" +user +"\"," +" \"status\": \"HIGH\"}";
                            console.log("BPM outside Range for " +user +". Warning sent.");
                            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                        }

                        if(y === 0){
                            //wearer died in transit??? Still Moving re: accelerometer??
                            res.style.color = "black";
                            res.innerHTML = "" +uName +"has died. SMS sent to ICE";
                            Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"DEAD\"}";
                            console.log("" +uName +" has died. Message sent.");
                            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                        }

                        // Special Cases.
                        // If user is 'Asleep'.
                        // Will take precedent over larger thresholds.
                        if($('#asleep').prop('checked') === true){
                            if(y < 40){  //Bradycardia
                                if(y === 0){
                                    //wearer dead.
                                    res.style.color = "black";
                                    res.innerHTML = "" +uName +"has died. SMS sent to ICE";
                                    Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"DEAD\"}";
                                    console.log("" +uName +" has died. Message sent.");
                                    $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                                }else{
                                    res.style.color = "blue";
                                    res.innerHTML = "Bradycardia Detected: SMS Sent.";
                                    Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"BRADY\"}";
                                    console.log("" +uName +" has Bradycardia. Message sent. BPM: " +y);
                                    $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                                }
                            }
                            if(y > Tachycardia){
                                res.style.color = '#B33A3A';
                                res.innerHTML = "Tachycardia Detected: SMS Sent.";
                                Json = " {\"user\": \"" +uName +"\"," +" \"status\": \"TACHY\"}";
                                console.log("" +uName +" has Tachycardia. Message sent. BPM: " +y);
                                $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});
                            }
                        }

                        series.addPoint([x, y], true, true);
                        index++;
                   }, 1000);
               }
           }
       },
       title: {
           text: 'Live Heart Rate'
       },
       xAxis: {
           type: 'datetime',
           tickPixelInterval: 150
       },
       yAxis: {
           title: {
               text: 'Beats Per Minute'
           },
           plotLines: [{
               value: 0,
               width: 1,
               color: '#808080'
           }]
       },
       tooltip: {
           formatter: function () {
               return '<b>' + this.series.name + '</b><br/>' +
                   Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                  // Highcharts.dateFormat(globalTime) + '<br/>' +
                   Highcharts.numberFormat(this.y, 2);
           }
       },
       legend: {
           enabled: false
       },
       exporting: {
           enabled: false
       },
       credits: {
             enabled: false
       },
       series: [{
           name: 'Heart Rate Data',
           data: (function () {

               // initialise chart history
               var data = [],
                   time = (new Date()).getTime(),
                   i;

               for (i = -30; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: parseInt(ys[i])
                   });
               }
               return data;
           }())
       }]
   });
};

//Get Heart Rates from MognoDB
//Put them into arrays for highCharts.
var getMongoHR = function(user){

    // Wait for DOM to load
    // Typedef problems otherwise.
    $( document ).ready(function(){});

    var xs = [];
    var ys = [];
    var r = jsRoutes.controllers.HRController.findAll(user);

    //var age = document.getElementById('age').innerHTML;
    var Tachycardia;

    //https://en.wikipedia.org/wiki/Tachycardia
    switch(true){
        case (age >= 15): Tachycardia = 100; break;
        case (age < 15 && age > 12) : Tachycardia = 119; break;
        case (age <= 12 && age > 8) : Tachycardia = 130; break;
        case (age <= 8 && age > 5) : Tachycardia = 133; break;
        case (age <= 5 && age > 3) : Tachycardia = 137; break;
        case (age <= 3 && age >= 1) : Tachycardia = 151; break;
    }

    console.log("Tach: " +Tachycardia);

    // Ajax GET from MongoDB.
    $.getJSON( r.url, function( data ) {
        var xAxis = data.map(
            function(measurement) {
                return [(measurement.time)];
            }
        );
       // var yAxis=0;
        var yAxis = data.map(
             function(measurement) {
                 return [(measurement.value)]; //Beats Per Minute
             }
        );

        //TODO
        //Code Smell
        //Json not read from MongoDB yet.
        // Wait 1 sec and try again.
        if(yAxis.length < 1){
            setTimeout(function() { getMongoHR(user); },1000);
            return;
        }

        //cast xAxis to DateTime for highcharts
        for (i = 0; i < xAxis.length; i += 1) {
           xs[i] = (new Date()).getTime(xAxis[i]);
        }

        //cast yAxis from strings to array of ints for highcharts
        for (i = 0; i < yAxis.length; i += 1) {
            ys[i] = parseInt(yAxis[i]);
        }

        plotSpline(user, xs, ys, Tachycardia);
    });
};
