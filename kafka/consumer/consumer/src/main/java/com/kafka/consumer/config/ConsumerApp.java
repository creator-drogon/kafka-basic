package com.kafka.consumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.consumer.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerApp {

    @Autowired
    EventService eventService;

    @KafkaListener(topics = {"library-event"})
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        eventService.processEvent(consumerRecord);
        log.info("These are the record from library-event topic:  {}",consumerRecord);



    }
}
