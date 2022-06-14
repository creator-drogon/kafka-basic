package integration.library.event.controller;

import library.event.producer.ProducerApplication;
import library.event.producer.domain.Book;
import library.event.producer.domain.EventType;
import library.event.producer.domain.LibraryEvents;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
//@SpringBootTest annotation will ensure that our test bootstraps the Spring application context.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = ProducerApplication.class)
//@EmbeddedKafka annotation to inject an instance of an EmbeddedKafkaBroker into our tests.
// we don't need a running zooKeeper or kafka server to test our producer and consumers
@EmbeddedKafka(topics = {"library-event"},partitions = 2)
//We don't want to use the servers mentioned inside application.yml rather use from embedded kafka broker list
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers = ${spring.embedded.kafka.brokers}"
        ,"spring.kafka.admin.properties.bootstrap.servers = ${spring.embedded.kafka.brokers}"})
public class LibraryEventControllerIntegrationTest {
    @Autowired
    TestRestTemplate testRestTemplate;


    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
    // gives an instance of embedded kafka broker since we are using @EmbeddedKafka
    // this will map to the random port automatically and create the instance

    Consumer consumer;

    @BeforeEach
    void setUp() {
        Map<String,Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1","true",embeddedKafkaBroker));

        //The ConsumerFactory implementation to produce new Consumer instances for provided Map configs
        // and optional Deserializers on each createConsumer() invocation.
        consumer = new DefaultKafkaConsumerFactory<>(configs,
                new IntegerDeserializer(),new StringDeserializer()).createConsumer();

        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }


    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {
        //given
        Book book = Book.builder().bookId("1234")
                .bookAuthor("karan").bookName("life of pie").build();

        LibraryEvents events= LibraryEvents.builder().book(book).libraryEventId(null)
                .eventType(EventType.NEW).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type",MediaType.APPLICATION_JSON.toString());
        //exchange method will accept HttpEntity as parameter
        HttpEntity<LibraryEvents> request = new HttpEntity<>(events,headers);
        //when
        ResponseEntity<LibraryEvents> exchange = testRestTemplate.exchange("/v1/event", HttpMethod.POST, request, LibraryEvents.class);
        //LibraryEvents.class is the return type

        //then
    //here we are just asserting that status was CREATED however since our call is asynchronous, CREATED will be return
    // on failure as well
        assertEquals(HttpStatus.CREATED,exchange.getStatusCode());
        //Get a single record for the group from the topic/partition. Optionally, seeking to the current last record.
        ConsumerRecord<Integer,String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-event");
        String expectedRecord = "{\"libraryEventId\":null,\"book\":{\"bookId\":\"1234\",\"bookName\":\"life of pie\",\"bookAuthor\":\"karan\"},\"eventType\":\"NEW\"}";
        String actual = singleRecord.value();

        assertEquals(expectedRecord,actual);

    }
}
