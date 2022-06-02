package library.event.producer.config;

import library.event.producer.domain.LibraryEvents;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("local") // making topics via code is not recommended for prod
public class AutoConfigCreate {

    //this bean is responsible for creating a topic programmatically
    @Bean
    public NewTopic createTopic(){

        return TopicBuilder.name("library-event").partitions(2).replicas(2).build();
    }
    //the producer factory below is used to supply properties for configuring kafka template
    //programmatically, same can be done via application.yml file as well

 //   @Bean
    public ProducerFactory<String,LibraryEvents> producerFactory(){

        Map<String,Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);


        return new DefaultKafkaProducerFactory<>(configs);
    }

 //   @Bean
    public KafkaTemplate<String,LibraryEvents> getKafkaTemplate(){

        return new KafkaTemplate<>(producerFactory());
    }
}
