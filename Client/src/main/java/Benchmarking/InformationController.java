package Benchmarking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Timestamp;

@Component
public class InformationController {
    @Autowired
    public void postInformation() {
        Timestamp BenchmarkStart = new Timestamp(System.currentTimeMillis());
        String Provider = "AWS";
        String Region = "UK";
        int CPULoad = (int)(Math.random() * 100 + 1);

        String CPUBenchmark = "CPUBenchmark.txt";
        String FileIOBench = "FileIOBenchmark.txt";

        Timestamp CPUStartTime = new Timestamp(System.currentTimeMillis());
        CPUBenchmarkTEST(CPUBenchmark);
        Timestamp CPUEndTime = new Timestamp(System.currentTimeMillis());
        Timestamp FileIOStartTime = new Timestamp(System.currentTimeMillis());
        FileIOBenchmarkTEST(FileIOBench);
        Timestamp FileIOEndTime = new Timestamp(System.currentTimeMillis());

        System.out.println("All Benchmarking test done!");
        System.out.println("CPU Time: " + pullOutCPUBenchmarkData(CPUBenchmark));
        System.out.println("Read: " + FileIOTimes(FileIOBench)[0] + " MiB/s");
        System.out.println("Write: " + FileIOTimes(FileIOBench)[1] + " MiB/s");

        ServerInformation serverInformation = new ServerInformation(
                BenchmarkStart.toString(),
                Provider,
                Region,
                CPULoad,
                CPUStartTime,
                pullOutCPUBenchmarkData(CPUBenchmark),
                CPUEndTime,
                FileIOStartTime,
                FileIOTimes(FileIOBench)[0],
                FileIOTimes(FileIOBench)[1],
                FileIOEndTime
        );

        try {
            PostObject(serverInformation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CPUBenchmarkTEST(String CPUBenchmark) {
        ProcessBuilder cpuBenchmark = new ProcessBuilder("sysbench", "cpu", "--cpu-max-prime=20000", "run");
        cpuBenchmark.redirectOutput(new File(CPUBenchmark));
        cpuBenchmark.redirectError(new File(CPUBenchmark));
        try {
            Process p = cpuBenchmark.start(); // may throw IOException
            System.out.println("CPU Benchmark Test Started ...");
            p.waitFor();
            System.out.println("CPU Benchmark Test Finished!");
        } catch (IOException | InterruptedException e) {
            System.out.println("There was a problem with the CPU Benchmark Test!");
            e.printStackTrace();
        }

        ProcessBuilder prepareFiles = new ProcessBuilder("sysbench", "fileio", "--file-total-size=2G", "prepare");
        try {
            Process p = prepareFiles.start(); // may throw IOException
            System.out.println("Preparing files for FileIOBenchmark ...");
            p.waitFor();
            System.out.println("All files prepared!");
        } catch (IOException | InterruptedException e) {
            System.out.println("There was a problem with preparing the files for the FilIO Benchmark Test!");
            e.printStackTrace();
        }
    }

    private void FileIOBenchmarkTEST(String FileIOBench) {
        ProcessBuilder FileIOBenchmark = new ProcessBuilder("sysbench", "fileio", "--file-total-size=2G", "--file-test-mode=rndrw", "--time=300", "--max-requests=0", "run");
        FileIOBenchmark.redirectOutput(new File(FileIOBench));
        FileIOBenchmark.redirectError(new File(FileIOBench));
        try {
            Process p = FileIOBenchmark.start(); // may throw IOException
            System.out.println("FileIO Benchmark Test Started. This may take up to 10 minutes ...");
            p.waitFor();
            System.out.println("FileIO Benchmark Test Finished!");
        } catch (IOException | InterruptedException e) {
            System.out.println("There has been a problem during the FileIO Benchmark Test");
            e.printStackTrace();
        }

        ProcessBuilder FileCleanUp = new ProcessBuilder("sysbench", "fileio", "--file-total-size=2G", "cleanup");
        try {
            Process p = FileCleanUp.start(); // may throw IOException
            System.out.println("Cleaning up files ...");
            p.waitFor();
            System.out.println("Files deleted!");
        } catch (IOException | InterruptedException e) {
            System.out.println("There has been a problem with deleting the files!");
            e.printStackTrace();
        }
    }


    private double pullOutCPUBenchmarkData(String CPUBenchmark) {
        String line;
        double CPUBenchmarkTime = 0;

        try {
            FileReader fileReader = new FileReader(CPUBenchmark);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                if (line.contains("total time: ")) {
                    line = line.replaceAll("\\s+", "");
                    line = line.substring(line.lastIndexOf(":") + 1);
                    line = line.substring(0, line.length() - 1);
                    CPUBenchmarkTime = Double.parseDouble(line);
                }
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            CPUBenchmark + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + CPUBenchmark + "'");
        }
        return CPUBenchmarkTime;
    }


    private double[] FileIOTimes(String FileIOBench) {
        String line;
        double read = 0;
        double written = 0;
        try {
            FileReader fileReader = new FileReader(FileIOBench);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            // Pull out the useful data from the file
            while((line = bufferedReader.readLine()) != null) {
                if (line.contains("read, MiB/s:")) {
                    line = line.replaceAll("\\s+", "");
                    line = line.substring(line.lastIndexOf(":") + 1);
                    read = Double.parseDouble(line);
                }
                if (line.contains("written, MiB/s:")) {
                    line = line.replaceAll("\\s+", "");
                    line = line.substring(line.lastIndexOf(":") + 1);
                    written = Double.parseDouble(line);
                }
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            FileIOBench + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + FileIOBench + "'");
        }
        double data[] = {read, written};
        return data;
    }


    private void PostObject(ServerInformation serverInformation) throws IOException {
        String url = "http://localhost:8080/postData";

        ObjectMapper objectMapper = new ObjectMapper();
        String JsonInfoString = objectMapper.writeValueAsString(serverInformation);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(JsonInfoString);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("ERROR RESPONSE code: " + response.getStatusLine().getStatusCode());
        }
        client.close();
    }
}





