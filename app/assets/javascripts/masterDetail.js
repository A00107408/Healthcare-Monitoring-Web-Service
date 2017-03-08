/* Student: A00107408
 * Date: 2016-2017
 * Project: Msc Software Engineering Project.
 * College: Athlone Institute of Technology.
 *
 * Credits:
 * HighCharts Master-Detail Chart :-
 * http://www.highcharts.com/demo/dynamic-master-detail
 */

var masterChart, detailChart;

var plotMaster = function(data){

    // create the master chart
    function createMaster() {
        masterChart = new Highcharts.Chart({
            chart: {
                renderTo: 'master-container',
                reflow: false,
                borderWidth: 0,
                backgroundColor: null,
                marginLeft: 50,
                marginRight: 20,
                zoomType: 'x',
                events: {

                    // listen to the selection event on the master chart to update the
                    // extremes of the detail chart
                    selection: function(event) {
                        var extremesObject = event.xAxis[0],
                            min = extremesObject.min,
                            max = extremesObject.max,
                            detailData = [],
                            xAxis = this.xAxis[0];

                        // reverse engineer the last part of the data
                        jQuery.each(this.series[0].data, function(i, point) {
                            if (point.x > min && point.x < max) {
                                detailData.push({
                                    x: point.x,
                                    y: point.y
                                });
                            }
                        });

                        // move the plot bands to reflect the new detail span
                        xAxis.removePlotBand('mask-before');
                        xAxis.addPlotBand({
                            id: 'mask-before',
                            from: Date.UTC(2017, 03, 13),
                            to: min,
                            color: 'rgba(0, 0, 0, 0.2)'
                        });

                        xAxis.removePlotBand('mask-after');
                        xAxis.addPlotBand({
                            id: 'mask-after',
                            from: max,
                            to: Date.UTC(2017, 03, 13),
                            color: 'rgba(0, 0, 0, 0.2)'
                        });


                        detailChart.series[0].setData(detailData);

                        return false;
                    }
                }
            },
            title: {
                text: null
            },
            xAxis: {
                type: 'datetime',
                showLastTickLabel: true,
                maxZoom: 14 * 24 * 3600000, // fourteen days
                plotBands: [{
                    id: 'mask-before',
                    from: Date.UTC(2017, 3, 13),
                    to: Date.UTC(2017, 3, 13),
                    color: 'rgba(0, 0, 0, 0.2)'
                }],
                title: {
                    text: null
                }
            },
            yAxis: {
                gridLineWidth: 0,
                labels: {
                    enabled: false
                },
                title: {
                    text: null
                },
                min: 0.6,
                showFirstLabel: false
            },
            tooltip: {
                formatter: function() {
                    return false;
                }
            },
            legend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            plotOptions: {
                series: {
                    fillColor: {
                        linearGradient: [0, 0, 0, 70],
                        stops: [
                            [0, '#4572A7'],
                            [1, 'rgba(0,0,0,0)']
                        ]
                    },
                    lineWidth: 1,
                    marker: {
                        enabled: false
                    },
                    shadow: false,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    enableMouseTracking: false
                }
            },

            series: [{
                type: 'area',
                name: 'Heart Rate',
                pointInterval: 24 * 3600 * 1000,
                pointStart: Date.UTC(2006, 0, 01),
                data: data
            }],

            exporting: {
                enabled: false
            }

        }, function(masterChart) {
            createDetail(masterChart);
        });
    }

    // create the detail chart
    function createDetail(masterChart) {

        // prepare the detail chart
        var detailData = [],
            detailStart = Date.UTC(2017, 3, 13);

        jQuery.each(masterChart.series[0].data, function(i, point) {
            if (point.x >= detailStart) {
                detailData.push(point.y);
            }
        });

        // create a detail chart referenced by a global variable
        detailChart = new Highcharts.Chart({
            chart: {
                marginBottom: 120,
                renderTo: 'detail-container',
                reflow: false,
                marginLeft: 50,
                marginRight: 20,
                style: {
                    position: 'absolute'
                }
            },
            credits: {
                enabled: false
            },
            title: {
                text: 'Historical Heart Rate'
            },
            subtitle: {
                text: 'Select an area by dragging across the lower chart'
            },
            xAxis: {
                type: 'datetime'
            },
            yAxis: {
                title: null,
                maxZoom: 0.1
            },
            tooltip: {
                formatter: function() {
                    var point = this.points[0];
                    return '<b>'+ point.series.name +'</b><br/>'+
                        Highcharts.dateFormat('%A %B %e %Y', this.x) + ':<br/>'+
                        'B.P.M: '+ Highcharts.numberFormat(point.y, 2);
                },
                shared: true
            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    marker: {
                        enabled: false,
                        states: {
                            hover: {
                                enabled: true,
                                radius: 3
                            }
                        }
                    }
                }
            },
            series: [{
                name: 'B.P.M',
                pointStart: detailStart,
                pointInterval: 24 * 3600 * 1000,
                data: detailData
            }],

            exporting: {
                enabled: false
            }

        });
    }

    // make the container smaller and add a second container for the master chart
    var $container = $('#container')
        .css('position', 'relative');

    var $detailContainer = $('<div id="detail-container">')
        .appendTo($container);

    var $masterContainer = $('<div id="master-container">')
        .css({ position: 'absolute', top: 300, height: 80, width: '100%' })
        .appendTo($container);

    // create master and in its callback, create the detail chart
    createMaster();
};

var getMongoMasterHR = function(){

    var xs = [];
    var ys = [];
    var r = jsRoutes.controllers.HRController.readHistoricalHR();

    // Ajax GET from MongoDB.
    $.getJSON( r.url, function( data ) {
        var xAxis = data.map(
            function(measurement) {
                return [(measurement.time)];
            } //return new Date()).getTime();
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

       /* var arr = [];
        for(var x = 0; x < 100; x++){
            arr[x] = [];
            for(var y = 0; y < 100; y++){
                arr[x][y] = x*y;
            }
        }*/

        plotMaster(ys);
    });
};

/*$( document ).ready(function() {
    getMongoMasterHR();
});*/
