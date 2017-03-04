/*
    A00107408 27-02-2017.
    Plot Heart rates on interactive graph.
    Uses Highchart's spline
    http://www.highcharts.com/demo/dynamic-update
*/

var plotSpline = function(xs, ys){

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

                   setInterval(function () {

                        var x = (new Date()).getTime(), //xs[index],
                            y = ys[index];
                        if (y < 50 ){
                            res.style.color = "blue";
                            res.innerHTML = "Heart Rate too Low.";
                        }
                        else if (y > 165){
                            res.style.color = "red";
                            res.innerHTML = "Heart Rate too High.";
                        }
                        else{
                            res.style.color = "black";
                            res.innerHTML = "BPM in Range."
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

var getMongoHR = function(){

    var xs = [];
    var ys = [];
    var r = jsRoutes.controllers.HRController.findAll();

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



        if(yAxis === null){console.log("yAxis is null");}               //remove
        plotSpline(xs, ys);
    });
}
