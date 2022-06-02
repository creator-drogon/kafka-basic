package library.event.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import library.event.producer.domain.LibraryEvents;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//this class is responsible for producing new events to the kafka topic "library-event"
@Component
@Slf4j
public class LibraryEventProd {
    //key serializer -> Integer , value serializer -> String
    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;


    @Autowired // used to create java object to JSON
    ObjectMapper objectMapper;
    private String topic = "library-event";

    //Library Event is type since this method will be invoked from controller using same parameters
    public void sendEvents(LibraryEvents libraryEvents) throws JsonProcessingException {
        //send default will not require explicit mention of topic  ,uses default topic
        Integer key = libraryEvents.getLibraryEventId();;
        String value = objectMapper.writeValueAsString(libraryEvents);
        // sendDefault will read topic from application.yml kafka:template:default-topic: library-event
        ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.sendDefault(key,value);
       // ListenableFuture--->Extend Future with the capability to accept completion callbacks.
        // If the future has completed when the callback is added, the callback is triggered immediately.
        //SendResult->>>Result for a ListenableFuture after send.

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override //if message published ended up in a failure
            public void onFailure(Throwable ex) {
                handleFailure(key,value,ex);

            }

            @Override //if message publish was a success
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value,result);

            }
        });
    }

    public void sendEventsWithProducerRecord(LibraryEvents libraryEvents) throws JsonProcessingException {
        Integer key = libraryEvents.getLibraryEventId();;
        String value = objectMapper.writeValueAsString(libraryEvents);


        ProducerRecord<Integer,String> producerRecord = getRecords(topic,key,value);
        // send will accept topic name from program unlike sendDefault that uses application.yml
        ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override //if message published ended up in a failure
            public void onFailure(Throwable ex) {
                handleFailure(key,value,ex);

            }
            @Override //if message publish was a success
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value,result);

            }
        });
    }

    private ProducerRecord<Integer,String> getRecords(String topic, Integer key, String value) {
        //If no partition is specified but a key is present a partition will be chosen
        // using a hash of the key
        //A key/value pair to be sent to Kafka. This consists of a topic name to which the record is being sent,
        // an optional partition number, and an optional key and value.
        List<Header> headers = Arrays.asList(new RecordHeader("event-header","scanner".getBytes(StandardCharsets.UTF_8)));
        return new ProducerRecord<>(topic,null,key,value,headers);
    }

    SendResult<Integer, String> sendEventSynchronized(LibraryEvents libraryEvents) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvents.getLibraryEventId();;
        String value = objectMapper.writeValueAsString(libraryEvents);
        SendResult<Integer,String> sendResult = null;
        try {
          sendResult  = kafkaTemplate.sendDefault(key,value).get(5, TimeUnit.SECONDS);
          // the get method will wait for 5 seconds until result is not obtained
        } catch (InterruptedException  | ExecutionException e) {
            log.error("InterruptedException  | ExecutionException: {} ",e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Generic Exception: {} ",e.getMessage());
            throw e;
        }

        return sendResult;

    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Message delivery was failed  with key : {} and the payload of: {}",key,value);

        try {
            throw ex;
        } catch (Throwable e) {
            log.info("Failed Delivery caused by: " + e.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message was successfully published with key : {} and the payload of: {}" +
                " to the partition {}" ,key,value,result.getRecordMetadata().partition());
    }



    public ListenableFuture<SendResult<Integer,String>> sendEventsWithProducerRecord1(LibraryEvents libraryEvents) throws JsonProcessingException {
        Integer key = libraryEvents.getLibraryEventId();;
        String value = objectMapper.writeValueAsString(libraryEvents);


        ProducerRecord<Integer,String> producerRecord = getRecords(topic,key,value);
        // send will accept topic name from program unlike sendDefault that uses application.yml
        ListenableFuture<SendResult<Integer,String>> listenableFuture = kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override //if message published ended up in a failure
            public void onFailure(Throwable ex) {
                handleFailure(key,value,ex);

            }
            @Override //if message publish was a success
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value,result);

            }
        });

        return listenableFuture;
    }

}
