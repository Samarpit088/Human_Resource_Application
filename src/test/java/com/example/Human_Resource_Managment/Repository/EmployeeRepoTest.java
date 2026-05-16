package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepoTest {

    @Autowired
    private EmployeeRepo employeeRepo;

    // =========================================================
    // save()
    // =========================================================

    @Test
    @DisplayName("REPO_SAVE_001")
    void testSaveEmployee() {

        Employees employee = new Employees();

        employee.setEmployeeId(999L);

        employee.setFirstName("Navya");

        employee.setLastName("Aggarwal");

        employee.setEmail(
                "navya" + System.currentTimeMillis()
        );

        employee.setPhoneNumber("9876543210");

        employee.setHireDate(LocalDate.now());

        employee.setSalary(
                BigDecimal.valueOf(7000)
        );

        employee.setCommissionPct(
                BigDecimal.valueOf(0.20)
        );

        // =========================
        // JOB
        // =========================

        Job job = new Job();

        job.setJobId("IT_PROG");

        employee.setJob(job);

        // =========================
        // DEPARTMENT
        // =========================

        Department department =
                new Department();

        department.setDepartmentId(60L);

        employee.setDepartment(department);

        // =========================
        // MANAGER
        // =========================

        Employees manager =
                new Employees();

        manager.setEmployeeId(103L);

        employee.setManager(manager);

        Employees savedEmployee =
                employeeRepo.save(employee);

        assertNotNull(savedEmployee);

        assertEquals(
                "Navya",
                savedEmployee.getFirstName()
        );

        assertEquals(
                "IT_PROG",
                savedEmployee.getJob().getJobId()
        );

        System.out.println(
                "Employee Saved Successfully"
        );
    }

    // =========================================================
    // findById()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDID_001")
    void testFindById_Valid() {

        Optional<Employees> employee =
                employeeRepo.findById(100L);

        assertTrue(employee.isPresent());

        employee.ifPresent(emp -> {

            System.out.println(
                    "Name : "
                            + emp.getFirstName()
                            + " "
                            + emp.getLastName()
            );

            System.out.println(
                    "Email : "
                            + emp.getEmail()
            );
        });
    }

    @Test
    @DisplayName("REPO_FINDID_002")
    void testFindById_Invalid() {

        Optional<Employees> employee =
                employeeRepo.findById(99999L);

        assertFalse(employee.isPresent());
    }

    // =========================================================
    // findAll()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDALL_001")
    void testFindAll() {

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by("salary")
                                .descending()
                );

        Page<Employees> employees =
                employeeRepo.findAll(pageable);

        assertNotNull(employees);

        assertFalse(employees.isEmpty());

        System.out.println(
                "Total Employees : "
                        + employees.getTotalElements()
        );

        employees.forEach(emp -> {

            System.out.println(
                    emp.getEmployeeId()
                            + " "
                            + emp.getFirstName()
                            + " "
                            + emp.getSalary()
            );
        });
    }

    // =========================================================
    // findByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDEMAIL_001")
    void testFindByEmail() {

        Optional<Employees> employee =
                employeeRepo.findByEmail(
                        "SKING"
                );

        assertTrue(employee.isPresent());

        employee.ifPresent(emp ->

                System.out.println(
                        emp.getFirstName()
                )
        );
    }

    @Test
    @DisplayName("REPO_FINDEMAIL_002")
    void testFindByEmail_Invalid() {

        Optional<Employees> employee =
                employeeRepo.findByEmail(
                        "INVALID_EMAIL"
                );

        assertFalse(employee.isPresent());
    }

    // =========================================================
    // existsByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_EXISTSEMAIL_001")
    void testExistsByEmail_True() {

        boolean exists =
                employeeRepo.existsByEmail(
                        "SKING"
                );

        assertTrue(exists);

        System.out.println(
                "Exists : " + exists
        );
    }

    @Test
    @DisplayName("REPO_EXISTSEMAIL_002")
    void testExistsByEmail_False() {

        boolean exists =
                employeeRepo.existsByEmail(
                        "XYZ123"
                );

        assertFalse(exists);
    }

    // =========================================================
    // findByDepartmentDepartmentId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDDEPT_001")
    void testFindByDepartmentId() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByDepartmentDepartmentId(
                        60L,
                        pageable
                );

        assertFalse(employees.isEmpty());

        employees.forEach(emp ->

                System.out.println(
                        emp.getFirstName()
                                + " -> "
                                + emp.getDepartment()
                                .getDepartmentId()
                )
        );
    }

    // =========================================================
    // findByJobJobId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDJOB_001")
    void testFindByJobId() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByJobJobId(
                        "IT_PROG",
                        pageable
                );

        assertFalse(employees.isEmpty());

        employees.forEach(emp ->

                System.out.println(
                        emp.getFirstName()
                                + " -> "
                                + emp.getJob()
                                .getJobId()
                )
        );
    }

    // =========================================================
    // findBySalaryBetween()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDSALARY_001")
    void testFindBySalaryBetween() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findBySalaryBetween(
                        BigDecimal.valueOf(5000),
                        BigDecimal.valueOf(10000),
                        pageable
                );

        assertFalse(employees.isEmpty());

        employees.forEach(emp ->

                System.out.println(
                        emp.getFirstName()
                                + " : "
                                + emp.getSalary()
                )
        );
    }

    // =========================================================
    // findByFirstNameContainingIgnoreCase()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDNAME_001")
    void testFindByFirstNameContainingIgnoreCase() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo
                        .findByFirstNameContainingIgnoreCase(
                                "ste",
                                pageable
                        );

        assertFalse(employees.isEmpty());

        employees.forEach(emp ->

                System.out.println(
                        emp.getFirstName()
                )
        );
    }

    // =========================================================
    // findByManagerEmployeeId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDMANAGER_001")
    void testFindByManagerEmployeeId() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByManagerEmployeeId(
                        103L,
                        pageable
                );

        assertFalse(employees.isEmpty());

        employees.forEach(emp ->

                System.out.println(
                        emp.getFirstName()
                                + " -> Manager : "
                                + emp.getManager()
                                .getEmployeeId()
                )
        );
    }

    // =========================================================
    // deleteById()
    // =========================================================

    @Test
    @DisplayName("REPO_DELETE_001")
    void testDeleteById() {

        Long employeeId = 999L;

        boolean existsBeforeDelete =
                employeeRepo.existsById(employeeId);

        assertTrue(existsBeforeDelete);

        employeeRepo.deleteById(employeeId);

        Optional<Employees> employee =
                employeeRepo.findById(employeeId);

        assertFalse(employee.isPresent());

        System.out.println(
                "Employee deleted successfully"
        );
    }

    // =========================================================
    // existsById()
    // =========================================================

    @Test
    @DisplayName("REPO_EXISTSID_001")
    void testExistsById_True() {

        boolean exists =
                employeeRepo.existsById(100L);

        assertTrue(exists);
    }

    @Test
    @DisplayName("REPO_EXISTSID_002")
    void testExistsById_False() {

        boolean exists =
                employeeRepo.existsById(99999L);

        assertFalse(exists);
    }
}