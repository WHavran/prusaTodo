package com.havranek.todolist.model.dto;

import com.havranek.todolist.model.entity.Status;

import java.time.LocalDate;

public record TaskAllDTO(
        long id,
        String title,
        Status status,
        LocalDate deadline
) {
}
