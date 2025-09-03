package coding.toast.camelplayground.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfiguration {

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost("localhost");
        cachingConnectionFactory.setPort(5672);
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        return cachingConnectionFactory;
    }

    @Bean("mongoClient")
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://admin:secret@localhost:27017");
    }

}
