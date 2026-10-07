package com.internal.tasktracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Validate paging input
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be >= 1 and pageSize must be between 1 and 100");
        }

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter, return 400 for unknown values
        String normalizedStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.toUpperCase()).name();
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
            }
        }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        int total = allResults.size();
        int start = (int) Math.min((long) (page - 1) * pageSize, total);
        int end = Math.min(start + pageSize, total);
        List<Task> pageResults = allResults.subList(start, end);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
