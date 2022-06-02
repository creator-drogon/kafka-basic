package library.event.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Book {
    @NotNull
    private String bookId;
    @NotBlank
    private String bookName;
    @NotBlank
    private String bookAuthor;
}
