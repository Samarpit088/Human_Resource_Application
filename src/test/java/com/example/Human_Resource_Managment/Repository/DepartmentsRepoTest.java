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

class DepartmentsRepoTest {

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

    @Test
    void testAddDepartment_WhenValidDepartmentProvided_DepartmentAddedSuccessfully() {

        Department department = new Department();

        department.setDepartmentId(500L);
        department.setDepartmentName("Testing");
        department.setManagerId(101L);
        department.setLocationId(1000L);

        Department savedDepartment =
                departmentRepo.save(department);

        assertNotNull(savedDepartment);

        assertEquals(500L,
                savedDepartment.getDepartmentId());

        assertEquals("Testing",
                savedDepartment.getDepartmentName());

        System.out.println("Department Added Successfully");
    }

    @Test
    void testSaveDepartment_WhenDepartmentIdExists_UpdateDepartment() {

        Department department1 = new Department();

        department1.setDepartmentId(600L);
        department1.setDepartmentName("HR");
        department1.setManagerId(101L);
        department1.setLocationId(1000L);

        departmentRepo.save(department1);

        Department department2 = new Department();

        department2.setDepartmentId(600L);
        department2.setDepartmentName("IT");
        department2.setManagerId(102L);
        department2.setLocationId(2000L);

        Department updatedDepartment =
                departmentRepo.save(department2);

        assertNotNull(updatedDepartment);

        assertEquals("IT",
                updatedDepartment.getDepartmentName());

        System.out.println("Existing Department Updated");
    }
}