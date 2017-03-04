var doPost = function(){
    console.log("in doPost");

    var MyJson = {"a":4, "b":5};
    console.log("myJson: " +MyJson);

    $.ajax({
        type: 'POST',
        //contentType: 'application/json',
        //contentType: 'text/plain',
        url: "http://localhost:8000/sum",
       // dataType: "json",
        data: MyJson,
        success: function(results){
           alert('great success!' +results);
        },
        error: function(results){
            alert('FAK!');
        }
    });

}

$(document).ready(function(){
    doPost();
});