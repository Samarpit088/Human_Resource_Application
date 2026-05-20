package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.Entity.Job;
import com.example.Human_Resource_Managment.Repository.JobRepo;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Custom controller for Job operations with server-side search
 */
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    @Autowired
    private JobRepo jobRepo;

    /**
     * GET endpoint to fetch all jobs with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobRepo.findAll(pageable);

        List<Map<String, Object>> jobsList = jobsPage.getContent().stream()
                .map(this::convertJobToMap)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();

        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("jobses", jobsList);
        response.put("_embedded", embedded);

        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", jobsPage.getSize());
        pageInfo.put("totalElements", jobsPage.getTotalElements());
        pageInfo.put("totalPages", jobsPage.getTotalPages());
        pageInfo.put("number", jobsPage.getNumber());
        response.put("page", pageInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint to fetch a single job
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> getJob(@PathVariable String jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        return ResponseEntity.ok(convertJobToMap(job));
    }

    /**
     * GET endpoint to search jobs with pagination
     * Supports searching by job title or job ID
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchJobs(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Job> allJobs = jobRepo.findAll();

        java.util.stream.Stream<Job> stream = allJobs.stream();

        // Search filter - matches job title or ID starting with the search term
        if (query != null && !query.trim().isEmpty()) {
            String searchTerm = query.toLowerCase();
            stream = stream.filter(job ->
                (job.getJobTitle() != null && job.getJobTitle().toLowerCase().startsWith(searchTerm)) ||
                (job.getJobId() != null && job.getJobId().toLowerCase().startsWith(searchTerm))
            );
        }

        List<Job> filteredList = stream.collect(java.util.stream.Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, filteredList.size());
        List<Job> pageContent = filteredList.subList(start, end);

        List<Map<String, Object>> jobsList = pageContent.stream()
                .map(this::convertJobToMap)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new java.util.HashMap<>();

        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("jobses", jobsList);
        response.put("_embedded", embedded);

        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", size);
        pageInfo.put("totalElements", filteredList.size());
        pageInfo.put("totalPages", (int) Math.ceil((double) filteredList.size() / size));
        pageInfo.put("number", page);
        response.put("page", pageInfo);

        return ResponseEntity.ok(response);
    }

    /**
     * POST endpoint to create a new job
     * Uses READ_COMMITTED transaction isolation for better performance during load testing
     */
    @PostMapping
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Map<String, Object>> createJob(@RequestBody Map<String, Object> jobData) {
        // Check if job ID already exists
        if (jobRepo.existsById((String) jobData.get("jobId"))) {
            throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                "Job ID " + jobData.get("jobId") + " already exists");
        }
        
        Job job = new Job();
        job.setJobId((String) jobData.get("jobId"));
        job.setJobTitle((String) jobData.get("jobTitle"));

        // Validate and set minSalary
        if (jobData.get("minSalary") != null) {
            BigDecimal minSalary = new BigDecimal(jobData.get("minSalary").toString());
            if (minSalary.compareTo(BigDecimal.ZERO) <= 0) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Minimum salary must be greater than zero");
            }
            if (minSalary.precision() - minSalary.scale() > 6) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Minimum salary can have maximum 6 digits");
            }
            job.setMinSalary(minSalary);
        }
        
        // Validate and set maxSalary
        if (jobData.get("maxSalary") != null) {
            BigDecimal maxSalary = new BigDecimal(jobData.get("maxSalary").toString());
            if (maxSalary.compareTo(BigDecimal.ZERO) <= 0) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Maximum salary must be greater than zero");
            }
            if (maxSalary.precision() - maxSalary.scale() > 6) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Maximum salary can have maximum 6 digits");
            }
            job.setMaxSalary(maxSalary);
        }
        
        // Validate salary range
        if (job.getMinSalary() != null && job.getMaxSalary() != null) {
            if (job.getMaxSalary().compareTo(job.getMinSalary()) < 0) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Maximum salary must be greater than or equal to minimum salary");
            }
        }

        Job saved;
        try {
            saved = jobRepo.save(job);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                "Job ID " + job.getJobId() + " already exists. Another user may have created this job.");
        }
        
        return ResponseEntity.ok(convertJobToMap(saved));
    }

    /**
     * PATCH endpoint to update an existing job
     */
    @PatchMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable String jobId,
            @RequestBody Map<String, Object> updates) {

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        if (updates.containsKey("jobTitle"))
            job.setJobTitle((String) updates.get("jobTitle"));

        if (updates.containsKey("minSalary"))
            job.setMinSalary(updates.get("minSalary") != null
                    ? new BigDecimal(updates.get("minSalary").toString()) : null);

        if (updates.containsKey("maxSalary"))
            job.setMaxSalary(updates.get("maxSalary") != null
                    ? new BigDecimal(updates.get("maxSalary").toString()) : null);

        Job saved = jobRepo.save(job);
        return ResponseEntity.ok(convertJobToMap(saved));
    }

    /**
     * Convert Job entity to Map
     */
    private Map<String, Object> convertJobToMap(Job job) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("jobId", job.getJobId());
        map.put("jobTitle", job.getJobTitle());
        map.put("minSalary", job.getMinSalary());
        map.put("maxSalary", job.getMaxSalary());
        return map;
    }
}