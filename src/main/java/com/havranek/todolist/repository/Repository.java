package com.havranek.todolist.repository;

import com.havranek.todolist.model.entity.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public interface Repository {

    Optional<Task> findById(long id);

    List<Task> findAll();

    Task save(Task task);

    int findSolvedTaskPerDay(LocalDate date);

    TreeMap<LocalDate, Integer> findSolvedThrewDays();

    void deleteById(long id);

    void clearDb();
}
