package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import com.example.Human_Resource_Managment.ExceptionHandling.JobHistoryException;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import com.example.Human_Resource_Managment.ExceptionHandling.ValidationException;
import com.example.Human_Resource_Managment.Service.JobHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/job-history")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class JobHistoryController {

    private final JobHistoryService jobHistoryService;

    /**
     * Update employee job information
     * Handles promotions, department changes, job title changes, and employee departures
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateEmployeeJob(
            @Valid @RequestBody UpdateJobRequest request
    ) {
        try {
            log.info("Received job update request for employee ID: {}", request.getEmployeeId());

            Map<String, Object> response = jobHistoryService.updateEmployeeJob(
                    request.getEmployeeId(),
                    request.getJobId(),
                    request.getDepartmentId(),
                    request.getSalary(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage(), false));
        } catch (ValidationException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage(), false));
        } catch (JobHistoryException e) {
            log.error("Job history error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update job information: " + e.getMessage(), false));
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An unexpected error occurred", false));
        }
    }

    /**
     * Get current job history for an employee
     */
    @GetMapping("/current/{employeeId}")
    public ResponseEntity<?> getCurrentJobHistory(
            @PathVariable @NotNull @Positive Long employeeId
    ) {
        try {
            JobHistory jobHistory = jobHistoryService.getCurrentJobHistory(employeeId);
            
            // Build a simple response map to avoid lazy loading issues
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", jobHistory.getId().getEmployeeId());
            response.put("startDate", jobHistory.getId().getStartDate());
            response.put("endDate", jobHistory.getEndDate());
            response.put("jobId", jobHistory.getJob() != null ? jobHistory.getJob().getJobId() : null);
            response.put("jobTitle", jobHistory.getJob() != null ? jobHistory.getJob().getJobTitle() : null);
            response.put("departmentId", jobHistory.getDepartment() != null ? jobHistory.getDepartment().getDepartmentId() : null);
            response.put("departmentName", jobHistory.getDepartment() != null ? jobHistory.getDepartment().getDepartmentName() : null);
            response.put("currentSalary", jobHistory.getEmployee() != null ? jobHistory.getEmployee().getSalary() : null);
            response.put("currentlyWorking", jobHistory.getEndDate().equals(LocalDate.of(9999, 12, 31)));
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Error fetching current job history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch current job history", false));
        }
    }

    /**
     * Get all job history for an employee (without salary history)
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeJobHistory(
            @PathVariable @NotNull @Positive Long employeeId
    ) {
        try {
            List<JobHistory> history = jobHistoryService.getEmployeeJobHistory(employeeId);
            
            // Build response list to avoid lazy loading issues
            List<Map<String, Object>> responseList = history.stream().map(jh -> {
                Map<String, Object> map = new HashMap<>();
                map.put("employeeId", jh.getId().getEmployeeId());
                map.put("startDate", jh.getId().getStartDate());
                map.put("endDate", jh.getEndDate());
                map.put("jobId", jh.getJob() != null ? jh.getJob().getJobId() : null);
                map.put("jobTitle", jh.getJob() != null ? jh.getJob().getJobTitle() : null);
                map.put("departmentId", jh.getDepartment() != null ? jh.getDepartment().getDepartmentId() : null);
                map.put("departmentName", jh.getDepartment() != null ? jh.getDepartment().getDepartmentName() : null);
                map.put("currentlyWorking", jh.getEndDate().equals(LocalDate.of(9999, 12, 31)));
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(responseList);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage(), false));
        } catch (Exception e) {
            log.error("Error fetching job history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch job history", false));
        }
    }
    
    /**
     * Create error response
     */
    private Map<String, Object> createErrorResponse(String message, boolean success) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", success);
        response.put("timestamp", LocalDate.now());
        return response;
    }

    /**
     * Debug endpoint - Get raw job history count
     */
    @GetMapping("/debug/count/{employeeId}")
    public ResponseEntity<?> getJobHistoryCount(@PathVariable Long employeeId) {
        try {
            List<JobHistory> allRecords = jobHistoryService.getEmployeeJobHistory(employeeId);
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", employeeId);
            response.put("totalRecords", allRecords.size());
            response.put("records", allRecords.stream().map(jh -> {
                Map<String, Object> record = new HashMap<>();
                record.put("startDate", jh.getId().getStartDate());
                record.put("endDate", jh.getEndDate());
                record.put("jobId", jh.getJob() != null ? jh.getJob().getJobId() : null);
                record.put("departmentId", jh.getDepartment() != null ? jh.getDepartment().getDepartmentId() : null);
                return record;
            }).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage(), false));
        }
    }

    /**
     * Inner class for request body
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateJobRequest {

        @NotNull(message = "Employee ID is required")
        @Positive(message = "Employee ID must be positive")
        private Long employeeId;

        @Size(max = 10, message = "Job ID cannot exceed 10 characters")
        private String jobId;

        @Positive(message = "Department ID must be positive")
        private Long departmentId;

        @Positive(message = "Salary must be positive")
        @Digits(integer = 6, fraction = 2, message = "Salary can have max 6 integer digits and 2 decimal places")
        private BigDecimal salary;

        @NotNull(message = "Start date is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }
}
