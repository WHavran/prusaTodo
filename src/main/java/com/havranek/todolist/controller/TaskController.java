package com.havranek.todolist.controller;

import com.havranek.todolist.model.dto.SolvedPerDay;
import com.havranek.todolist.model.dto.TaskAllDTO;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Task;
import com.havranek.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/task")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> showOneTask(@PathVariable long id){
        Task dbTask = taskService.getOne(id);
        return ResponseEntity.ok(dbTask);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TaskAllDTO>> showAll(Pageable pageable){
        Page<TaskAllDTO> listTaskDTO = taskService.getAll(pageable);
        return ResponseEntity.ok(listTaskDTO);
    }

    @GetMapping("/solved/{day}")
    public ResponseEntity<SolvedPerDay> showSolved(@PathVariable String day){
        SolvedPerDay solved = taskService.getSolvedPerDay(day);
        return ResponseEntity.ok(solved);
    }

    @GetMapping("/solved/all")
    public ResponseEntity<Page<SolvedPerDay>> showAllSolved(Pageable pageable){
        Page<SolvedPerDay> solvedSummary = taskService.getSolvedSummary(pageable);
        return ResponseEntity.ok(solvedSummary);
    }

    @PostMapping()
    public ResponseEntity<Task> createNewTask(@Valid @RequestBody TaskCreateDTO taskDTO){
        Task dbTask = taskService.create(taskDTO);
        URI newUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dbTask.getId())
                .toUri();
        return ResponseEntity.created(newUri).body(dbTask);
    }

    @PostMapping("/csv/new")
    public ResponseEntity<Page<TaskAllDTO>> importCSVNew(@RequestParam("file") MultipartFile file){
        try {
            Page<TaskAllDTO> listTaskDTO = taskService.importCreateCSV(file);
            return ResponseEntity.ok(listTaskDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/csv/exist")
    public ResponseEntity<Page<TaskAllDTO>> importCSVExist(@RequestParam("file") MultipartFile file){
        try {
            Page<TaskAllDTO> listTaskDTO = taskService.importExistCSV(file);
            return ResponseEntity.ok(listTaskDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping()
    public ResponseEntity<Task> updateExist(@Valid @RequestBody Task task){
        Task dbTask = taskService.update(task);
        return ResponseEntity.ok(dbTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable long id){
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
