/*NEED TO OPEN CHROME WITH THIS TERMINAL COMMAND:
open -a Google\ Chrome --args --disable-web-security --user-data-dir*/


var DataPoints = [];
var Time = [];

function Update() {

    var request = new XMLHttpRequest();
    request.open('GET', 'http:/localhost:8080/getAggregatedData', true);
    request.onload = function getDataPoint() {
      var data = JSON.parse(this.response);
      document.getElementById("cpuaverage").innerHTML = data.cpuaverage;

      var today = new Date();
      var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();

      DataPoints.push(data.cpuaverage);
      Time.push(time);
      console.log(DataPoints);
      if (DataPoints.length > 20) {
        DataPoints.shift();
        Time.shift();
      }

      let myChart = document.getElementById("myChart").getContext('2d');
      let massPopChart = new Chart(myChart, {
        type: 'line',
        data: {
            labels: Time,
            datasets: [{
                label: 'CPU %',
                data: DataPoints,
                backgroundColor: '#00B9FC',
                hoverBackgroundColor: '#00B9FC'
            }]
         },
        options: {
            responsive: false,
            title: {
                display: true,
                text: 'CPU LOAD %',
                fontSize:25
            },
            animation: {
                duration: 0
            },
            scales: {
               yAxes: [{
                  ticks: {
                     min: 0,
                     max: 100
                  },
                  scaleLabel: {
                      display: true,
                      labelString: '%'
                    }
               }],
               xAxes: [{
                 scaleLabel: {
                         display: true,
                         labelString: 'Time'
                       }
                  }],

            }
        }
      });

    }
    request.send();
}


setInterval(Update, 1000);





