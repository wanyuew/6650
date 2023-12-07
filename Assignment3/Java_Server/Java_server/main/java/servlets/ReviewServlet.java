package main.java.servlets;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;

@WebServlet(name="ReviewServlet", value = "/review/*")
@MultipartConfig(fileSizeThreshold = 1024*1024*10,
        maxFileSize = 1024*1024*50,
        maxRequestSize = 1024*1024*100)
public class ReviewServlet extends HttpServlet {
    private ConnectionFactory factory;
    private Connection con;

    @Override
    public void init() {
        factory = new ConnectionFactory();

        try {
            factory.setUri("amqp://guest:guest@localhost:5672");
//            factory.setUri("amqp://guest:guest@ec2-54-218-61-193.us-west-2.compute.amazonaws.com:15672");
            factory.setUsername("guest");
            factory.setPassword("guest");
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            System.err.println(e.getMessage());
        }

        try {
            con = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            // Close RabbitMQ Connection to prevent memory leaks
            if (con != null && con.isOpen()) {
                con.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log or handle the exception appropriately
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String urlPath = request.getPathInfo();
        String[] parameters = urlPath.split("/");
        if (urlPath.isEmpty() || parameters.length != 3) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers!");
            return;
        }

        String albumId = parameters[1];
        String action = parameters[2];
        System.out.print("the action is" + action);

        // Create a JSONObject and put albumId and action in it
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("albumId", albumId);
        jsonObject.put("action", action);

        // Convert the JSONObject to a JSON string
        String jsonString = jsonObject.toString();

        // Publish the JSON string to the queue
        try{
            String QUEUE_NAME = "review";
            Channel channel = con.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, jsonString.getBytes());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}
