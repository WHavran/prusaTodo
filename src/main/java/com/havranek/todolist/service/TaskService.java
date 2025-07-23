package com.havranek.todolist.service;

import com.havranek.todolist.model.dto.SolvedPerDay;
import com.havranek.todolist.model.dto.TaskAllDTO;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {

    Task getOne(long id);

    Page<TaskAllDTO> getAll(Pageable pageable);

    Task update(Task task);

    Task create(TaskCreateDTO dto);

    void createByEntity(Task task);

    Page<TaskAllDTO> importCreateCSV(MultipartFile file);

    Page<TaskAllDTO> importExistCSV(MultipartFile file);

    Page<SolvedPerDay> getSolvedSummary(Pageable pageable);

    SolvedPerDay getSolvedPerDay(String getDate);

    void deleteById(long id);
}
