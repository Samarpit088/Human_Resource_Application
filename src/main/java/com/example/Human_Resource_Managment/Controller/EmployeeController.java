package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.Entity.*;
import com.example.Human_Resource_Managment.Repository.*;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import com.example.Human_Resource_Managment.ExceptionHandling.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Custom controller for Employee write operations (PUT/PATCH)
 * 
 * Note: Spring Data REST projections are read-only (GET only).
 * This controller handles write operations by bypassing the projection
 * and directly accessing the full entity from the database.
 * 
 * Request flow:
 * - GET /api/v1/employees/{id} → Spring Data REST + EmployeeProjection (read-only)
 * - PATCH /api/v1/employees/{id} → This controller (bypasses projection, directly updates entity)
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private DepartmentRepo departmentRepo;
    
    @Autowired
    private com.example.Human_Resource_Managment.Service.JobHistoryService jobHistoryService;

    /**
     * GET endpoint to fetch all employees with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Employees> employeesPage = employeeRepo.findAll(pageable);
        
        // Convert to DTOs to avoid lazy loading issues
        java.util.List<Map<String, Object>> employeesList = employeesPage.getContent().stream()
                .map(this::convertEmployeeToMap)
                .collect(java.util.stream.Collectors.toList());
        
        // Build response similar to Spring Data REST format
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("employeeses", employeesList);
        response.put("_embedded", embedded);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", employeesPage.getSize());
        pageInfo.put("totalElements", employeesPage.getTotalElements());
        pageInfo.put("totalPages", employeesPage.getTotalPages());
        pageInfo.put("number", employeesPage.getNumber());
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Convert Employee entity to Map to avoid lazy loading issues
     */
    private Map<String, Object> convertEmployeeToMap(Employees employee) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("employeeId", employee.getEmployeeId());
        map.put("firstName", employee.getFirstName());
        map.put("lastName", employee.getLastName());
        map.put("email", employee.getEmail());
        map.put("phoneNumber", employee.getPhoneNumber());
        map.put("hireDate", employee.getHireDate());
        map.put("salary", employee.getSalary());
        map.put("commissionPct", employee.getCommissionPct());
        
        // Add job info (handle lazy loading)
        if (employee.getJob() != null) {
            Map<String, Object> jobInfo = new java.util.HashMap<>();
            jobInfo.put("jobId", employee.getJob().getJobId());
            jobInfo.put("jobTitle", employee.getJob().getJobTitle());
            map.put("job", jobInfo);
        } else {
            map.put("job", null);
        }
        
        // Add department info (handle lazy loading)
        if (employee.getDepartment() != null) {
            Map<String, Object> deptInfo = new java.util.HashMap<>();
            deptInfo.put("departmentId", employee.getDepartment().getDepartmentId());
            deptInfo.put("departmentName", employee.getDepartment().getDepartmentName());
            map.put("department", deptInfo);
        } else {
            map.put("department", null);
        }
        
        // Add manager info (handle lazy loading)
        if (employee.getManager() != null) {
            map.put("managerId", employee.getManager().getEmployeeId());
            map.put("managerFirstName", employee.getManager().getFirstName());
            map.put("managerLastName", employee.getManager().getLastName());
        } else {
            map.put("managerId", null);
            map.put("managerFirstName", null);
            map.put("managerLastName", null);
        }
        
        return map;
    }

    /**
     * POST endpoint to create a new employee
     * 
     * Uses READ_COMMITTED transaction isolation for better performance during load testing
     * while still preventing dirty reads
     * 
     * Supports:
     * - employeeId (required)
     * - firstName, lastName (required)
     * - email (required, unique)
     * - phoneNumber, hireDate
     * - salary (required)
     * - jobId (required)
     * - departmentId (optional)
     * 
     * Example request body:
     * {
     *   "employeeId": 823,
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john.doe@company.com",
     *   "phoneNumber": "555.123.4567",
     *   "hireDate": "2024-01-15",
     *   "salary": 50000,
     *   "job": { "jobId": "IT_PROG" },
     *   "department": { "departmentId": 60 }
     * }
     */
    @PostMapping
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> employeeData) {
        // Log incoming request data for debugging
        System.out.println("=== CREATE EMPLOYEE REQUEST ===");
        System.out.println("Request data: " + employeeData);
        System.out.println("Contains phoneNumber key: " + employeeData.containsKey("phoneNumber"));
        if (employeeData.containsKey("phoneNumber")) {
            System.out.println("Phone number value: '" + employeeData.get("phoneNumber") + "'");
            System.out.println("Phone number is null: " + (employeeData.get("phoneNumber") == null));
        }
        System.out.println("===============================");
        
        Employees employee = new Employees();
        
        // Set required fields
        if (!employeeData.containsKey("employeeId")) {
            throw new ValidationException("employeeId is required");
        }
        Long employeeId = Long.valueOf(employeeData.get("employeeId").toString());
        
        System.out.println("Checking if employee ID " + employeeId + " already exists...");
        
        // Check if employee ID already exists
        employeeRepo.findById(employeeId).ifPresent(emp -> {
            System.out.println("DUPLICATE FOUND! Employee ID " + employeeId + " already exists");
            throw new ValidationException("Employee ID " + employeeId + " already exists");
        });
        
        System.out.println("Employee ID " + employeeId + " is available, proceeding with creation...");
        
        employee.setEmployeeId(employeeId);
        
        // First name is REQUIRED
        if (!employeeData.containsKey("firstName") || employeeData.get("firstName") == null) {
            throw new ValidationException("First name is required");
        }
        String firstName = ((String) employeeData.get("firstName")).trim();
        if (firstName.isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }
        if (firstName.length() > 20) {
            throw new ValidationException("First name cannot exceed 20 characters");
        }
        employee.setFirstName(firstName);
        
        // Last name is REQUIRED
        if (!employeeData.containsKey("lastName") || employeeData.get("lastName") == null) {
            throw new ValidationException("Last name is required");
        }
        String lastName = ((String) employeeData.get("lastName")).trim();
        if (lastName.isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }
        if (lastName.length() > 25) {
            throw new ValidationException("Last name cannot exceed 25 characters");
        }
        employee.setLastName(lastName);
        
        if (!employeeData.containsKey("email")) {
            throw new ValidationException("email is required");
        }
        String email = (String) employeeData.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        email = email.trim();
        if (email.length() > 25) {
            throw new ValidationException("Email cannot exceed 25 characters");
        }
        // Check if email already exists
        employeeRepo.findByEmail(email).ifPresent(emp -> {
            throw new ValidationException("Email already exists");
        });
        employee.setEmail(email);
        
        if (!employeeData.containsKey("hireDate")) {
            throw new ValidationException("hireDate is required");
        }
        employee.setHireDate(LocalDate.parse(employeeData.get("hireDate").toString()));
        
        // Phone number validation - REQUIRED field
        if (!employeeData.containsKey("phoneNumber") || employeeData.get("phoneNumber") == null) {
            throw new ValidationException("Phone number is required");
        }
        String phoneNumber = ((String) employeeData.get("phoneNumber")).trim();
        if (phoneNumber.isEmpty()) {
            throw new ValidationException("Phone number cannot be empty");
        }
        // Validate phone number length (exactly 10 digits)
        String phoneDigits = phoneNumber.replaceAll("[^0-9]", "");
        if (phoneDigits.length() != 10) {
            throw new ValidationException("Phone number must be exactly 10 digits");
        }
        employee.setPhoneNumber(phoneNumber);
        
        if (employeeData.containsKey("salary") && employeeData.get("salary") != null) {
            employee.setSalary(new BigDecimal(employeeData.get("salary").toString()));
        }
        
        if (employeeData.containsKey("commissionPct") && employeeData.get("commissionPct") != null) {
            employee.setCommissionPct(new BigDecimal(employeeData.get("commissionPct").toString()));
        }
        
        // Set job if provided
        if (employeeData.containsKey("job") && employeeData.get("job") != null) {
            Map<String, Object> jobMap = (Map<String, Object>) employeeData.get("job");
            if (jobMap.containsKey("jobId")) {
                String jobId = (String) jobMap.get("jobId");
                Job job = jobRepo.findById(jobId)
                        .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
                employee.setJob(job);
            }
        }
        
        // Set department if provided
        if (employeeData.containsKey("department") && employeeData.get("department") != null) {
            Map<String, Object> deptMap = (Map<String, Object>) employeeData.get("department");
            if (deptMap.containsKey("departmentId")) {
                Long departmentId = Long.valueOf(deptMap.get("departmentId").toString());
                Department department = departmentRepo.findById(departmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
                employee.setDepartment(department);
                
                // Set manager from department if department has a manager
                if (department.getManager() != null) {
                    employee.setManager(department.getManager());
                }
            }
        }
        
        // Save the employee with proper error handling for race conditions
        Employees savedEmployee;
        try {
            savedEmployee = employeeRepo.save(employee);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handle duplicate key violations that might occur in race conditions
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Duplicate entry")) {
                if (errorMessage.contains("PRIMARY")) {
                    throw new ValidationException("Employee ID " + employee.getEmployeeId() + " already exists. Another user may have created this employee.");
                } else if (errorMessage.contains("email")) {
                    throw new ValidationException("Email " + employee.getEmail() + " already exists. Another user may have used this email.");
                }
            }
            throw new ValidationException("Failed to create employee: " + e.getMessage());
        }
        
        // Create initial job history record if employee has job, department, and salary
        if (savedEmployee.getJob() != null && savedEmployee.getDepartment() != null && savedEmployee.getSalary() != null) {
            try {
                jobHistoryService.createInitialJobHistory(
                    savedEmployee.getEmployeeId(),
                    savedEmployee.getJob().getJobId(),
                    savedEmployee.getDepartment().getDepartmentId(),
                    savedEmployee.getSalary(),
                    savedEmployee.getHireDate() // Start date is hire date
                );
            } catch (Exception e) {
                // Log error but don't fail employee creation
                System.err.println("Failed to create initial job history for employee " + savedEmployee.getEmployeeId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Convert to Map to avoid lazy loading serialization issues
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("employeeId", savedEmployee.getEmployeeId());
        response.put("firstName", savedEmployee.getFirstName());
        response.put("lastName", savedEmployee.getLastName());
        response.put("email", savedEmployee.getEmail());
        response.put("phoneNumber", savedEmployee.getPhoneNumber());
        response.put("hireDate", savedEmployee.getHireDate());
        response.put("salary", savedEmployee.getSalary());
        
        if (savedEmployee.getJob() != null) {
            response.put("jobId", savedEmployee.getJob().getJobId());
            response.put("jobTitle", savedEmployee.getJob().getJobTitle());
        }
        
        if (savedEmployee.getDepartment() != null) {
            response.put("departmentId", savedEmployee.getDepartment().getDepartmentId());
            response.put("departmentName", savedEmployee.getDepartment().getDepartmentName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint to fetch a single employee with all details
     * This bypasses the projection to avoid lazy loading issues
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployee(@PathVariable Long employeeId) {
        Employees employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("employeeId", employee.getEmployeeId());
        response.put("firstName", employee.getFirstName());
        response.put("lastName", employee.getLastName());
        response.put("email", employee.getEmail());
        response.put("phoneNumber", employee.getPhoneNumber());
        response.put("hireDate", employee.getHireDate());
        response.put("salary", employee.getSalary());
        response.put("commissionPct", employee.getCommissionPct());
        
        // Add job info
        if (employee.getJob() != null) {
            Map<String, Object> jobInfo = new java.util.HashMap<>();
            jobInfo.put("jobId", employee.getJob().getJobId());
            jobInfo.put("jobTitle", employee.getJob().getJobTitle());
            response.put("job", jobInfo);
        } else {
            response.put("job", null);
        }
        
        // Add department info
        if (employee.getDepartment() != null) {
            Map<String, Object> deptInfo = new java.util.HashMap<>();
            deptInfo.put("departmentId", employee.getDepartment().getDepartmentId());
            deptInfo.put("departmentName", employee.getDepartment().getDepartmentName());
            response.put("department", deptInfo);
        } else {
            response.put("department", null);
        }
        
        // Add manager info
        if (employee.getManager() != null) {
            Map<String, Object> managerInfo = new java.util.HashMap<>();
            managerInfo.put("employeeId", employee.getManager().getEmployeeId());
            managerInfo.put("firstName", employee.getManager().getFirstName());
            managerInfo.put("lastName", employee.getManager().getLastName());
            response.put("manager", managerInfo);
        } else {
            response.put("manager", null);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH endpoint to partially update an employee
     * 
     * Supports updating:
     * - firstName, lastName
     * - email, phoneNumber
     * - salary, commissionPct
     * - jobId (change employee's job)
     * - departmentId (change employee's department)
     * - managerId (assign a manager/supervisor)
     * 
     * IMPORTANT: When job, department, or salary changes, this automatically creates a job history record
     * 
     * Example request bodies:
     * 
     * 1. Update basic info:
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "email": "john.doe@company.com",
     *   "phoneNumber": "555.123.4567"
     * }
     * 
     * 2. Update salary:
     * {
     *   "salary": 50000,
     *   "commissionPct": 0.15
     * }
     * 
     * 3. Change job and department:
     * {
     *   "jobId": "IT_PROG",
     *   "departmentId": 60
     * }
     * 
     * 4. Assign manager:
     * {
     *   "managerId": 100
     * }
     */
    @PatchMapping("/{employeeId}")
    public ResponseEntity<Employees> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody Map<String, Object> updates) {
        
        // Fetch employee - this bypasses the read-only projection
        Employees employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Track if job-related fields changed (for job history)
        boolean jobRelatedChange = false;
        String newJobId = null;
        Long newDepartmentId = null;
        BigDecimal newSalary = null;

        // Update first name if provided - REQUIRED, cannot be empty
        if (updates.containsKey("firstName")) {
            if (updates.get("firstName") == null) {
                throw new ValidationException("First name cannot be null");
            }
            String firstName = ((String) updates.get("firstName")).trim();
            if (firstName.isEmpty()) {
                throw new ValidationException("First name cannot be empty");
            }
            if (firstName.length() > 20) {
                throw new ValidationException("First name cannot exceed 20 characters");
            }
            employee.setFirstName(firstName);
        }

        // Update last name if provided - REQUIRED, cannot be empty
        if (updates.containsKey("lastName")) {
            if (updates.get("lastName") == null) {
                throw new ValidationException("Last name cannot be null");
            }
            String lastName = ((String) updates.get("lastName")).trim();
            if (lastName.isEmpty()) {
                throw new ValidationException("Last name cannot be empty");
            }
            if (lastName.length() > 25) {
                throw new ValidationException("Last name cannot exceed 25 characters");
            }
            employee.setLastName(lastName);
        }

        // Update email if provided
        if (updates.containsKey("email") && updates.get("email") != null) {
            String email = (String) updates.get("email");
            if (!email.trim().isEmpty() && email.length() <= 25) {
                // Check if email already exists for another employee
                employeeRepo.findByEmail(email).ifPresent(emp -> {
                    if (!emp.getEmployeeId().equals(employeeId)) {
                        throw new ValidationException("Email already exists for another employee");
                    }
                });
                employee.setEmail(email);
            } else if (email.trim().isEmpty()) {
                throw new ValidationException("Email is required");
            } else if (email.length() > 25) {
                throw new ValidationException("Email cannot exceed 25 characters");
            }
        }

        // Update phone number if provided - REQUIRED, cannot be empty
        if (updates.containsKey("phoneNumber")) {
            if (updates.get("phoneNumber") == null) {
                throw new ValidationException("Phone number cannot be null");
            }
            String phoneNumber = ((String) updates.get("phoneNumber")).trim();
            if (phoneNumber.isEmpty()) {
                throw new ValidationException("Phone number cannot be empty");
            }
            // Validate phone number length (exactly 10 digits)
            String phoneDigits = phoneNumber.replaceAll("[^0-9]", "");
            if (phoneDigits.length() != 10) {
                throw new ValidationException("Phone number must be exactly 10 digits");
            }
            if (phoneNumber.length() > 20) {
                throw new ValidationException("Phone number cannot exceed 20 characters");
            }
            employee.setPhoneNumber(phoneNumber);
        }

        // Update commission percentage if provided
        if (updates.containsKey("commissionPct") && updates.get("commissionPct") != null) {
            try {
                BigDecimal commissionPct = new BigDecimal(updates.get("commissionPct").toString());
                if (commissionPct.signum() < 0 || commissionPct.compareTo(new BigDecimal("0.99")) > 0) {
                    throw new ValidationException("Commission percentage must be between 0.00 and 0.99");
                }
                employee.setCommissionPct(commissionPct);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid commission percentage format");
            }
        }

        // Check for salary change
        if (updates.containsKey("salary") && updates.get("salary") != null) {
            try {
                BigDecimal salary = new BigDecimal(updates.get("salary").toString());
                if (salary.signum() <= 0) {
                    throw new ValidationException("Salary must be positive");
                }
                // Check if salary actually changed
                if (employee.getSalary() == null || employee.getSalary().compareTo(salary) != 0) {
                    newSalary = salary;
                    jobRelatedChange = true;
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid salary format");
            }
        }

        // Check for job change
        if (updates.containsKey("jobId") && updates.get("jobId") != null) {
            String jobId = (String) updates.get("jobId");
            Job job = jobRepo.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
            // Check if job actually changed
            if (employee.getJob() == null || !employee.getJob().getJobId().equals(jobId)) {
                newJobId = jobId;
                jobRelatedChange = true;
            }
        }

        // Check for department change
        if (updates.containsKey("departmentId") && updates.get("departmentId") != null) {
            try {
                Long departmentId = Long.valueOf(updates.get("departmentId").toString());
                Department department = departmentRepo.findById(departmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
                // Check if department actually changed
                if (employee.getDepartment() == null || !employee.getDepartment().getDepartmentId().equals(departmentId)) {
                    newDepartmentId = departmentId;
                    jobRelatedChange = true;
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid departmentId format");
            }
        }

        // Update manager if provided
        if (updates.containsKey("managerId") && updates.get("managerId") != null) {
            try {
                Long managerId = Long.valueOf(updates.get("managerId").toString());
                
                // Prevent employee from being their own manager
                if (managerId.equals(employeeId)) {
                    throw new ValidationException("An employee cannot be their own manager");
                }
                
                Employees manager = employeeRepo.findById(managerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Manager employee not found with id: " + managerId));
                employee.setManager(manager);
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid managerId format");
            }
        }

        // If job-related fields changed, use JobHistoryService to handle the update
        // This will automatically create job history records
        if (jobRelatedChange) {
            // Use current values if new values not provided
            if (newJobId == null && employee.getJob() != null) {
                newJobId = employee.getJob().getJobId();
            }
            if (newDepartmentId == null && employee.getDepartment() != null) {
                newDepartmentId = employee.getDepartment().getDepartmentId();
            }
            if (newSalary == null) {
                newSalary = employee.getSalary();
            }
            
            // Call JobHistoryService to update job info and create history record
            jobHistoryService.updateEmployeeJob(
                employeeId,
                newJobId,
                newDepartmentId,
                newSalary,
                LocalDate.now(), // Use current date as start date
                null // No end date (employee is still working)
            );
            
            // Refresh employee from database to get updated values
            employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        } else {
            // No job-related changes, just save the employee
            employee = employeeRepo.save(employee);
        }

        return ResponseEntity.ok(employee);
    }

    /**
     * GET endpoint to fetch employees by region with full details including country
     * Custom endpoint to avoid projection issues with deeply nested relationships
     */
    @GetMapping("/by-region/{regionId}")
    public ResponseEntity<Map<String, Object>> getEmployeesByRegion(
            @PathVariable Long regionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Employees> employeesPage = 
            employeeRepo.findByDepartmentLocationCountryRegionRegionId(regionId, pageable);
        
        java.util.List<Map<String, Object>> employeeList = new java.util.ArrayList<>();
        
        for (Employees emp : employeesPage.getContent()) {
            Map<String, Object> empData = new java.util.HashMap<>();
            empData.put("employeeId", emp.getEmployeeId());
            empData.put("firstName", emp.getFirstName());
            empData.put("lastName", emp.getLastName());
            empData.put("email", emp.getEmail());
            empData.put("salary", emp.getSalary());
            
            // Job info
            if (emp.getJob() != null) {
                Map<String, Object> jobInfo = new java.util.HashMap<>();
                jobInfo.put("jobId", emp.getJob().getJobId());
                jobInfo.put("jobTitle", emp.getJob().getJobTitle());
                empData.put("job", jobInfo);
            }
            
            // Department and location info
            if (emp.getDepartment() != null) {
                Map<String, Object> deptInfo = new java.util.HashMap<>();
                deptInfo.put("departmentId", emp.getDepartment().getDepartmentId());
                deptInfo.put("departmentName", emp.getDepartment().getDepartmentName());
                
                // Location info
                if (emp.getDepartment().getLocation() != null) {
                    Locations location = emp.getDepartment().getLocation();
                    Map<String, Object> locationInfo = new java.util.HashMap<>();
                    locationInfo.put("locationId", location.getLocationId());
                    locationInfo.put("city", location.getCity());
                    
                    // Country info
                    if (location.getCountry() != null) {
                        Map<String, Object> countryInfo = new java.util.HashMap<>();
                        countryInfo.put("countryId", location.getCountry().getCountryId());
                        countryInfo.put("countryName", location.getCountry().getCountryName());
                        locationInfo.put("country", countryInfo);
                    }
                    
                    deptInfo.put("location", locationInfo);
                }
                
                empData.put("department", deptInfo);
            }
            
            employeeList.add(empData);
        }
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("employees", employeeList);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", employeesPage.getSize());
        pageInfo.put("totalElements", employeesPage.getTotalElements());
        pageInfo.put("totalPages", employeesPage.getTotalPages());
        pageInfo.put("number", employeesPage.getNumber());
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint to fetch all unique managers in a department
     * Returns a list of employees who are managers of other employees in the specified department
     */
    @GetMapping("/managers-in-department/{departmentId}")
    public ResponseEntity<java.util.List<Map<String, Object>>> getManagersInDepartment(
            @PathVariable Long departmentId) {
        
        // Verify department exists
        departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
        
        // Get all employees in the department
        Page<Employees> employeesPage = employeeRepo.findByDepartmentDepartmentId(
                departmentId, 
                PageRequest.of(0, 1000) // Get all employees
        );
        
        // Extract unique managers
        java.util.Set<Long> managerIds = new java.util.HashSet<>();
        java.util.Map<Long, Employees> managersMap = new java.util.HashMap<>();
        
        for (Employees emp : employeesPage.getContent()) {
            if (emp.getManager() != null) {
                Long managerId = emp.getManager().getEmployeeId();
                if (!managerIds.contains(managerId)) {
                    managerIds.add(managerId);
                    managersMap.put(managerId, emp.getManager());
                }
            }
        }
        
        // Build response
        java.util.List<Map<String, Object>> managersList = new java.util.ArrayList<>();
        for (Employees manager : managersMap.values()) {
            Map<String, Object> managerData = new java.util.HashMap<>();
            managerData.put("employeeId", manager.getEmployeeId());
            managerData.put("firstName", manager.getFirstName());
            managerData.put("lastName", manager.getLastName());
            managerData.put("email", manager.getEmail());
            
            if (manager.getJob() != null) {
                Map<String, Object> jobInfo = new java.util.HashMap<>();
                jobInfo.put("jobId", manager.getJob().getJobId());
                jobInfo.put("jobTitle", manager.getJob().getJobTitle());
                managerData.put("job", jobInfo);
            }
            
            managersList.add(managerData);
        }
        
        // Sort by employee ID
        managersList.sort((m1, m2) -> 
            ((Long) m1.get("employeeId")).compareTo((Long) m2.get("employeeId"))
        );
        
        return ResponseEntity.ok(managersList);
    }

    /**
     * GET endpoint to fetch employees by manager with pagination
     */
    @GetMapping("/by-manager/{managerId}")
    public ResponseEntity<Map<String, Object>> getEmployeesByManager(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Verify manager exists
        employeeRepo.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + managerId));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Employees> employeesPage = employeeRepo.findByManagerEmployeeId(managerId, pageable);
        
        java.util.List<Map<String, Object>> employeeList = new java.util.ArrayList<>();
        
        for (Employees emp : employeesPage.getContent()) {
            Map<String, Object> empData = new java.util.HashMap<>();
            empData.put("employeeId", emp.getEmployeeId());
            empData.put("firstName", emp.getFirstName());
            empData.put("lastName", emp.getLastName());
            empData.put("email", emp.getEmail());
            empData.put("phoneNumber", emp.getPhoneNumber());
            empData.put("salary", emp.getSalary());
            empData.put("hireDate", emp.getHireDate());
            
            // Job info
            if (emp.getJob() != null) {
                Map<String, Object> jobInfo = new java.util.HashMap<>();
                jobInfo.put("jobId", emp.getJob().getJobId());
                jobInfo.put("jobTitle", emp.getJob().getJobTitle());
                empData.put("job", jobInfo);
            }
            
            // Department info
            if (emp.getDepartment() != null) {
                Map<String, Object> deptInfo = new java.util.HashMap<>();
                deptInfo.put("departmentId", emp.getDepartment().getDepartmentId());
                deptInfo.put("departmentName", emp.getDepartment().getDepartmentName());
                empData.put("department", deptInfo);
            }
            
            employeeList.add(empData);
        }
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("employees", employeeList);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", employeesPage.getSize());
        pageInfo.put("totalElements", employeesPage.getTotalElements());
        pageInfo.put("totalPages", employeesPage.getTotalPages());
        pageInfo.put("number", employeesPage.getNumber());
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT endpoint for full employee replacement (optional)
     * All required fields must be provided
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<Employees> replaceEmployee(
            @PathVariable Long employeeId,
            @RequestBody Map<String, Object> updates) {
        
        return updateEmployee(employeeId, updates);
    }

    /**
     * GET endpoint to search employees with pagination
     * Supports searching by name, email, or employee ID
     * Also supports filtering by department and job
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Get all employees first
        java.util.List<Employees> allEmployees = employeeRepo.findAll();
        
        // Apply filters
        java.util.stream.Stream<Employees> stream = allEmployees.stream();
        
        // Search filter - matches firstName or full name starting with search term (NOT lastName alone)
        if (query != null && !query.trim().isEmpty()) {
            String searchTerm = query.toLowerCase();
            stream = stream.filter(emp -> {
                // Combine first name and last name with a space
                String fullName = ((emp.getFirstName() != null ? emp.getFirstName() : "") + " " + 
                                  (emp.getLastName() != null ? emp.getLastName() : "")).toLowerCase().trim();
                String firstName = emp.getFirstName() != null ? emp.getFirstName().toLowerCase() : "";
                
                // Check if full name starts with search term OR first name starts OR employee ID starts
                // Note: We don't check lastName alone to avoid confusion (e.g., typing "a" shouldn't show "Samuel Adeyemi")
                return fullName.startsWith(searchTerm) || 
                       firstName.startsWith(searchTerm) ||
                       emp.getEmployeeId().toString().startsWith(searchTerm);
            });
        }
        
        // Email filter - separate parameter for email search
        if (email != null && !email.trim().isEmpty()) {
            String emailTerm = email.toLowerCase();
            stream = stream.filter(emp -> 
                emp.getEmail() != null && emp.getEmail().toLowerCase().startsWith(emailTerm)
            );
        }
        
        // Department filter
        if (departmentId != null) {
            stream = stream.filter(emp -> 
                emp.getDepartment() != null && 
                emp.getDepartment().getDepartmentId().equals(departmentId)
            );
        }
        
        // Job filter
        if (jobId != null && !jobId.trim().isEmpty()) {
            stream = stream.filter(emp -> 
                emp.getJob() != null && 
                emp.getJob().getJobId().equals(jobId)
            );
        }
        
        // Collect filtered results
        java.util.List<Employees> filteredList = stream.collect(java.util.stream.Collectors.toList());
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, filteredList.size());
        java.util.List<Employees> pageContent = filteredList.subList(start, end);
        
        // Convert to DTOs
        java.util.List<Map<String, Object>> employeesList = pageContent.stream()
                .map(this::convertEmployeeToMap)
                .collect(java.util.stream.Collectors.toList());
        
        // Build response
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("employeeses", employeesList);
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
 * GET /api/v1/employees/by-job?jobId=IT_PROG&page=0&size=100
 * Used by job-details page to show all employees under a specific job
 */
@GetMapping("/by-job")
public ResponseEntity<Map<String, Object>> getEmployeesByJob(
        @RequestParam String jobId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Employees> empPage = employeeRepo.findByJobJobId(jobId, pageable);

    java.util.List<Map<String, Object>> list = empPage.getContent().stream()
            .map(this::convertEmployeeToMap)
            .collect(java.util.stream.Collectors.toList());

    Map<String, Object> embedded = new java.util.HashMap<>();
    embedded.put("employees", list);

    Map<String, Object> pageInfo = new java.util.HashMap<>();
    pageInfo.put("size", empPage.getSize());
    pageInfo.put("totalElements", empPage.getTotalElements());
    pageInfo.put("totalPages", empPage.getTotalPages());
    pageInfo.put("number", empPage.getNumber());

    Map<String, Object> response = new java.util.HashMap<>();
    response.put("_embedded", embedded);
    response.put("page", pageInfo);

    return ResponseEntity.ok(response);
    }
}
