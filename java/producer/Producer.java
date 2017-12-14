import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
  
  private static final String QUEUE_NAME = "pc";
  
  public static void main(String[] args) throws IOException, 
      TimeoutException, InterruptedException {

    if (args.length != 3) {
        System.out.println(args);
        String msg = "Usage:\n" + 
                        "\t" + Producer.class.getName() + " <port> <server> <message>\n" +
                        "\n" +
                        "\tport = the port to listen to\n" +
                        "\tserver = The RabbitMQ server\n" +
                        "\tmessage = The message to send\n";

        System.out.print(msg);
        System.exit(1);
    }

    int port = Integer.parseInt(args[0]);
    String server = args[1];
    String msg = args[2];

    // Sleep a few seconds to allow RabbitMQ server to come up
    Thread.sleep(5000);

    Logger log = LoggerFactory.getLogger(Producer.class);
    
    // --- Create a connection to RabbitMQ server
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(server);
    factory.setPort(port);
    factory.setUsername("guest");
    factory.setPassword("guest");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    
    // --- Declare a queue to sent to 
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    
    // --- Publish Message
    for (int i = 0; i <= 30; i++) {
        try {
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            log.info("Sent message: '" + msg + "'");
        }
        catch (Exception e) {
            log.warn("Message NOT delivered");
        }
        Thread.sleep(2000);
    }

    
    Thread.sleep(2000);
    
    System.exit(0);
  }
  
}