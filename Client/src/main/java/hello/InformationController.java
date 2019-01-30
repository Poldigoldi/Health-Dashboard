package hello;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;


@Component
public class InformationController {

    @Scheduled(cron = "*/1 * * * * *")
    public void postInformation() throws IOException {
        String url = "http://localhost:8080/postData";

        Timestamp StartTestTime = new Timestamp(System.currentTimeMillis());
        String Provider = "AWS";
        String Region = "UK";
        int CPULoad = (int)(Math.random() * 100 + 1);

        ServerInformation serverInformation = new ServerInformation(
                StartTestTime.toString(),
                Provider,
                Region,
                CPULoad
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String JsonInfoString = objectMapper.writeValueAsString(serverInformation);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(JsonInfoString);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println("Response code: " + response.getStatusLine().getStatusCode());
        client.close();
    }
}
