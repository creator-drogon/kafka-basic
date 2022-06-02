package library.event.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import library.event.producer.domain.EventType;
import library.event.producer.domain.LibraryEvents;
import library.event.producer.producer.LibraryEventProd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventController {
    @Autowired
    LibraryEventProd producer;

    @PostMapping(path = "/v1/event")
    public ResponseEntity<LibraryEvents> postData(@RequestBody LibraryEvents libraryEvents) throws JsonProcessingException {
        // used to invoke a kafka producer
        log.info("creating new entry ");
        libraryEvents.setEventType(EventType.NEW);
        producer.sendEventsWithProducerRecord(libraryEvents);
        // listenable future call here is asynchronous ie will be returned immediately
        // so irrespective of weather the message was produced or not, created HTTP status will be returned
        log.info("created new entry ");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvents);
    }

    @PutMapping(path = "/v1/event")
    public ResponseEntity<LibraryEvents> putData(@RequestBody LibraryEvents libraryEvents) throws JsonProcessingException {
        if(libraryEvents.getLibraryEventId() == null){

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(libraryEvents);
        }
        libraryEvents.setEventType(EventType.UPDATE);
        producer.sendEventsWithProducerRecord(libraryEvents);
        log.info("updated entry ");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvents);
    }
}
