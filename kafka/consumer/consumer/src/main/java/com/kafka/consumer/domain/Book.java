package com.kafka.consumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Book {
    @NotNull
    @Id
    private String bookId;
    @NotBlank
    @Column
    private String bookName;
    @NotBlank
    @Column
    private String bookAuthor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "libraryEvent")
    private LibraryEvents libraryEvents;
}
