import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

  private static final String QUEUE_NAME = "pc";
  
  public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
    

    if (args.length != 2) {
        System.out.println(args);
        String msg = "Usage:\n" + 
                     "\t" + Consumer.class.getName() + " <port> <server>\n" +
                     "\n" +
                     "\tport = the port to listen to\n" +
                     "\tserver = The RabbitMQ server\n";

        System.out.print(msg);
        System.exit(1);
    }

    int port = Integer.parseInt(args[0]);
    String server = args[1];

    // Sleep a few seconds to allow RabbitMQ server to come up
    Thread.sleep(5000);

    // --- Create a connection to RabbitMQ server
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(server);
    factory.setPort(port);
    factory.setUsername("guest");
    factory.setPassword("guest");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    Logger log = LoggerFactory.getLogger(Consumer.class);
    
    // --- Declare a queue to sent to 
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    log.info("[*] Waiting for messages. To exit press CTRL+C");
    
    com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
      
      @Override
      public void handleDelivery(String consumerTag, 
              Envelope envelope, 
              AMQP.BasicProperties properties, 
              byte[] body) throws IOException {
                
        String msg = new String(body, "UTF-8");
        log.info("Received message: '" + msg + "'");
      }
    };
    
    channel.basicConsume(QUEUE_NAME, true, consumer);
    
  }
  
}