/*NEED TO OPEN CHROME WITH THIS TERMINAL COMMAND:
open -a Google\ Chrome --args --disable-web-security --user-data-dir*/


var DataPoints = [];


function Update() {
    var request = new XMLHttpRequest();
    request.open('GET', 'http:/localhost:8080/getAggregatedData', true);
    request.onload = function getDataPoint() {
      var data = JSON.parse(this.response);
      document.getElementById("cpuaverage").innerHTML = data.cpuaverage;
      DataPoints.push(data.cpuaverage);
      console.log(DataPoints);
      if (DataPoints.length > 20) {
        DataPoints.shift();
      }

      let myChart = document.getElementById("myChart").getContext('2d');
      let massPopChart = new Chart(myChart, {
        type: 'line',
        data: {
            labels: [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],
            datasets: [{
                label: 'CPU %',
                data: DataPoints,
                backgroundColor: '#00B9FC'
            }]
         },
        options: {
            title: {
                display: true,
                text: 'CPU LOAD %',
                fontSize:25
            },
            layout: {
                padding: {
                    bottom: 800
                }
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





