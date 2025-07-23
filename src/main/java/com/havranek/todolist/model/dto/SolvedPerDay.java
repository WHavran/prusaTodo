package com.havranek.todolist.model.dto;

public record SolvedPerDay(
        String day,
        int countOfSolved
) {
}
