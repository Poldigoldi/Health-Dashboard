package hello.HealthData;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@RestController
public class DataController {

    @Autowired
    public ServerInformationRepository repository;

    private int id;
    private String StartTestTime;
    private String Provider;
    private String Region;
    private int CPULoad;


    @RequestMapping(value = "/postData", method = RequestMethod.POST)
    public ServerInformation serverInformation(@RequestBody String JsonInfoString) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ServerInformation serverInformation = objectMapper.readValue(JsonInfoString, ServerInformation.class);
        repository.save(serverInformation);

        if (repository.count() > 1000) {
            repository.deleteAll();
        }

        return new ServerInformation(StartTestTime, Provider, Region, CPULoad);
    }


    @RequestMapping(value = "/getAggregatedData", method = RequestMethod.GET)
    public AggregatedData aggregatedData() throws IOException {
        double CPUAverage;
        double Sum = 0;
        for (ServerInformation serverInformation : repository.findAll()) {
            //Sum = Sum + serverInformation.getCPULoad();
            Sum = serverInformation.getCPULoad();
        }
        //CPUAverage = Sum / repository.count();
        CPUAverage = Sum;

        return new AggregatedData(CPUAverage);
    }
}