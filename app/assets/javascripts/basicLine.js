/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * HighCharts Basic Line :-
 * http://www.highcharts.com/demo/line-basic
 */

var plotLine = function(user, xs, ys){

    Highcharts.chart('container2', {

         title: {
             text: ''+user +'\'s Stats - 2017'
         },

         subtitle: {
             text: 'Pulse Services'
         },

         yAxis: {
             title: {
                 text: '' +user +'\'s Telemetry'
             }
         },
         legend: {
             layout: 'vertical',
             align: 'right',
             verticalAlign: 'middle'
         },

         plotOptions: {
             series: {
                 pointStart: 2008
             }
         },

         series: [{
             name: 'Distance',
             data: [17, 19, 28, 20, 23, 18, 19, 25]
         },/* {
             name: 'Steps',
             data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
         }, {
             name: 'Weight',
             data: [11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387]
         },*/ {
             name: 'Weight',
             data: [22.5, 22.4, 22.3, 21.7, 22.6, 22.4, 23, 22.8]
         }, {
             name: 'Calories Burned',
             //data: [12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111]
             data: [ys[0], ys[11], ys[12], ys[13], ys[14], ys[15], ys[16], ys[17]]
         }]

    });
};

//Get calorie data from MongoDB
//Convert to arrays for HighCharts basicLine
 var getMongoCal = function(user){

     var xs = [];
     var ys = [];
     var r = jsRoutes.controllers.CalorieController.findAll(user);

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

         //Json not read yet. Wait 1 sec and try again.
         if(yAxis.length < 1){
             window.setTimeout(getMongoCal(user),1000);
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
         plotLine(user, xs, ys);
     });
 };