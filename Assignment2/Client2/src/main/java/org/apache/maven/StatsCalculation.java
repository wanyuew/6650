package org.apache.maven;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatsCalculation {

    public static void calculateStats(List<ResponseTimeData> allResponseTimes) {
        // Separate POST and GET requests
        List<ResponseTimeData> postResponseTimes = allResponseTimes.stream()
                .filter(data -> "POST".equals(data.getRequestType()))
                .collect(Collectors.toList());

        List<ResponseTimeData> getResponseTimes = allResponseTimes.stream()
                .filter(data -> "GET".equals(data.getRequestType()))
                .collect(Collectors.toList());

        // Calculate statistics for POST requests
        calculateAndPrintStats(postResponseTimes, "POST");

        // Calculate statistics for GET requests
        calculateAndPrintStats(getResponseTimes, "GET");
    }

    private static void calculateAndPrintStats(List<ResponseTimeData> responseTimes, String requestType) {
        double mean = responseTimes.stream()
                .mapToLong(ResponseTimeData::getResponseTime)
                .average()
                .orElse(0.0);

        Collections.sort(responseTimes, Comparator.comparingLong(ResponseTimeData::getResponseTime));

        long median = responseTimes.get(responseTimes.size() / 2).getResponseTime();

        double p99 = responseTimes.stream()
                .sorted(Comparator.comparingLong(ResponseTimeData::getResponseTime).reversed())
                .skip((long) (responseTimes.size() * 0.01))
                .findFirst()
                .map(ResponseTimeData::getResponseTime)
                .orElse(0L);

        long min = responseTimes.stream().mapToLong(ResponseTimeData::getResponseTime).min().orElse(0L);
        long max = responseTimes.stream().mapToLong(ResponseTimeData::getResponseTime).max().orElse(0L);

        System.out.println("Statistics for " + requestType + " requests:");
        System.out.println("Mean: " + mean + " milliseconds");
        System.out.println("Median: " + median + " milliseconds");
        System.out.println("p99: " + p99 + " milliseconds");
        System.out.println("Min: " + min + " milliseconds");
        System.out.println("Max: " + max + " milliseconds");
        System.out.println();
    }
}

