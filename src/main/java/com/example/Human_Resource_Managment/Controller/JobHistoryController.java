package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.DTO.JobHistoryDTO;
import com.example.Human_Resource_Managment.Entity.JobHistory;
import com.example.Human_Resource_Managment.Service.JobHistoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/job-history")
@RequiredArgsConstructor
@Slf4j
public class JobHistoryController {

    private final JobHistoryService jobHistoryService;
    private final CacheManager cacheManager;

    /**
     * Update employee job information
     * PUT /api/v1/job-history/update
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateEmployeeJob(@Valid @RequestBody UpdateJobRequest request) {
        log.info("Received job update request for employee ID: {}", request.getEmployeeId());
        
        try {
            Map<String, Object> result = jobHistoryService.updateEmployeeJob(
                request.getEmployeeId(),
                request.getJobId(),
                request.getDepartmentId(),
                request.getSalary(),
                request.getStartDate(),
                request.getEndDate()
            );
            
            log.info("Job update successful for employee ID: {}", request.getEmployeeId());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error updating job for employee ID: {}", request.getEmployeeId(), e);
            throw e;
        }
    }

    /**
     * Get current job history for an employee (NO CACHE - for debugging)
     * GET /api/v1/job-history/current/{employeeId}/nocache
     */
    @GetMapping("/current/{employeeId}/nocache")
    public ResponseEntity<JobHistoryDTO> getCurrentJobHistoryNoCache(@PathVariable Long employeeId) {
        log.info("Fetching current job history for employee ID: {} (NO CACHE)", employeeId);
        JobHistory jobHistory = jobHistoryService.getCurrentJobHistory(employeeId);
        
        // Force initialize
        org.hibernate.Hibernate.initialize(jobHistory.getJob());
        org.hibernate.Hibernate.initialize(jobHistory.getDepartment());
        org.hibernate.Hibernate.initialize(jobHistory.getEmployee());
        
        // Manual DTO creation with detailed logging
        JobHistoryDTO dto = new JobHistoryDTO();
        dto.setEmployeeId(jobHistory.getId().getEmployeeId());
        dto.setStartDate(jobHistory.getId().getStartDate());
        dto.setEndDate(jobHistory.getEndDate());
        
        log.info("Job object: {}", jobHistory.getJob());
        if (jobHistory.getJob() != null) {
            log.info("Job ID: {}, Job Title: {}", jobHistory.getJob().getJobId(), jobHistory.getJob().getJobTitle());
            dto.setJobId(jobHistory.getJob().getJobId());
            dto.setJobTitle(jobHistory.getJob().getJobTitle());
        } else {
            log.error("JOB IS NULL!");
        }
        
        log.info("Department object: {}", jobHistory.getDepartment());
        if (jobHistory.getDepartment() != null) {
            log.info("Dept ID: {}, Dept Name: {}", jobHistory.getDepartment().getDepartmentId(), jobHistory.getDepartment().getDepartmentName());
            dto.setDepartmentId(jobHistory.getDepartment().getDepartmentId());
            dto.setDepartmentName(jobHistory.getDepartment().getDepartmentName());
        }
        
        if (jobHistory.getEmployee() != null) {
            dto.setCurrentSalary(jobHistory.getEmployee().getSalary());
        }
        
        dto.setCurrentlyWorking(jobHistory.getEndDate().equals(java.time.LocalDate.of(9999, 12, 31)));
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Get current job history for an employee
     * GET /api/v1/job-history/current/{employeeId}
     */
    @GetMapping("/current/{employeeId}")
    public ResponseEntity<JobHistoryDTO> getCurrentJobHistory(
            @PathVariable Long employeeId,
            @RequestParam(required = false, defaultValue = "false") boolean bypassCache) {
        log.info("Fetching current job history for employee ID: {} (bypassCache={})", employeeId, bypassCache);
        
        if (bypassCache) {
            // Clear cache first, then fetch
            if (cacheManager.getCache("currentJobHistory") != null) {
                cacheManager.getCache("currentJobHistory").evict(employeeId);
            }
        }
        
        JobHistoryDTO jobHistory = jobHistoryService.getCurrentJobHistoryDTO(employeeId);
        return ResponseEntity.ok(jobHistory);
    }

    /**
     * Get all job history for an employee
     * GET /api/v1/job-history/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<JobHistoryDTO>> getEmployeeJobHistory(
            @PathVariable Long employeeId,
            @RequestParam(required = false, defaultValue = "false") boolean bypassCache) {
        log.info("Fetching all job history for employee ID: {} (bypassCache={})", employeeId, bypassCache);
        
        if (bypassCache) {
            // Clear cache first, then fetch
            if (cacheManager.getCache("jobHistory") != null) {
                cacheManager.getCache("jobHistory").evict(employeeId);
            }
        }
        
        List<JobHistoryDTO> jobHistory = jobHistoryService.getEmployeeJobHistoryDTO(employeeId);
        return ResponseEntity.ok(jobHistory);
    }

    /**
     * Check if employee is currently working
     * GET /api/v1/job-history/is-working/{employeeId}
     */
    @GetMapping("/is-working/{employeeId}")
    public ResponseEntity<Map<String, Boolean>> isCurrentlyWorking(@PathVariable Long employeeId) {
        log.info("Checking if employee ID: {} is currently working", employeeId);
        boolean isWorking = jobHistoryService.isCurrentlyWorking(employeeId);
        return ResponseEntity.ok(Map.of("isWorking", isWorking));
    }

    /**
     * Clear job history cache for a specific employee
     * DELETE /api/v1/job-history/cache/{employeeId}
     */
    @DeleteMapping("/cache/{employeeId}")
    public ResponseEntity<Map<String, String>> clearEmployeeCache(@PathVariable Long employeeId) {
        log.info("Clearing job history cache for employee ID: {}", employeeId);
        
        try {
            // Clear current job history cache
            if (cacheManager.getCache("currentJobHistory") != null) {
                cacheManager.getCache("currentJobHistory").evict(employeeId);
            }
            
            // Clear all job history cache
            if (cacheManager.getCache("jobHistory") != null) {
                cacheManager.getCache("jobHistory").evict(employeeId);
            }
            
            log.info("Cache cleared successfully for employee ID: {}", employeeId);
            return ResponseEntity.ok(Map.of(
                "message", "Cache cleared successfully for employee ID: " + employeeId,
                "success", "true"
            ));
        } catch (Exception e) {
            log.error("Error clearing cache for employee ID: {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to clear cache: " + e.getMessage(),
                    "success", "false"
                ));
        }
    }

