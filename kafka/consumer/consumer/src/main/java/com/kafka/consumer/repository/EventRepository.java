package com.kafka.consumer.repository;

import com.kafka.consumer.domain.LibraryEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutorService;

@Repository
public interface EventRepository extends JpaRepository<LibraryEvents,Integer> {


}
