package com.example.Human_Resource_Managment.Service;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Repository.DepartmentRepo;
import com.example.Human_Resource_Managment.Repository.EmployeeRepo;
import com.example.Human_Resource_Managment.ExceptionHandling.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * Change department manager and update all employees in the department
     * to report to the new manager.
     * 
     * This is a transactional operation that:
     * 1. Updates the department's manager
     * 2. Updates all employees in the department to have the new manager
     * 3. Ensures the new manager is assigned to this department
     * 
     * @param departmentId The department ID
     * @param newManagerId The new manager's employee ID
     * @return The updated department
     */
    @Transactional
    public Department changeDepartmentManager(Long departmentId, Long newManagerId) {
        // Fetch department
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        // Fetch new manager
        Employees newManager = employeeRepo.findById(newManagerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + newManagerId));

        // Update department manager
        department.setManager(newManager);
        
        // Ensure the new manager is assigned to this department
        if (newManager.getDepartment() == null || !newManager.getDepartment().getDepartmentId().equals(departmentId)) {
            newManager.setDepartment(department);
            employeeRepo.save(newManager);
        }

        // Get all employees in this department
        Page<Employees> employeesPage = employeeRepo.findByDepartmentDepartmentId(
                departmentId, 
                PageRequest.of(0, 1000) // Get all employees
        );
        
        List<Employees> employees = employeesPage.getContent();
        
        System.out.println("=== Updating manager for " + employees.size() + " employees in department " + departmentId + " ===");
        
        // Update manager_id for all employees in the department
        int updatedCount = 0;
        for (Employees employee : employees) {
            // Don't set the manager as their own manager
            if (!employee.getEmployeeId().equals(newManagerId)) {
                employee.setManager(newManager);
                employeeRepo.save(employee);
                updatedCount++;
                System.out.println("Updated employee " + employee.getEmployeeId() + " (" + 
                    employee.getFirstName() + " " + employee.getLastName() + ") manager to " + newManagerId);
            }
        }
        
        System.out.println("=== Updated " + updatedCount + " employees ===");

        // Save and return the updated department
        return departmentRepo.save(department);
    }
}
