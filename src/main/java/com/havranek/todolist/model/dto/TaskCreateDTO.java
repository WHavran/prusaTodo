package com.havranek.todolist.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateDTO(
        @Size(min = 2, max = 30, message = "Required range 2-30 characters")
        String title,
        @NotNull(message = "Required")
        @Future(message = "Date has to be in the future")
        LocalDate deadline,
        @Size(min = 5, max = 200, message = "Required range 5-250 characters")
        String description
) {
}
