package org.apache.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static String baseUrl = "http://";

    private static final int INIT_THREAD_POOL_SIZE = 10;
    private static final int INIT_REQUEST_COUNT_PER_THREAD_GROUP = 100;
    private static final int REQUEST_COUNT_PER_THREAD = 10;

    private static byte[] imageData;


    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Main <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
            System.exit(1);
        }

        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delay = Integer.parseInt(args[2]);
        String testUrl = baseUrl + args[3];
        List<ResponseTimeData> responseTimes = new ArrayList<>();

        String imagePath = "/Users/wanyuewang/Desktop/6650/nmtb.png";
        File imageFile = new File(imagePath);
        imageData = Files.readAllBytes(imageFile.toPath());

        // Initial phase
        Threads.runThreads(INIT_THREAD_POOL_SIZE,
                testUrl, INIT_REQUEST_COUNT_PER_THREAD_GROUP,
                responseTimes, imageData);

        // Thread groups
        long initialStartTime = System.currentTimeMillis();
        for (int i = 0; i < numThreadGroups; i++) {
            Threads.runThreads(threadGroupSize, testUrl, REQUEST_COUNT_PER_THREAD, responseTimes, imageData);
//            try {
//                Thread.sleep(delay * 1000); // Convert seconds to milliseconds
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }


        //end time
        long endTime = System.currentTimeMillis();

        // Calculate metrics
        double wallTime = (endTime - initialStartTime) / 1000.0;
        double throughput = (numThreadGroups * threadGroupSize * 2 * REQUEST_COUNT_PER_THREAD) / wallTime;

        //calculate successful and failed requests
        int totalSuccess = Threads.successfulPost;
        int totalFailures =  INIT_THREAD_POOL_SIZE*INIT_REQUEST_COUNT_PER_THREAD_GROUP + numThreadGroups * threadGroupSize * 2000 - totalSuccess;

        StatsCalculation.calculateStats(responseTimes);
        System.out.println("The number of successful requests are: " + totalSuccess);
        System.out.println("The number of failed requests are: " + totalFailures);
        System.out.println("threadGroupSize = " + threadGroupSize + "; " + "numThreadGroups = " + numThreadGroups + "; "+"delay = " + delay);
        System.out.println("Wall Time: " + wallTime + " seconds");
        System.out.println("Throughput: " + throughput + " requests per second");
    }

}
