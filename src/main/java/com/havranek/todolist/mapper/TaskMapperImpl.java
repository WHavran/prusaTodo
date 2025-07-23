package com.havranek.todolist.mapper;

import com.havranek.todolist.model.dto.TaskAllDTO;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Status;
import com.havranek.todolist.model.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskMapperImpl implements TaskMapper{

    @Override
    public TaskAllDTO mapTaskToAllDTO(Task task) {
        return new TaskAllDTO(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getDeadline()
        );
    }

    @Override
    public Task mapCreateDTOToTask(TaskCreateDTO dto) {
        Task task = new Task();
        task.setId(-1);
        task.setTitle(dto.title());
        task.setStatus(Status.CREATED);
        task.setCreated(LocalDate.now());
        task.setDeadline(dto.deadline());
        task.setFinished(null);
        task.setDescription(dto.description());
        return task;
    }
}
