package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
        department1.setDepartment_id(1L);
        department1.setDepartment_name("HR");
        department1.setManager_id(101L);
        department1.setLocation_id(1001L);

        Department department2 = new Department();
        department2.setDepartment_id(2L);
        department2.setDepartment_name("IT");
        department2.setManager_id(102L);
        department2.setLocation_id(1002L);

        departmentRepo.saveAll(List.of(department1, department2));

        Pageable pageable = PageRequest.of(0, 5);

        Page<Department> result = departmentRepo.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals("HR",
                result.getContent().get(0).getDepartment_name());

        assertEquals("IT",
                result.getContent().get(1).getDepartment_name());
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