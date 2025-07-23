package com.havranek.todolist.mapper;

import com.havranek.todolist.model.dto.TaskAllDTO;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Task;

public interface TaskMapper {

    TaskAllDTO mapTaskToAllDTO(Task task);

    Task mapCreateDTOToTask(TaskCreateDTO dto);

}
