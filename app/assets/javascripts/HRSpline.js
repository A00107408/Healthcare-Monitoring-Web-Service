/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * HighCharts Interactive Spline :-
 * http://www.highcharts.com/demo/dynamic-update
 */

var plotSpline = function(user, xs, ys){

   var r = jsRoutes.controllers.SMSController.makeSMS();

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
                   var WarningSent = false;

                   setInterval(function () {

                        var x = (new Date()).getTime(), //xs[index],
                            y = ys[index];
                        if (y < 50 ){
                            res.style.color = "blue";
                            res.innerHTML = "Heart Rate too Low. Sending SMS.";


                            var Json = " {\"bpm\": \"" +y +"\"}";
                            console.log("BPM outside Range: " +Json);
                            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Json});

                        /*    var msg = "Heart Rate too Low."
                            if(WarningSent == false){
                                BPMWarnings(msg);
                                WarningSent = true;
                                res.innerHTML = "BPM Low Warning Dispatched.";
                            }*/
                        }
                        else if (y > 160){
                            res.style.color = "red";
                            res.innerHTML = "Heart Rate too High. Sending SMS.";

                            var Jsonb = " {\"bpm\": \"" +y +"\"}";
                            console.log("BPM Outside Range: " +Jsonb);
                           // $.ajax({url: r.url, type: r.type, data: Jsonb});

                            $.ajax({url: r.url, type: r.type, contentType: "application/json", data: Jsonb});

                        /*    var msg = "Heart Rate too High.";
                            if(WarningSent == false){
                                BPMWarnings(msg);
                                WarningSent = true;
                                res.innerHTML = "BPM High Warning Dispatched.";
                            }*/
                        }
                        else{
                            res.style.color = "black";
                            res.innerHTML = "BPM in Range.";
                        }
                        series.addPoint([x, y], true, true); index++;
                   }, 600);
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

               for (i = -50; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: ys[i]
                   });
               }

               return data;
           }())
       }]
   });
};

var deleteFromMongo = function(){

    var URL = jsRoutes.controllers.HRController.deleteAll(user);

    $.ajax({
        url: URL,
        type: 'DELETE',
        success: function(result) {
            console.log("Delete Result: " +result);
        }
    });
};

var getMongoHR = function(user){

    var xs = [];
    var ys = [];
    var r = jsRoutes.controllers.HRController.findAll(user);

    // Ajax GET from MongoDB.
    $.getJSON( r.url, function( data ) {
        var xAxis = data.map(
            function(measurement) {
                return [(measurement.time)];
            }
        );
        var yAxis = data.map(
             function(measurement) {
                 return [(measurement.value)]; //Beats Per Minute
             }
        );

        //cast xAxis to DateTime for highcharts
        for (i = 0; i <= xAxis.length; i += 1) {
           xs[i] = (new Date()).getTime(xAxis[i]);
        }

        //cast yAxis from strings to array of ints for highcharts
        for (i = 0; i <= yAxis.length; i += 1) {
            ys[i] = parseInt(yAxis[i]);
        }

       // deleteFromMongo(user);
        plotSpline(user, xs, ys);
    });
};

$( document ).ready(function() {
    var uName = document.getElementById('uName').innerHTML;
    getMongoHR(uName);
});
