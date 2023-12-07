package org.apache.maven;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static java.awt.BorderLayout.LINE_END;


public class Threads {

    private static final int MAX_RETRY_ATTEMPTS = 5;
    static String postEndpoint ="/A1_6650/";
    static int successfulPost = 0;
    static int failedPost = 0;
    private static final String BOUNDARY = UUID.randomUUID().toString();


    public Threads() {}

    public static void runThreads(int numThreads, String url,
                                   int numRequests,
                                  List<ResponseTimeData>responseTimes, byte[] imageData) {

        Thread[] threads = new Thread[numThreads];
        String postUrl = url + postEndpoint + "albums/";
        String reviewUrl = url + postEndpoint + "review/";


        //set up Http Client
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        //run threads per group
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try{
                    for (int j = 0; j < numRequests; j++) {
                        int albumId = sendPostRequest(postUrl, httpClient, imageData, responseTimes);
                        if(albumId != 0) {
                            for(int n = 0; n < 2; n++) {
                                sendReviewRequest(reviewUrl, httpClient, "like", albumId, responseTimes);
                            }
                            sendReviewRequest(reviewUrl, httpClient, "dislike", albumId, responseTimes);
                        }
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

    private static void sendReviewRequest (String url,
                                           HttpClient httpClient,
                                           String action,
                                           int albumId,
                                           List<ResponseTimeData>responseTimes) {
        int attempts = 0;
        String url_review = url + "/" + albumId + "/" + action;
        String boundary = "--" + BOUNDARY;

        // Create a JSON object and convert it to a string
        String jsonString = "{\"albumId\":\"" + albumId + "\",\"action\":\"" + action + "\"}";

        while (attempts <= MAX_RETRY_ATTEMPTS) {
            long startTime = System.currentTimeMillis();
            try {
                // Create a POST request with multipart content
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url_review))
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int responseCode = response.statusCode();
                System.out.println(response.body());

                if (responseCode >= 200 && responseCode < 300) {
                    successfulPost++;
                    // Success, break the loop
                    break;
                } else {
                    // 5xx server error, retry
                    attempts++;
                    continue;
                }

            }catch (Exception e) {
                failedPost++;
                // Handle exceptions related to network issues or connection problems
                attempts++;
            } finally {
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;

                // Add response time data to the list
                responseTimes.add(new ResponseTimeData(responseTime, "PostAction"));
            }
        }

    }
    private static int sendPostRequest(String url, HttpClient httpClient,
                                        byte[] imageData,
                                        List<ResponseTimeData>responseTimes) throws IOException {
        int attempts = 0;
        int maxAttempts = MAX_RETRY_ATTEMPTS;
        String artist = "The Bollocks";
        String year = "1971";
        String title = "Sex Pistols";
        String boundary = "--" + BOUNDARY;
        int albumId = 0;

        while(attempts <= maxAttempts) {
            long startTime = System.currentTimeMillis();
            try {

                // Create a POST request with multipart content
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(buildMultipartBody(artist, year, title, imageData))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int responseCode = response.statusCode();
                System.out.println(response.body());

                // Parse the JSON response to get AlbumId
                JSONObject jsonResponse = new JSONObject(response.body());
                albumId = jsonResponse.getInt("albumId");

                if (responseCode >= 200 && responseCode < 300) {
                    successfulPost++;
                    // Success, break the loop
                    break;
                } else {

                    // 5xx server error, retry
                    attempts++;
                    continue;
                }

            }catch (Exception e) {
                failedPost++;
                // Handle exceptions related to network issues or connection problems
                attempts++;
            } finally {
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;

                // Add response time data to the list
                responseTimes.add(new ResponseTimeData(responseTime, "PostTAlbum"));
            }
        }
        return albumId;
    }

    private static HttpRequest.BodyPublisher buildMultipartBody(String artist, String year, String title, byte[] imageData) {
        String boundary = "--" + BOUNDARY;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Add artist parameter
            outputStream.write((boundary + LINE_END).getBytes());
            outputStream.write(("Content-Disposition: form-data; name=\"artist\"" + LINE_END + LINE_END + artist + LINE_END).getBytes());

            // Add year parameter
            outputStream.write((boundary + LINE_END).getBytes());
            outputStream.write(("Content-Disposition: form-data; name=\"year\"" + LINE_END + LINE_END + year + LINE_END).getBytes());

            // Add title parameter
            outputStream.write((boundary + LINE_END).getBytes());
            outputStream.write(("Content-Disposition: form-data; name=\"title\"" + LINE_END + LINE_END + title + LINE_END).getBytes());

            // Add image parameter
            outputStream.write((boundary + LINE_END).getBytes());
            outputStream.write(("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"" + LINE_END).getBytes());
            outputStream.write(("Content-Type: application/octet-stream" + LINE_END + LINE_END).getBytes());
            outputStream.write(imageData);
            outputStream.write(LINE_END.getBytes());

            // Add final boundary
            outputStream.write((boundary + "--" + LINE_END).getBytes());

            return HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return HttpRequest.BodyPublishers.noBody();
        }
    }

}
