package library.event.producer.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import library.event.producer.controller.LibraryEventController;
import library.event.producer.domain.Book;
import library.event.producer.domain.EventType;
import library.event.producer.domain.LibraryEvents;
import library.event.producer.producer.LibraryEventProd;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//Annotation that can be applied to a test class to enable and configure  MockMvc.
/*Annotation that can be used for a Spring MVC test that focuses only on Spring MVC components.
 Using this annotation will disable full autoconfiguration and instead apply only configuration
 relevant to MVC tests (i.e. @Controller, @ControllerAdvice, @JsonComponent, Converter/GenericConverter,
 Filter, WebMvcConfigurer and HandlerMethodArgumentResolver beans but not @Component, @Service or @Repository beans).*/
@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class EventControllerUnitTest {

    @Autowired
    MockMvc mockMvc; //Main entry point for server-side Spring MVC test support, provides dispatcher servlet and
    //is ware of all thew endpoints in controller


    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean//since we are just testing controller we can mock bean of producer to test controller endpoints
    LibraryEventProd producer;
    //Annotation that can be used to add mocks to a Spring ApplicationContext. Can be used as a class level annotation or on fields in either
    // @Configuration classes, or test classes that are @RunWith the SpringRunner.


    @Test
    void postLibraryEvent() throws Exception {
        //given
        Book book = Book.builder().bookId("1234")
                .bookAuthor("karan").bookName("life of pie").build();

        LibraryEvents events= LibraryEvents.builder().book(book).libraryEventId(null)
                .eventType(EventType.NEW).build();
        String json = objectMapper.writeValueAsString(events);
        //when
        //Perform a request and return a type that allows chaining further actions,
        // such as asserting expectations, on the result.
        doNothing().when(producer).sendEventsWithProducerRecord(isA(LibraryEvents.class));

        //then
        mockMvc.perform(post("/v1/event").accept(json).
                contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given

        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvents libraryEvent = LibraryEvents.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
      //  when(producer.sendEvents(isA(LibraryEvents.class))).thenReturn(null);
        //expect
        String expectedErrorMessage = "book.bookAuthor - must not be blank, book.bookId - must not be null";
        mockMvc.perform(post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));

    }
}
