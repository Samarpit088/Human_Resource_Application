package com.example.Human_Resource_Managment.Controller;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Locations;
import com.example.Human_Resource_Managment.Repository.DepartmentRepo;
import com.example.Human_Resource_Managment.Repository.EmployeeRepo;
import com.example.Human_Resource_Managment.Repository.LocationsRepo;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Custom controller for Department operations
 * 
 * Spring Data REST is disabled for departments (exported = false)
 * This controller handles all CRUD operations with proper lazy loading handling
 */
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private LocationsRepo locationsRepo;

    @Autowired
    private com.example.Human_Resource_Managment.Service.DepartmentService departmentService;

    /**
     * GET endpoint to fetch all departments with pagination
     */
    @GetMapping("/api/v1/departments")
    public ResponseEntity<Map<String, Object>> getDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Department> departmentsPage = departmentRepo.findAll(pageable);
        
        // Convert to DTOs to avoid lazy loading issues
        java.util.List<Map<String, Object>> departmentsList = departmentsPage.getContent().stream()
                .map(this::convertDepartmentToMap)
                .collect(java.util.stream.Collectors.toList());
        
        // Build response similar to Spring Data REST format
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("departmentses", departmentsList);
        response.put("_embedded", embedded);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", departmentsPage.getSize());
        pageInfo.put("totalElements", departmentsPage.getTotalElements());
        pageInfo.put("totalPages", departmentsPage.getTotalPages());
        pageInfo.put("number", departmentsPage.getNumber());
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET endpoint to fetch a single department
     */
    @GetMapping("/api/v1/departments/{departmentId}")
    public ResponseEntity<Map<String, Object>> getDepartment(@PathVariable Long departmentId) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        return ResponseEntity.ok(convertDepartmentToMap(department));
    }

    /**
     * Convert Department entity to Map to avoid lazy loading issues
     */
    private Map<String, Object> convertDepartmentToMap(Department department) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("departmentId", department.getDepartmentId());
        map.put("departmentName", department.getDepartmentName());
        
        // Add manager info (handle lazy loading)
        if (department.getManager() != null) {
            map.put("managerId", department.getManager().getEmployeeId());
            map.put("managerFirstName", department.getManager().getFirstName());
            map.put("managerLastName", department.getManager().getLastName());
        } else {
            map.put("managerId", null);
            map.put("managerFirstName", null);
            map.put("managerLastName", null);
        }
        
        // Add location info (handle lazy loading)
        if (department.getLocation() != null) {
            map.put("locationId", department.getLocation().getLocationId());
            map.put("locationCity", department.getLocation().getCity());
        } else {
            map.put("locationId", null);
            map.put("locationCity", null);
        }
        
        return map;
    }

    /**
     * POST endpoint to create a new department
     * Uses READ_COMMITTED transaction isolation for better performance during load testing
     * 
     * Supports:
     * - departmentId (required)
     * - departmentName (required)
     * - locationId (optional)
     * - managerId (optional)
     * 
     * Example request body:
     * {
     *   "departmentId": 280,
     *   "departmentName": "IT Support",
     *   "location": { "locationId": 1700 },
     *   "manager": { "employeeId": 100 }
     * }
     */
    @PostMapping("/api/v1/departments")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Map<String, Object>> createDepartment(@RequestBody Map<String, Object> departmentData) {
        try {
            Department department = new Department();
            
            // Set required fields
            if (!departmentData.containsKey("departmentId")) {
                throw new IllegalArgumentException("departmentId is required");
            }
            if (!departmentData.containsKey("departmentName")) {
                throw new IllegalArgumentException("departmentName is required");
            }
            
            Long departmentId = Long.valueOf(departmentData.get("departmentId").toString());
            
            // Check if department ID already exists
            if (departmentRepo.existsById(departmentId)) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Department ID " + departmentId + " already exists");
            }
            
            department.setDepartmentId(departmentId);
            department.setDepartmentName((String) departmentData.get("departmentName"));
            
            // Set location if provided
            if (departmentData.containsKey("location") && departmentData.get("location") != null) {
                Map<String, Object> locationMap = (Map<String, Object>) departmentData.get("location");
                if (locationMap.containsKey("locationId")) {
                    Long locationId = Long.valueOf(locationMap.get("locationId").toString());
                    Locations location = locationsRepo.findById(locationId)
                            .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
                    department.setLocation(location);
                }
            }
            
            // Set manager if provided
            if (departmentData.containsKey("manager") && departmentData.get("manager") != null) {
                Map<String, Object> managerMap = (Map<String, Object>) departmentData.get("manager");
                if (managerMap.containsKey("employeeId")) {
                    Long managerId = Long.valueOf(managerMap.get("employeeId").toString());
                    Employees manager = employeeRepo.findById(managerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + managerId));
                    department.setManager(manager);
                }
            }
            
            // Save the department with proper error handling for race conditions
            Department savedDepartment;
            try {
                savedDepartment = departmentRepo.save(department);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                throw new com.example.Human_Resource_Managment.ExceptionHandling.ValidationException(
                    "Department ID " + department.getDepartmentId() + " already exists. Another user may have created this department.");
            }
            
            // If manager was set, update the manager's department assignment
            // Note: For a new department, there are no existing employees to update
            if (savedDepartment.getManager() != null) {
                Employees manager = savedDepartment.getManager();
                // Only update manager's department if not already set to this department
                if (manager.getDepartment() == null || !manager.getDepartment().getDepartmentId().equals(savedDepartment.getDepartmentId())) {
                    manager.setDepartment(savedDepartment);
                    employeeRepo.save(manager);
                }
            }
            
            // Convert to Map to avoid lazy loading issues
            return ResponseEntity.ok(convertDepartmentToMap(savedDepartment));
        } catch (com.example.Human_Resource_Managment.ExceptionHandling.ValidationException | 
                 com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException |
                 IllegalArgumentException e) {
            // Re-throw known exceptions to be handled by GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            // Log unexpected errors with full stack trace
            System.err.println("Unexpected error creating department: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create department: " + e.getMessage(), e);
        }
    }

    /**
     * PATCH endpoint to partially update a department (e.g., change manager)
     * 
     * Supports updating:
     * - departmentName
     * - managerId (employee ID to assign as manager) - This will also update all employees in the department
     * - locationId
     * 
     * Example request body:
     * {
     *   "managerId": 100,
     *   "departmentName": "Administration",
     *   "locationId": 1700
     * }
     */
    @PatchMapping("/api/v1/departments/{departmentId}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long departmentId,
            @RequestBody Map<String, Object> updates) {
        
        // Fetch department
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        boolean managerChanged = false;
        Long newManagerId = null;

        // Update department name if provided
        if (updates.containsKey("departmentName") && updates.get("departmentName") != null) {
            String deptName = (String) updates.get("departmentName");
            if (!deptName.trim().isEmpty()) {
                department.setDepartmentName(deptName);
            }
        }

        // Update location if provided
        if (updates.containsKey("locationId") && updates.get("locationId") != null) {
            try {
                Long locationId = Long.valueOf(updates.get("locationId").toString());
                Locations location = locationsRepo.findById(locationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
                department.setLocation(location);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid locationId format");
            }
        }

        // Check if manager is being changed
        if (updates.containsKey("managerId") && updates.get("managerId") != null) {
            try {
                newManagerId = Long.valueOf(updates.get("managerId").toString());
                
                // Check if manager actually changed
                if (department.getManager() == null || !department.getManager().getEmployeeId().equals(newManagerId)) {
                    managerChanged = true;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid managerId format");
            }
        }

        // If manager changed, use the transactional service to update department and all employees
        if (managerChanged && newManagerId != null) {
            // Save any other changes first
            if (updates.containsKey("departmentName") || updates.containsKey("locationId")) {
                department = departmentRepo.save(department);
            }
            
            // Use service to change manager (transactional - updates department and all employees)
            Department updatedDept = departmentService.changeDepartmentManager(departmentId, newManagerId);
            return ResponseEntity.ok(updatedDept);
        } else {
            // No manager change, just save the department
            Department updatedDept = departmentRepo.save(department);
            return ResponseEntity.ok(updatedDept);
        }
    }

    /**
     * GET endpoint to search departments with pagination
     * Supports searching by name or ID and filtering by location
     */
    @GetMapping("/api/v1/departments/search")
    public ResponseEntity<Map<String, Object>> searchDepartments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Get all departments
        List<Department> allDepartments = departmentRepo.findAll();
        
        // Apply filters
        java.util.stream.Stream<Department> stream = allDepartments.stream();
        
        // Search filter - matches names starting with the search term
        if (query != null && !query.trim().isEmpty()) {
            String searchTerm = query.toLowerCase();
            stream = stream.filter(dept -> 
                (dept.getDepartmentName() != null && dept.getDepartmentName().toLowerCase().startsWith(searchTerm)) ||
                dept.getDepartmentId().toString().startsWith(searchTerm)
            );
        }
        
        // Location filter
        if (locationId != null) {
            stream = stream.filter(dept -> 
                dept.getLocation() != null && 
                dept.getLocation().getLocationId().equals(locationId)
            );
        }
        
        // Collect filtered results
        List<Department> filteredList = stream.collect(java.util.stream.Collectors.toList());
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, filteredList.size());
        List<Department> pageContent = filteredList.subList(start, end);
        
        // Convert to maps
        List<Map<String, Object>> departmentsList = pageContent.stream()
                .map(dept -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("departmentId", dept.getDepartmentId());
                    map.put("departmentName", dept.getDepartmentName());
                    
                    if (dept.getManager() != null) {
                        map.put("managerId", dept.getManager().getEmployeeId());
                        map.put("managerFirstName", dept.getManager().getFirstName());
                        map.put("managerLastName", dept.getManager().getLastName());
                    }
                    
                    if (dept.getLocation() != null) {
                        Map<String, Object> locationMap = new java.util.HashMap<>();
                        locationMap.put("locationId", dept.getLocation().getLocationId());
                        locationMap.put("city", dept.getLocation().getCity());
                        map.put("location", locationMap);
                        map.put("locationId", dept.getLocation().getLocationId());
                    }
                    
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Build response
        Map<String, Object> response = new java.util.HashMap<>();
        
        Map<String, Object> embedded = new java.util.HashMap<>();
        embedded.put("departments", departmentsList);
        response.put("_embedded", embedded);
        
        Map<String, Object> pageInfo = new java.util.HashMap<>();
        pageInfo.put("size", size);
        pageInfo.put("totalElements", filteredList.size());
        pageInfo.put("totalPages", (int) Math.ceil((double) filteredList.size() / size));
        pageInfo.put("number", page);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(response);
    }
}