    /**
     * Clear all job history caches
     * DELETE /api/v1/job-history/cache/all
     */
    @DeleteMapping("/cache/all")
    public ResponseEntity<Map<String, String>> clearAllCache() {
        log.info("Clearing all job history caches");
        
        try {
            // Clear all current job history cache
            if (cacheManager.getCache("currentJobHistory") != null) {
                cacheManager.getCache("currentJobHistory").clear();
            }
            
            // Clear all job history cache
            if (cacheManager.getCache("jobHistory") != null) {
                cacheManager.getCache("jobHistory").clear();
            }
            
            log.info("All job history caches cleared successfully");
            return ResponseEntity.ok(Map.of(
                "message", "All job history caches cleared successfully",
                "success", "true"
            ));
        } catch (Exception e) {
            log.error("Error clearing all caches", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to clear caches: " + e.getMessage(),
                    "success", "false"
                ));
        }
    }

    /**
     * Request DTO for updating employee job
     */
    @Data
    public static class UpdateJobRequest {
        @NotNull(message = "Employee ID is required")
        private Long employeeId;
        
        private String jobId;
        
        private Long departmentId;
        
        private BigDecimal salary;
        
        @NotNull(message = "Start date is required")
        private LocalDate startDate;
        
        private LocalDate endDate;
    }
}
