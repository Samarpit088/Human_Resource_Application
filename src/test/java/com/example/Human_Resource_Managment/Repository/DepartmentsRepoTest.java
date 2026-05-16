package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Locations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class DepartmentsRepoTest {

    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * TC ID : DEP_001
     * Scenario : departments exist
     * Expected : department list returned
     */
    // =========================================================
// findByManagerEmployeeId()
// =========================================================

    @Test
    @DisplayName("REPO_FINDMANAGER_001")
    void testFindByManagerEmployeeId_Valid() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByManagerEmployeeId(
                        103L,
                        pageable
                );

        assertNotNull(employees);

        assertFalse(employees.isEmpty());

        employees.forEach(employee ->

                System.out.println(
                        employee.getFirstName()
                                + " -> Manager : "
                                + employee.getManager()
                                .getEmployeeId()
                )
        );
    }

    @Test
    @DisplayName("REPO_FINDMANAGER_002")
    void testFindByManagerEmployeeId_Invalid() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByManagerEmployeeId(
                        99999L,
                        pageable
                );

        assertNotNull(employees);

        assertTrue(employees.isEmpty());

        System.out.println(
                "No employees found under this manager"
        );
    }

    // =========================================================
// getManager()
// =========================================================

    @Test
    @DisplayName("REPO_GETMANAGER_001")
    void testGetManager_Invalid() {

        Optional<Employees> employee =
                employeeRepo.findById(99999L);

        assertFalse(employee.isPresent());

        System.out.println(
                "Employee not found, manager cannot be fetched"
        );
    }


    // =========================================================
// save()
// =========================================================

    @Test
    @DisplayName("REPO_SAVEDEPT_001")
    void testSaveDepartment() {

        Department department =
                new Department();

        department.setDepartmentId(999L);

        department.setDepartmentName(
                "Artificial Intelligence"
        );

        // =========================
        // MANAGER
        // =========================

        Employees manager =
                new Employees();

        manager.setEmployeeId(103L);

        department.setManager(manager);

        // =========================
        // LOCATION
        // =========================

        Locations location =
                new Locations();

        location.setLocationId(1700L);

        department.setLocation(location);

        Department savedDepartment =
                departmentRepo.save(
                        department
                );

        assertNotNull(savedDepartment);

        assertEquals(
                "Artificial Intelligence",
                savedDepartment.getDepartmentName()
        );

        assertEquals(
                103L,
                savedDepartment.getManager()
                        .getEmployeeId()
        );

        assertEquals(
                1700L,
                savedDepartment.getLocation()
                        .getLocationId()
        );

        System.out.println(
                "Department saved successfully"
        );
    }

    // =========================================================
// update()
// =========================================================

    @Test
    @DisplayName("REPO_UPDATEDEPT_001")
    void testUpdateDepartment() {

        Optional<Department> optionalDepartment =
                departmentRepo.findById(60L);

        assertTrue(optionalDepartment.isPresent());

        Department department =
                optionalDepartment.get();

        department.setDepartmentName(
                "Updated IT Department"
        );

        Department updatedDepartment =
                departmentRepo.save(
                        department
                );

        assertNotNull(updatedDepartment);

        assertEquals(
                "Updated IT Department",
                updatedDepartment.getDepartmentName()
        );

        System.out.println(
                "Department updated successfully"
        );
    }

    @Test
    @DisplayName("REPO_UPDATEDEPT_002")
    void testUpdateDepartment_Invalid() {

        Optional<Department> optionalDepartment =
                departmentRepo.findById(99999L);

        assertFalse(optionalDepartment.isPresent());

        System.out.println(
                "Department not found"
        );
    }
}
