package com.kafka.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class ConsumerAppManualAck implements AcknowledgingMessageListener<Integer,String> {

    @KafkaListener(topics = {"library-event"})
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord){

        log.info("These are the record from library-event topic:  {}",consumerRecord);



    }

    @Override
    @KafkaListener(topics = {"library-event"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {

        log.info("Offsets are committed only when acknowledged via method for {}",consumerRecord);
        assert acknowledgment != null;
        acknowledgment.acknowledge();

    }
}
