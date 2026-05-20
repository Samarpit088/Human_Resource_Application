package com.example.Human_Resource_Managment.Service;

import com.example.Human_Resource_Managment.DTO.JobHistoryDTO;
import com.example.Human_Resource_Managment.Entity.*;
import com.example.Human_Resource_Managment.ExceptionHandling.JobHistoryException;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import com.example.Human_Resource_Managment.ExceptionHandling.ValidationException;
import com.example.Human_Resource_Managment.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobHistoryService {

    private final EmployeeRepo employeeRepo;
    private final JobHistoryRepo jobHistoryRepo;
    private final JobRepo jobRepo;
    private final DepartmentRepo departmentRepo;
    
    // Use a far future date to represent "currently working" since end_date is NOT NULL in schema
    private static final LocalDate FAR_FUTURE_DATE = LocalDate.of(9999, 12, 31);

    /**
     * Update employee job information with automatic job history tracking
     * This method handles promotions, department changes, job title changes, and employee departures
     * All operations are transactional - if any part fails, everything rolls back
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "jobHistory", key = "#employeeId", beforeInvocation = false),
        @CacheEvict(value = "employees", allEntries = true, beforeInvocation = false)
    })
    public Map<String, Object> updateEmployeeJob(
            Long employeeId,
            String jobId,
            Long departmentId,
            BigDecimal salary,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            log.info("=== CACHE EVICTION: Starting job update for employee ID: {} ===", employeeId);
            log.info("Cache eviction will occur AFTER transaction commits successfully");

            // 1. Fetch and validate employee
            Employees employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with ID: " + employeeId));

            // 2. Validate dates
            validateDates(employee, startDate, endDate);

            // 3. Determine what changed
            boolean salaryChanged = salary != null && !salary.equals(employee.getSalary());
            boolean jobChanged = jobId != null && !jobId.equals(employee.getJob().getJobId());
            boolean departmentChanged = departmentId != null && 
                    (employee.getDepartment() == null || 
                     !departmentId.equals(employee.getDepartment().getDepartmentId()));

            // 4. Handle employee departure (end date provided)
            if (endDate != null) {
                return handleEmployeeDeparture(employee, endDate);
            }

            // 5. Handle job changes (promotion, department change, job title change)
            if (salaryChanged || jobChanged || departmentChanged) {
                return handleJobChange(employee, jobId, departmentId, salary, startDate,
                        salaryChanged, jobChanged, departmentChanged);
            }

            throw new ValidationException("No changes detected. Please modify at least one field.");

        } catch (ResourceNotFoundException | ValidationException e) {
            log.error("Validation error during job update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during job update for employee {}: {}", employeeId, e.getMessage(), e);
            throw new JobHistoryException("Failed to update employee job information", e);
        }
    }

    /**
     * Handle employee departure by setting end date on current job history record
     */
    private Map<String, Object> handleEmployeeDeparture(Employees employee, LocalDate endDate) {
        log.info("Processing employee departure for employee ID: {}", employee.getEmployeeId());

        // Find current job history record (end_date = FAR_FUTURE_DATE)
        JobHistory currentRecord = jobHistoryRepo.findByIdEmployeeIdAndEndDate(employee.getEmployeeId(), FAR_FUTURE_DATE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active job history record found for employee ID: " + employee.getEmployeeId()));

        // Validate end date is after start date
        if (!endDate.isAfter(currentRecord.getId().getStartDate())) {
            throw new ValidationException(
                    "End date must be after start date: " + currentRecord.getId().getStartDate());
        }

        // Update end date
        currentRecord.setEndDate(endDate);
        jobHistoryRepo.save(currentRecord);

        log.info("Employee departure recorded successfully for employee ID: {}", employee.getEmployeeId());

        return buildResponse(employee, currentRecord, "Employee departure recorded successfully");
    }

    /**
     * Handle job changes (promotion, department change, job title change)
     * Creates new job history record and closes previous one
     */
    private Map<String, Object> handleJobChange(
            Employees employee,
            String jobId,
            Long departmentId,
            BigDecimal salary,
            LocalDate startDate,
            boolean salaryChanged,
            boolean jobChanged,
            boolean departmentChanged
    ) {
        log.info("Processing job change for employee ID: {}", employee.getEmployeeId());

        // Validate salary is positive
        if (salary != null && salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Salary must be positive");
        }
        
        // Note: Salary can increase, decrease, or stay the same
        // We don't enforce salary increase for all job changes

        // Check if this is the first job history record - if so, store initial salary
        List<JobHistory> existingRecords = jobHistoryRepo.findByIdEmployeeId(employee.getEmployeeId());
        
        // Determine the correct start date for the new record
        LocalDate newRecordStartDate = startDate;
        
        if (existingRecords.isEmpty()) {
            // This is the FIRST job history record for this employee
            // Create a record starting from hire_date with CURRENT job/dept
            LocalDate hireDate = employee.getHireDate();
            
            log.info("First job history record for employee {}. Creating initial record from hire_date: {}", 
                    employee.getEmployeeId(), hireDate);
            
            // Create the first record with hire_date as start_date
            JobHistory firstRecord = new JobHistory();
            JobHistoryId firstRecordId = new JobHistoryId(employee.getEmployeeId(), hireDate);
            firstRecord.setId(firstRecordId);
            firstRecord.setEmployee(employee);
            firstRecord.setJob(employee.getJob());
            firstRecord.setDepartment(employee.getDepartment());
            firstRecord.setEndDate(startDate); // Close at the new start_date
            jobHistoryRepo.save(firstRecord);
            
            log.info("Created initial job history record: employee={}, start={}, end={}", 
                    employee.getEmployeeId(), hireDate, startDate);
        } else {
            // Close previous job history record
            closePreviousJobHistoryRecord(employee.getEmployeeId(), startDate);
        }

        // Check for overlapping records (pass existing records to avoid redundant DB call)
        checkForOverlappingRecords(employee.getEmployeeId(), startDate, existingRecords);

        // Fetch new job if changed
        Job newJob = null;
        if (jobChanged) {
            newJob = jobRepo.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));
        }

        // Fetch new department if changed
        Department newDepartment = null;
        if (departmentChanged) {
            newDepartment = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with ID: " + departmentId));
        }

        // Update employee record
        if (salary != null) {
            employee.setSalary(salary);
        }
        if (newJob != null) {
            employee.setJob(newJob);
        }
        if (newDepartment != null) {
            employee.setDepartment(newDepartment);
        }
        employeeRepo.save(employee);

        // Create new job history record
        JobHistory newJobHistory = createNewJobHistoryRecord(employee, startDate);
        jobHistoryRepo.save(newJobHistory);

        // Build change description
        String changeDescription = buildChangeDescription(salaryChanged, jobChanged, departmentChanged);
        log.info("Job change completed successfully for employee ID: {}. Changes: {}", 
                employee.getEmployeeId(), changeDescription);
        log.info("=== CACHE EVICTION: Transaction will commit now, cache eviction will trigger ===");

        return buildResponse(employee, newJobHistory, changeDescription + " recorded successfully");
    }

    /**
     * Close previous job history record by setting end date
     */
    private void closePreviousJobHistoryRecord(Long employeeId, LocalDate newStartDate) {
        List<JobHistory> currentRecords = jobHistoryRepo.findAllByIdEmployeeIdAndEndDate(employeeId, FAR_FUTURE_DATE);

        if (currentRecords.isEmpty()) {
            log.info("No previous job history record to close for employee ID: {}", employeeId);
            return;
        }

        if (currentRecords.size() > 1) {
            throw new JobHistoryException(
                    "Data integrity issue: Multiple active job history records found for employee ID: " + employeeId);
        }

        JobHistory previousRecord = currentRecords.get(0);
        LocalDate oldStartDate = previousRecord.getId().getStartDate();
        previousRecord.setEndDate(newStartDate);
        jobHistoryRepo.save(previousRecord);

        log.info("Previous job history record closed for employee ID: {}. Record: (start={}, end={})", 
                employeeId, oldStartDate, newStartDate);
    }

    /**
     * Create new job history record
     */
    private JobHistory createNewJobHistoryRecord(Employees employee, LocalDate startDate) {
        JobHistoryId jobHistoryId = new JobHistoryId(employee.getEmployeeId(), startDate);

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(jobHistoryId);
        jobHistory.setEmployee(employee);
        jobHistory.setJob(employee.getJob());
        jobHistory.setDepartment(employee.getDepartment());
        jobHistory.setEndDate(FAR_FUTURE_DATE); // Use far future date for currently working

        log.info("Created new job history record for employee ID: {}. Record: (start={}, end={}, job={}, dept={})", 
                employee.getEmployeeId(), startDate, FAR_FUTURE_DATE, 
                employee.getJob() != null ? employee.getJob().getJobId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null);

        return jobHistory;
    }

    /**
     * Validate dates
     */
    private void validateDates(Employees employee, LocalDate startDate, LocalDate endDate) {
        // Start date cannot be null
        if (startDate == null && endDate == null) {
            throw new ValidationException("Start date is required");
        }

        // Start date validations
        if (startDate != null) {
            // Start date cannot be before hire date
            if (startDate.isBefore(employee.getHireDate())) {
                throw new ValidationException(
                        "Start date cannot be before hire date: " + employee.getHireDate());
            }
        }

        // End date validations
        if (endDate != null && startDate != null) {
            if (!endDate.isAfter(startDate)) {
                throw new ValidationException("End date must be after start date");
            }
        }
    }

    /**
     * Check for overlapping job history records
     * Uses the already-fetched records to avoid redundant DB calls
     */
    private void checkForOverlappingRecords(Long employeeId, LocalDate startDate, List<JobHistory> existingRecords) {
        // Check if a record with this exact start date already exists
        boolean exactDateExists = existingRecords.stream()
                .anyMatch(record -> record.getId().getStartDate().equals(startDate));
        
        if (exactDateExists) {
            throw new ValidationException(
                    "A job history record with start date " + startDate + " already exists for employee ID: " + employeeId + 
                    ". Please use a different start date for the new job change.");
        }
        
        // Check for overlaps with closed records only
        for (JobHistory record : existingRecords) {
            LocalDate recordStart = record.getId().getStartDate();
            LocalDate recordEnd = record.getEndDate();
            
            // Skip the current active record (which we'll close) - identified by FAR_FUTURE_DATE
            if (recordEnd.equals(FAR_FUTURE_DATE)) {
                continue;
            }
            
            // Check if the new start date falls within an existing closed record
            if (startDate.isAfter(recordStart) && startDate.isBefore(recordEnd)) {
                throw new ValidationException(
                        "Cannot create job history record: start date " + startDate + 
                        " overlaps with existing record from " + recordStart + " to " + recordEnd);
            }
        }
    }

    /**
     * Build change description
     */
    private String buildChangeDescription(boolean salaryChanged, boolean jobChanged, boolean departmentChanged) {
        StringBuilder description = new StringBuilder();
        
        if (salaryChanged) {
            description.append("Salary Hike");
        }
        if (jobChanged) {
            if (!description.isEmpty()) description.append(" and ");
            description.append("Job title change");
        }
        if (departmentChanged) {
            if (!description.isEmpty()) description.append(" and ");
            description.append("Department change");
        }
        
        return description.toString();
    }

    /**
     * Build response map
     */
    private Map<String, Object> buildResponse(Employees employee, JobHistory jobHistory, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employee.getEmployeeId());
        response.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
        response.put("jobId", employee.getJob().getJobId());
        response.put("jobTitle", employee.getJob().getJobTitle());
        response.put("departmentId", employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null);
        response.put("departmentName", employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null);
        response.put("salary", employee.getSalary());
        response.put("startDate", jobHistory.getId().getStartDate());
        response.put("endDate", jobHistory.getEndDate());
        response.put("message", message);
        response.put("success", true);
        
        return response;
    }

    /**
     * Get current job history for an employee (NO CACHING - direct from DB)
     */
    @Transactional(readOnly = true)
    public JobHistoryDTO getCurrentJobHistoryDTO(Long employeeId) {
        log.info("Fetching current job history for employee ID: {} from DATABASE (NO CACHE)", employeeId);
        JobHistory jobHistory = jobHistoryRepo.findByIdEmployeeIdAndEndDate(employeeId, FAR_FUTURE_DATE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active job history found for employee ID: " + employeeId));
        
        // Force initialization of all lazy-loaded associations
        Hibernate.initialize(jobHistory.getJob());
        Hibernate.initialize(jobHistory.getDepartment());
        Hibernate.initialize(jobHistory.getEmployee());
        
        // Convert to DTO
        JobHistoryDTO dto = convertToDTO(jobHistory);
        
        // Validate DTO has all required data
        if (dto.getJobId() == null || dto.getJobTitle() == null) {
            log.error("DTO missing job data for employee {}: jobId={}, jobTitle={}", 
                employeeId, dto.getJobId(), dto.getJobTitle());
            throw new IllegalStateException("Failed to load complete job history data");
        }
        
        log.info("Fetched current job history for employee ID: {} (jobId={}, deptId={})", 
            employeeId, dto.getJobId(), dto.getDepartmentId());
        return dto;
    }
    
    /**
     * Get current job history for an employee (returns entity - no caching)
     */
    @Transactional(readOnly = true)
    public JobHistory getCurrentJobHistory(Long employeeId) {
        log.info("Fetching current job history entity for employee ID: {}", employeeId);
        return jobHistoryRepo.findByIdEmployeeIdAndEndDate(employeeId, FAR_FUTURE_DATE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active job history found for employee ID: " + employeeId));
    }

    /**
     * Get all job history for an employee (returns DTOs with caching)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jobHistory", key = "#employeeId", unless = "#result == null || #result.isEmpty()")
    public List<JobHistoryDTO> getEmployeeJobHistoryDTO(Long employeeId) {
        log.info("=== CACHE MISS: Fetching all job history for employee ID: {} from DATABASE ===", employeeId);
        List<JobHistory> history = jobHistoryRepo.findByIdEmployeeIdOrderByIdStartDateDesc(employeeId);
        log.info("Found {} job history records for employee ID: {}", history.size(), employeeId);
        
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No job history found for employee ID: " + employeeId);
        }
        
        // Force initialization of all lazy-loaded associations for all records
        for (JobHistory jh : history) {
            Hibernate.initialize(jh.getJob());
            Hibernate.initialize(jh.getDepartment());
            Hibernate.initialize(jh.getEmployee());
        }
        
        // Convert all to DTOs - this forces all lazy loading to complete within transaction
        List<JobHistoryDTO> dtos = history.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Validate all DTOs have required data before caching
        long invalidDtos = dtos.stream()
                .filter(dto -> dto.getJobId() == null || dto.getJobTitle() == null)
                .count();
        
        if (invalidDtos > 0) {
            log.error("Found {} DTOs with missing job data for employee {}", invalidDtos, employeeId);
            throw new IllegalStateException("Failed to load complete job history data");
        }
        
        log.info("=== CACHE WRITE: Storing {} job history records for employee ID: {} ===", dtos.size(), employeeId);
        return dtos;
    }

    /**
     * Get all job history for an employee (returns entities - no caching)
     */
    @Transactional(readOnly = true)
    public List<JobHistory> getEmployeeJobHistory(Long employeeId) {
        log.info("Fetching all job history entities for employee ID: {}", employeeId);
        List<JobHistory> history = jobHistoryRepo.findByIdEmployeeIdOrderByIdStartDateDesc(employeeId);
        log.info("Found {} job history records for employee ID: {}", history.size(), employeeId);
        
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No job history found for employee ID: " + employeeId);
        }
        return history;
    }
    
    /**
     * Convert JobHistory entity to DTO
     * Explicitly initializes all lazy-loaded fields to avoid proxy issues
     */
    private JobHistoryDTO convertToDTO(JobHistory jh) {
        JobHistoryDTO dto = new JobHistoryDTO();
        
        try {
            dto.setEmployeeId(jh.getId().getEmployeeId());
            dto.setStartDate(jh.getId().getStartDate());
            dto.setEndDate(jh.getEndDate());
            
            // Safely access job - force initialization
            if (jh.getJob() != null) {
                try {
                    // Access properties to force proxy initialization
                    String jobId = jh.getJob().getJobId();
                    String jobTitle = jh.getJob().getJobTitle();
                    
                    if (jobId == null || jobTitle == null) {
                        log.error("Job data is null: jobId={}, jobTitle={}", jobId, jobTitle);
                        throw new IllegalStateException("Job data is incomplete");
                    }
                    
                    dto.setJobId(jobId);
                    dto.setJobTitle(jobTitle);
                } catch (Exception e) {
                    log.error("Failed to load job details for job history: {}", e.getMessage(), e);
                    throw new IllegalStateException("Failed to load job details", e);
                }
            } else {
                log.error("Job is null for job history record");
                throw new IllegalStateException("Job is null");
            }
            
            // Safely access department - force initialization
            if (jh.getDepartment() != null) {
                try {
                    // Access properties to force proxy initialization
                    Long deptId = jh.getDepartment().getDepartmentId();
                    String deptName = jh.getDepartment().getDepartmentName();
                    dto.setDepartmentId(deptId);
                    dto.setDepartmentName(deptName);
                } catch (Exception e) {
                    log.warn("Failed to load department details for job history: {}", e.getMessage());
                    dto.setDepartmentId(null);
                    dto.setDepartmentName(null);
                }
            }
            
            // Safely access employee salary - force initialization
            if (jh.getEmployee() != null) {
                try {
                    // Access property to force proxy initialization
                    BigDecimal salary = jh.getEmployee().getSalary();
                    dto.setCurrentSalary(salary);
                } catch (Exception e) {
                    log.warn("Failed to load employee salary for job history: {}", e.getMessage());
                    dto.setCurrentSalary(null);
                }
            }
            
            dto.setCurrentlyWorking(jh.getEndDate().equals(FAR_FUTURE_DATE));
            
        } catch (Exception e) {
            log.error("Critical error converting JobHistory to DTO: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to convert job history to DTO", e);
        }
        
        return dto;
    }
    
    /**
     * Check if an employee is currently working (has active job history)
     */
    @Transactional(readOnly = true)
    public boolean isCurrentlyWorking(Long employeeId) {
        return jobHistoryRepo.findByIdEmployeeIdAndEndDate(employeeId, FAR_FUTURE_DATE).isPresent();
    }
    
    /**
     * Create initial job history record for a new employee
     * This is called when an employee is first created
     */
    @Transactional
    @CacheEvict(value = "jobHistory", key = "#employeeId", beforeInvocation = false)
    public void createInitialJobHistory(Long employeeId, String jobId, Long departmentId, BigDecimal salary, LocalDate hireDate) {
        log.info("Creating initial job history for new employee ID: {}", employeeId);
        
        // Verify employee exists
        Employees employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        
        // Verify no existing job history
        List<JobHistory> existing = jobHistoryRepo.findByIdEmployeeId(employeeId);
        if (!existing.isEmpty()) {
            log.warn("Employee {} already has job history records. Skipping initial creation.", employeeId);
            return;
        }
        
        // Fetch job
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));
        
        // Fetch department
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
        
        // Create initial job history record
        JobHistoryId jobHistoryId = new JobHistoryId(employeeId, hireDate);
        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(jobHistoryId);
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);
        jobHistory.setEndDate(FAR_FUTURE_DATE); // Currently working
        
        jobHistoryRepo.save(jobHistory);
        
        log.info("Initial job history created successfully for employee ID: {} (start={}, job={}, dept={})", 
                employeeId, hireDate, jobId, departmentId);
    }
}
