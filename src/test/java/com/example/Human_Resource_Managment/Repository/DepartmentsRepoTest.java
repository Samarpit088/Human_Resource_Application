package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DepartmentsRepoTest {

    @Autowired
    private DepartmentRepo departmentRepo;

    /**
     * TC ID : DEP_001
     * Scenario : departments exist
     * Expected : department list returned
     */
    @Test
    void testFindAll_WhenDepartmentsExist_ReturnDepartmentList() {

        Department department1 = new Department();
        department1.setDepartmentId(1L);
        department1.setDepartmentName("HR");
        department1.setManagerId(101L);
        department1.setLocationId(1001L);

        Department department2 = new Department();
        department2.setDepartmentId(2L);
        department2.setDepartmentName("IT");
        department2.setManagerId(102L);
        department2.setLocationId(1002L);

        departmentRepo.saveAll(List.of(department1, department2));

        Pageable pageable = PageRequest.of(0, 5);

        Page<Department> result = departmentRepo.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals("HR",
                result.getContent().get(0).getDepartmentName());

        assertEquals("IT",
                result.getContent().get(1).getDepartmentName());
    }

    /**
     * TC ID : DEP_003
     * Scenario : no departments in database
     * Expected : empty list returned
     */
    @Test
    void testFindAll_WhenNoDepartmentsExist_ReturnEmptyList() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Department> result = departmentRepo.findAll(pageable);

        assertNotNull(result);

        assertTrue(result.getContent().isEmpty());

        assertEquals(0, result.getTotalElements());
    }
}