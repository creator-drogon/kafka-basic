package com.kafka.consumer.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data //getter setter and toString methods
@Builder // @Builder is a useful mechanism for using the Builder pattern without writing boilerplate code.
// We can apply this annotation to a Class or a method.
@Entity
public class LibraryEvents {
    @Id
    @GeneratedValue
    Integer libraryEventId;

    @OneToOne(mappedBy = "libraryEvents",cascade = CascadeType.ALL)
    @ToString.Exclude// one library event can have only one book associated to it
    private Book book;

    @Enumerated(value = EnumType.STRING)
    @Column
    private EventType eventType;
}
