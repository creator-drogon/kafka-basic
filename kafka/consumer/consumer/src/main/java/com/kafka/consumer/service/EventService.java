package com.kafka.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.domain.LibraryEvents;
import com.kafka.consumer.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    public void processEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        LibraryEvents recordValue;
        recordValue = objectMapper.readValue(consumerRecord.value(),LibraryEvents.class);
        switch (recordValue.getEventType()){

            case NEW:
                persistData(recordValue);
                break;
            case UPDATE:
                validateData(recordValue);
                persistData(recordValue);
                break;
            case DELETE:
                eventRepository.delete(recordValue);
                log.info("deleting this event: {}",recordValue);
            default:
                System.out.println("Wrong entry ");

        }





    }

    private void validateData(LibraryEvents recordValue) {
        if(recordValue.getLibraryEventId() == null){
            throw new IllegalArgumentException("Not a valid Event ID to be updated ");
        }
        Optional<LibraryEvents> eventsOptional = eventRepository.findById(recordValue.getLibraryEventId());

        if(!eventsOptional.isPresent()){
            throw new RuntimeException();
        }
        else{
            log.info("Proceeding with new update for valid event");
        }
    }

    private void persistData(LibraryEvents recordValue) {

        recordValue.getBook().setLibraryEvents(recordValue);
                eventRepository.save(recordValue);
                log.info("Saving a new Event with Value: {}",recordValue);
    }
}
