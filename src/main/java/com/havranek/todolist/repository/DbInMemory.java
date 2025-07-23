package com.havranek.todolist.repository;

import com.havranek.todolist.exceptions.EntityNotFound;
import com.havranek.todolist.model.entity.Status;
import com.havranek.todolist.model.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DbInMemory implements Repository{

    private final TreeMap<Long, Task> taskDb = new TreeMap<>();
    private final AtomicLong safeIdGenerator = new AtomicLong();

    public DbInMemory() {
        setData();
        setLastId();
    }

    @Override
    public Optional<Task> findById(long id) {
        return Optional.ofNullable(taskDb.get(id));
    }

    @Override
    public List<Task> findAll() {
        return taskDb.values().stream()
                .toList();
    }

    @Override
    public Task save(Task task) {
        if (task.getId() > 0 && taskDb.containsKey(task.getId())){
            taskDb.replace(task.getId(), task);
        } else {
            long dbId = safeIdGenerator.getAndIncrement();
            task.setId(dbId);
            taskDb.put(dbId, task);
        }
        return task;
    }

    @Override
    public int findSolvedTaskPerDay(LocalDate date) {
        return (int) taskDb.values().stream()
                .filter(task -> date.equals(task.getFinished()) &&
                        task.getStatus() == Status.COMPLETED)
                .count();
    }

    @Override
    public TreeMap<LocalDate, Integer> findSolvedThrewDays() {
        TreeMap<LocalDate,Integer> summaryMap = new TreeMap<>();
        for (var task : taskDb.values()){
            if (task.getFinished() != null && task.getStatus().equals(Status.COMPLETED)){
                if (summaryMap.containsKey(task.getFinished())){
                    summaryMap.put(task.getFinished(), summaryMap.get(task.getFinished()) + 1);
                } else {
                    summaryMap.put(task.getFinished(), 1);
                }
            }
        }
        return summaryMap;
    }

    @Override
    public void deleteById(long id) {
        if (taskDb.get(id) == null){
            throw new EntityNotFound();
        }
        taskDb.remove(id);
    }

    @Override
    public void clearDb() {
        taskDb.clear();
        safeIdGenerator.set(1);

    }

    private void setData(){
        Random random = new Random();

        for (long i = 1; i <= 20; i++) {
            Task task = new Task();
            task.setId(i);
            task.setTitle("Task " + i);

            int statusIndex = random.nextInt(Status.values().length);
            Status status = Status.values()[statusIndex];
            task.setStatus(status);

            LocalDate createdDate = LocalDate.now().minusDays(random.nextInt(30));
            task.setCreated(createdDate);

            LocalDate deadlineDate = createdDate.plusDays(random.nextInt(30));
            task.setDeadline(deadlineDate);

            if (status == Status.COMPLETED) {
                LocalDate finishedDate = createdDate.plusDays(random.nextInt((int) (deadlineDate.toEpochDay() - createdDate.toEpochDay() + 1)));
                task.setFinished(finishedDate);
            } else {
                task.setFinished(null);
            }

            if (random.nextBoolean()) {
                task.setDescription("Random description for task " + i);
            } else {
                task.setDescription(null);
            }

            taskDb.put(i, task);
        }
    }

    private void setLastId(){
        long lastDbKey = taskDb.lastKey();
        safeIdGenerator.set(lastDbKey+1);
    }
}
