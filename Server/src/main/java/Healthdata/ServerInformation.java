package Healthdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInformation {

    private String BenchmarkStart;
    private String Provider;
    private String Region;
    private int CPULoad;

    private Timestamp CPUStartTime;
    private double CPUBenchmarkTime;
    private Timestamp CPUEndTime;

    private Timestamp FileIOStartTime;
    private double FileIOReadSpeed;
    private double FileIOWriteSpeed;
    private Timestamp FileIOEndTime;

}
