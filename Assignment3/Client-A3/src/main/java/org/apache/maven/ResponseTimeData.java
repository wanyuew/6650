package org.apache.maven;

public class ResponseTimeData {
    private long responseTime;
    private String requestType;

    // Constructors, getters, and setters

    // Constructor for creating an instance with response time and request type
    public ResponseTimeData(long responseTime, String requestType) {
        this.responseTime = responseTime;
        this.requestType = requestType;
    }
    // Getter method for responseTime
    public long getResponseTime() {
        return responseTime;
    }

    // Getter method for requestType
    public String getRequestType() {
        return requestType;
    }
}
