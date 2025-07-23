package com.havranek.todolist.service;

import com.havranek.todolist.exceptions.EntityNotFound;
import com.havranek.todolist.mapper.TaskMapper;
import com.havranek.todolist.model.dto.SolvedPerDay;
import com.havranek.todolist.model.dto.TaskAllDTO;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Status;
import com.havranek.todolist.model.entity.Task;
import com.havranek.todolist.repository.Repository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TaskServiceImpl implements TaskService {

    private Repository repository;
    private TaskMapper taskMapper;
    private Validator validator;

    @Autowired
    public TaskServiceImpl(Repository repository, TaskMapper taskMapper, Validator validator) {
        this.repository = repository;
        this.taskMapper = taskMapper;
        this.validator = validator;
    }

    @Override
    public Task getOne(long id) {
        return repository.findById(id)
                .orElseThrow(EntityNotFound::new);
    }

    @Override
    public Page<TaskAllDTO> getAll(Pageable pageable) {
        List<TaskAllDTO> dtoList = repository.findAll()
                .stream()
                .map(taskMapper::mapTaskToAllDTO)
                .toList();

        return transformListToPageable(dtoList, pageable);

    }

    @Override
    public Task update(Task task) {
        Task dbTask = getOne(task.getId());
        dbTask.setTitle(task.getTitle());
        dbTask.setStatus(task.getStatus());
        dbTask.setCreated(task.getCreated());
        dbTask.setDeadline(task.getDeadline());
        dbTask.setFinished(task.getFinished());
        dbTask.setDescription(task.getDescription());
        return repository.save(task);
    }

    @Override
    public Task create(TaskCreateDTO dto) {
        Task task = taskMapper.mapCreateDTOToTask(dto);
        return repository.save(task);
    }

    @Override
    public void createByEntity(Task task) {
        repository.save(task);
    }

    @Override
    public Page<TaskAllDTO> importCreateCSV(MultipartFile file) {

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                file.getInputStream(), StandardCharsets.UTF_8))){

            String[] oneRow;
            boolean isHeader = true;

            while ((oneRow = reader.readNext())!= null){
                if (isHeader){
                    isHeader = false;
                    continue;
                }
                TaskCreateDTO newDTO = new TaskCreateDTO(oneRow[0],
                        LocalDate.parse(oneRow[1]), oneRow[2]);

                Set<ConstraintViolation<TaskCreateDTO>> violations = validator.validate(newDTO);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }
                create(newDTO);
            }
            Pageable defaultPageable = PageRequest.of(0, 20);
            return getAll(defaultPageable);
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<TaskAllDTO> importExistCSV(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                file.getInputStream(), StandardCharsets.UTF_8))){

            String[] oneRow;
            boolean isHeader = true;

            while ((oneRow = reader.readNext())!= null){
                if (isHeader){
                    isHeader = false;
                    continue;
                }
                Task task = new Task(
                        Long.parseLong(oneRow[0]),
                        oneRow[1],
                        Status.valueOf(oneRow[2].toUpperCase()),
                        LocalDate.parse(oneRow[3]),
                        LocalDate.parse(oneRow[4]),
                        LocalDate.parse(oneRow[5]),
                        oneRow[6]
                );

                Set<ConstraintViolation<Task>> violations = validator.validate(task);
                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }

                createByEntity(task);
            }
            Pageable defaultPageable = PageRequest.of(0, 20);
            return getAll(defaultPageable);
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<SolvedPerDay> getSolvedSummary(Pageable pageable) {
        Map<LocalDate, Integer> summaryMap = repository.findSolvedThrewDays();
        List<SolvedPerDay> summaryList = summaryMap.entrySet().stream()
                .map(entrySet ->
                        new SolvedPerDay(
                                entrySet.getKey().toString(),
                                entrySet.getValue()
                        ))
                .toList();

        return transformListToPageable(summaryList, pageable);

    }

    @Override
    public SolvedPerDay getSolvedPerDay(String getDate) {
        LocalDate day = LocalDate.parse(getDate);
        int solved = repository.findSolvedTaskPerDay(day);
        return new SolvedPerDay(getDate, solved);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    private<T> Page<T> transformListToPageable(List<T> taskList, Pageable pageable){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<T> pagedList;

        if (startItem >= taskList.size()) {
            pagedList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, taskList.size());
            pagedList = taskList.subList(startItem, toIndex);
        }

        return new PageImpl<>(pagedList, pageable, taskList.size());
    }
}
