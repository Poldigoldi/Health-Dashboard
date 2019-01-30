package hello.HealthData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInformation {

    private String StartTestTime;
    private String Provider;
    private String Region;
    private int CPULoad;

}
