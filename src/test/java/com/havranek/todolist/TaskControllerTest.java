package com.havranek.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.havranek.todolist.model.dto.TaskCreateDTO;
import com.havranek.todolist.model.entity.Status;
import com.havranek.todolist.model.entity.Task;
import com.havranek.todolist.repository.Repository;
import com.havranek.todolist.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class TaskControllerTest {

    @Autowired
    private Repository repository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setupTestData() {
        repository.clearDb();
        Task task1 = new Task(
                1,
                "Write documentation",
                Status.CREATED,
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 8, 1),
                null,
                "Write detailed project documentation covering all modules."
        );
        Task task2 = new Task(
                2,
                "Implement feature X",
                Status.IN_PROCESS,
                LocalDate.of(2025, 7, 15),
                LocalDate.of(2025, 8, 15),
                null,
                "Develop and test the new feature X according to specifications."
        );
        Task task3 = new Task(
                3,
                "Code review",
                Status.COMPLETED,
                LocalDate.of(2025, 6, 30),
                LocalDate.of(2025, 7, 5),
                LocalDate.of(2025, 7, 4),
                "Review the codebase and provide feedback to developers."
        );
        repository.save(task1);
        repository.save(task2);
        repository.save(task3);
    }

    @Test
    public void getOneRequestSuccess() throws Exception {

        assertTrue(repository.findById(1).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Write documentation")))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.created", is("2025-07-10")))
                .andExpect(jsonPath("$.deadline", is("2025-08-01")))
                .andExpect(jsonPath("$.finished", nullValue()))
                .andExpect(jsonPath("$.description", is("Write detailed project documentation covering all modules.")));
    }

    @Test
    public void getOneRequestFailed() throws Exception {

        assertFalse(repository.findById(100).isPresent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/{id}", 100))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Entity not found")));
    }

    @Test
    public void getAllRequestSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].title", is("Write documentation")))
                .andExpect(jsonPath("$.content[1].status", is("IN_PROCESS")))
                .andExpect(jsonPath("$.content[2].deadline", is("2025-07-05")))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.number", is(0)));

    }

    @Test
    public void getAllRequestFailed() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/al"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void getSolvedPerDaySuccess() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/solved/{day}", "2025-07-04"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.day", is("2025-07-04")))
                .andExpect(jsonPath("$.countOfSolved", is(1)));

        Task task4 = new Task(
                4,
                "Unit test",
                Status.COMPLETED,
                LocalDate.of(2025, 6, 5),
                LocalDate.of(2025, 7, 6),
                LocalDate.of(2025, 7, 4),
                "Review the codebase and provide feedback to developers."
        );
        repository.save(task4);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/solved/{day}", "2025-07-04"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.day", is("2025-07-04")))
                .andExpect(jsonPath("$.countOfSolved", is(2)));
    }


    @Test
    public void getSolvedPerDayFailed() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/task/solved/{day}", "2025-07-48"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Text '2025-07-48' could not be parsed: Invalid value for DayOfMonth (valid values 1 - 28/31): 48")));

    }

    @Test
    public void postCreateNewRequestSuccess() throws Exception {

        TaskCreateDTO newTaskDTO = new TaskCreateDTO("Testing", LocalDate.of(2025, 11, 11), "This is testing case");

        mockMvc.perform(post("/api/task").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.title", is("Testing")))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.created", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.deadline", is("2025-11-11")))
                .andExpect(jsonPath("$.finished", nullValue()))
                .andExpect(jsonPath("$.description", is("This is testing case")));

        assertTrue(repository.findById(4).isPresent());
    }

    @Test
    public void postCreateNewRequestFailed() throws Exception {

        TaskCreateDTO newTaskDTO1 = new TaskCreateDTO("T", LocalDate.of(2025, 11, 11), "");

        mockMvc.perform(post("/api/task").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDTO1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listOfErrors[0].field", is("description")))
                .andExpect(jsonPath("$.listOfErrors[0].message", is("Required range 5-250 characters")))
                .andExpect(jsonPath("$.listOfErrors[1].field", is("title")))
                .andExpect(jsonPath("$.listOfErrors[1].message", is("Required range 2-30 characters")));

        String jsonPayload = """
                {
                  "title": "Titles",
                  "deadline": "11-11-2025",
                  "description": "Something something"
                }
                """;

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid input: Text '11-11-2025' could not be parsed at index 0")));
    }

    @Test
    public void putUpdateRequestSuccess() throws Exception{

        String jsonPayload1 = """
                {
                  "id": 1,
                  "title": "Write documentation",
                  "status": "COMPLETED",
                  "created": "2025-07-10",
                  "deadline": "2025-07-22",
                  "finished": "2025-07-22",
                  "description": "Write detailed project documentation covering all modules."
                }
                """;

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Write documentation")))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.created", is("2025-07-10")))
                .andExpect(jsonPath("$.deadline", is("2025-07-22")))
                .andExpect(jsonPath("$.finished", is("2025-07-22")))
                .andExpect(jsonPath("$.description", is("Write detailed project documentation covering all modules.")));
    }

    @Test
    public void putUpdateRequestFailed() throws Exception{

        String jsonPayload = """
                {
                  "id": 100,
                  "title": "Write documentation",
                  "status": "COMPLETED",
                  "created": "2025-07-10",
                  "deadline": "2025-07-22",
                  "finished": "2025-07-22",
                  "description": "Write detailed project documentation covering all modules."
                }
                """;

        assertFalse(repository.findById(100).isPresent());

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Entity not found")));

        String jsonPayload2 = """
                {
                  "id": 1,
                  "title": "Write documentation",
                  "status": "DONE",
                  "created": "2025-07-10",
                  "deadline": "2025-07-22",
                  "finished": "2025-07-22",
                  "description": ""
                }
                """;

        assertTrue(repository.findById(1).isPresent());
        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload2))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid status. Allowed values: CREATED, IN_PROCESS, COMPLETED, FAILED.")));

    }
    @Test
    public void putDeleteRequestSuccess() throws Exception{
        assertTrue(repository.findById(1).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/task/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    public void putDeleteRequestFailed() throws Exception{
        assertFalse(repository.findById(100).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/task/{id}", 100))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Entity not found")));

    }


}
