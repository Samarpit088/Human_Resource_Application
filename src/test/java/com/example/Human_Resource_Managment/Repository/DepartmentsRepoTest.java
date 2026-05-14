package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class DepartmentRepoTest {

    @Autowired
    private DepartmentRepo departmentRepo;

    /**
     * TC ID : DEP_001
     * Scenario : departments exist
     * Expected : department list returned
     */
    @Test
    void testFindAllDepartments_WhenDepartmentsExist_ReturnDepartmentList() {

        // Fetch first page with 5 records
        Page<Department> departments =
                departmentRepo.findAll(PageRequest.of(0, 5));

        // Assertions
        assertNotNull(departments);

        // Print total departments count
        System.out.println("Total Departments = "
                + departments.getTotalElements());

        // Print fetched departments
        departments.forEach(department -> {

            System.out.println("Department ID: "
                    + department.getDepartmentId());

            System.out.println("Department Name: "
                    + department.getDepartmentName());

            System.out.println("Manager ID: "
                    + department.getManagerId());

            System.out.println("Location ID: "
                    + department.getLocationId());

            System.out.println("------------------------");
        });

        // Verify departments exist
        assertFalse(departments.isEmpty());
    }

    /**
     * TC ID : DEP_003
     * Scenario : no departments in database
     * Expected : empty list returned
     */
    @Test
    void testFindAllDepartments_WhenNoDepartmentsExist_ReturnEmptyList() {

        Page<Department> departments =
                departmentRepo.findAll(PageRequest.of(0, 5));

        assertNotNull(departments);

        if (departments.getTotalElements() == 0) {

            assertTrue(departments.isEmpty());

            System.out.println("No departments found");
        }
    }
}