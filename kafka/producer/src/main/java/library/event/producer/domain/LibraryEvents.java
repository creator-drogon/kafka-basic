package library.event.producer.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data //getter setter and toString methods
@Builder // @Builder is a useful mechanism for using the Builder pattern without writing boilerplate code.
// We can apply this annotation to a Class or a method.
public class LibraryEvents {

    Integer libraryEventId;
    @NotNull
    private Book book;
    private EventType eventType;
}
