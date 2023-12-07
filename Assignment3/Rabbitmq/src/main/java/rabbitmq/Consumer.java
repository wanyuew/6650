package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static final String QUEUE_NAME = "review";
    private static ConnectionFactory factory;
    private static Connection con;
    private final static int NUM_OF_CONSUMERS = 15;

    public static void main(String[] args) {
        try {
            receiveMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void receiveMessages() throws URISyntaxException,
            NoSuchAlgorithmException,
            KeyManagementException,
            IOException,
            TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        con = factory.newConnection();
        System.out.println("1");

        for (int i = 0; i < NUM_OF_CONSUMERS; i++) {
            Runnable runnable = () -> {
                try {
                    Channel channel = con.createChannel();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    System.out.println("3");

                    final DeliverCallback threadCallback = (consumerTag, delivery)
                            -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                        //parse the json string
                        JSONObject jsonObject = new JSONObject(message);

                        // Obtain albumId and action from the JSON object
                        int albumId = Integer.parseInt(jsonObject.getString("albumId"));
                        String action = jsonObject.getString("action");
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                        //post to DB
                        try {
                            ReviewDao.updateReview(albumId, action);
                            System.out.println("5");

                        } catch (SQLException | InterruptedException e) {
                            System.out.println(e);
                            throw new RuntimeException(e);
                        }
                        System.out.println("Received message - Album ID: " + albumId + ", Action: " + action);
                    };

                    channel.basicConsume(QUEUE_NAME, false, threadCallback, consumerTag -> {
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            System.out.println("2");
        }
    }
}
