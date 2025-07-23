package com.havranek.todolist.model.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public class Task {

    @NotNull(message = "Required")
    private  long id;
    @Size(min = 2, max = 30, message = "Required range 2-30 characters")
    private String title;
    @NotNull(message = "Required")
    private Status status;
    @NotNull(message = "Required")
    private LocalDate created;
    @NotNull(message = "Required")
    private LocalDate deadline;
    private LocalDate finished;
    @Size(min = 5, max = 250, message = "Required range 5-250 characters")
    private String description;

    public Task() {
        this.created = LocalDate.now();
    }


    public Task(long id, String title, Status status, LocalDate created, LocalDate deadline, LocalDate finished, String description) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.created = created;
        this.deadline = deadline;
        this.finished = finished;
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getFinished() {
        return finished;
    }

    public void setFinished(LocalDate finished) {
        this.finished = finished;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
