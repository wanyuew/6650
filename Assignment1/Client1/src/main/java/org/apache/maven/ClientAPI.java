package org.apache.maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;


public class ClientAPI {

    private static final int MAX_RETRY_ATTEMPTS = 5;
    static String baseUrl = "http://";
    static String getAlbumsEndpoint_GO = "/IGORTON/AlbumStore/1.0.0/albums/1";
    static String postAlbumEndpoint_GO = "/IGORTON/AlbumStore/1.0.0/albums";
    static String getAlbumsEndpoint_Java = "/A1-6650/albums/1";
    static String postAlbumEndpoint_Java ="/A1-6650/albums/";

    static String port_Go = "1234";

    private static final int INIT_THREAD_POOL_SIZE = 10;
    private static final int INIT_REQUEST_COUNT_PER_THREAD_GROUP = 100;
    private static final int REQUEST_COUNT_PER_THREAD = 1000;

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Main <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
            System.exit(1);
        }

        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delay = Integer.parseInt(args[2]);
        String testUrl = baseUrl + args[3];


        System.out.println("testUrl: " + testUrl);

        // Initial phase
        runThreads(INIT_THREAD_POOL_SIZE, testUrl, INIT_REQUEST_COUNT_PER_THREAD_GROUP);

        // Thread groups
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numThreadGroups; i++) {
            runThreads(threadGroupSize, testUrl, REQUEST_COUNT_PER_THREAD);
            try {
                Thread.sleep(delay * 1000); // Convert seconds to milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        // Calculate metrics
        double wallTime = (endTime - startTime) / 1000.0;
        double throughput = (numThreadGroups * threadGroupSize * 2000) / wallTime;

        // Output results
        if(testUrl.substring(testUrl.length() - 4).equals(port_Go)) {
            System.out.println("Threads run on Go Server");
        } else {
            System.out.println("Threads run on Java Server");
        }
        System.out.println("threadGroupSize = " + threadGroupSize + "; " + "numThreadGroups = " + numThreadGroups + "; "+"delay = " + delay);
        System.out.println("Wall Time: " + wallTime + " seconds");
        System.out.println("Throughput: " + throughput + " requests per second");
    }

    private static void runThreads(int numThreads, String url, int numRequests) throws IOException {
        Thread[] threads = new Thread[numThreads];
        String port = url.substring(url.length() - 4);
        String postUrl;
        String getUrl;
        if (port.equals(port_Go)) {
            postUrl = url + postAlbumEndpoint_GO;
            getUrl = url + getAlbumsEndpoint_GO;
        } else {
            postUrl = url + postAlbumEndpoint_Java;
            getUrl = url + getAlbumsEndpoint_Java;
        }

        String imagePath = "/Users/wanyuewang/Desktop/6650/nmtb.png";
        File imageFile = new File(imagePath);
        byte[] imageData = Files.readAllBytes(imageFile.toPath());

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try{
                    for (int j = 0; j <numRequests; j++) {
                        sendPostRequest(postUrl, httpClient, imageData);
                        sendGetRequest(getUrl, httpClient);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendPostRequest(String url, HttpClient httpClient, byte[] imageData) throws IOException {
        int attempts = 0;
        int maxAttempts = MAX_RETRY_ATTEMPTS;

        while(attempts < maxAttempts) {
            try {
                // Create a POST request with multipart content
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "multipart/form-data")
                        .POST(buildMultipartBody(imageData))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int responseCode = response.statusCode();
                System.out.println("the code" + responseCode);
                if (responseCode >= 200 && responseCode < 300) {
                    // Success, break the loop
                    break;
                } else if (responseCode >= 400 && responseCode < 600) {
                    // 5xx server error, retry
                    attempts++;
                    System.out.println("Retrying POST request (Attempt " + attempts + ")");
                    continue;
                }
                // Handle response if needed
                attempts++;
                System.out.println("Retrying POST request (Attempt " + attempts + ")");
            }catch (Exception e) {
                // Handle exceptions related to network issues or connection problems
                e.printStackTrace();
                attempts++;
                System.out.println("Exception during POST request." + e.getMessage());
            }
        }
    }

    private static void sendGetRequest(String url, HttpClient httpClient) throws IOException {
        int attempts = 0;
        int maxAttempts = MAX_RETRY_ATTEMPTS;

        while(attempts < maxAttempts) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                int responseCode = response.statusCode();
                System.out.println("the code" + responseCode);
                if (responseCode >= 200 && responseCode < 300) {
                    // Success, break the loop
                    break;
                } else if (responseCode >= 400 && responseCode < 600) {
                    // 5xx server error, retry
                    attempts++;
                    System.out.println("Retrying GET request (Attempt " + attempts + ")");
                    continue;
                }
                attempts++;
                System.out.println("Retrying (Attempt " + attempts + ")");
            }catch(Exception ex){
                // Handle exceptions related to network issues or connection problems
                attempts++;
                System.out.println("Exception during GET request." + ex.getMessage());
            }
        }
    }

    private static HttpRequest.BodyPublisher buildMultipartBody(byte[] imageData) throws Exception {
        return HttpRequest.BodyPublishers.ofByteArrays(List.of(imageData));
    }
}

