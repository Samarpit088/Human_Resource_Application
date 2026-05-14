package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @DisplayName("REPO_SAVE_001 : Save employee")
    void testSaveEmployee() {

        Employees employee = new Employees();

        employee.setEmployeeId(999);

        employee.setFirstName("Navya");
        employee.setLastName("Aggarwal");

        // keep unique email to avoid duplicate constraint
        employee.setEmail("NAVYA999");

        employee.setPhoneNumber("9999999999");

        employee.setHireDate(LocalDate.now());

        // Existing values from DB
        employee.setJobId("IT_PROG");
        employee.setDepartmentId(60);
        employee.setManagerId(103);

        employee.setSalary(BigDecimal.valueOf(5000));

        Employees savedEmployee =
                employeeRepo.save(employee);

        assertNotNull(savedEmployee);

        assertEquals(
                "Navya",
                savedEmployee.getFirstName()
        );

        assertEquals(
                "NAVYA999",
                savedEmployee.getEmail()
        );

        System.out.println("Saved Employee Details");

        System.out.println(
                "Employee ID : "
                        + savedEmployee.getEmployeeId()
        );

        System.out.println(
                "Name : "
                        + savedEmployee.getFirstName()
                        + " "
                        + savedEmployee.getLastName()
        );

        System.out.println(
                "Email : "
                        + savedEmployee.getEmail()
        );

        System.out.println(
                "Department : "
                        + savedEmployee.getDepartmentId()
        );
    }

    // =========================================================
    // findById()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDID_001")
    void testFindById_ValidId() {

        Optional<Employees> employee =
                employeeRepo.findById(100);

        assertTrue(employee.isPresent());

        employee.ifPresent(emp -> {
            System.out.println("ID: " + emp.getEmployeeId());
            System.out.println("Name: " + emp.getFirstName());
            System.out.println("Email: " + emp.getEmail());
        });
    }

    // =========================================================
    // findAll()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDALL_001")
    void testFindAll() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findAll(pageable);

        assertNotNull(employees);
        assertFalse(employees.isEmpty());

        System.out.println("Total Employees = "
                + employees.getTotalElements());

        employees.forEach(emp -> {

            System.out.println("ID: "
                    + emp.getEmployeeId());

            System.out.println("Name: "
                    + emp.getFirstName()
                    + " "
                    + emp.getLastName());

            System.out.println("Email: "
                    + emp.getEmail());

            System.out.println("Salary: "
                    + emp.getSalary());

            System.out.println("-------------------");
        });
    }

    // =========================================================
    // findByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDEMAIL_001")
    void testFindByEmail() {

        Optional<Employees> employee =
                employeeRepo.findByEmail("SKING");

        assertTrue(employee.isPresent());

        employee.ifPresent(emp ->

                System.out.println(
                        emp.getFirstName()
                )
        );
    }

    // =========================================================
    // existsByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_EXISTSEMAIL_001")
    void testExistsByEmail() {

        boolean exists =
                employeeRepo.existsByEmail(
                        "SKING"
                );

        assertTrue(exists);

        System.out.println(
                "Exists = " + exists
        );
    }

    // =========================================================
    // findByDepartmentId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDDEPT_001")
    void testFindByDepartmentId() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByDepartmentId(
                        60,
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
    // findByJobId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDJOB_001")
    void testFindByJobId() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findByJobId(
                        "IT_PROG",
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
    // findBySalaryBetween()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDSALARY_001")
    void testFindBySalaryBetween() {

        Pageable pageable =
                PageRequest.of(0,5);

        Page<Employees> employees =
                employeeRepo.findBySalaryBetween(
                        BigDecimal.valueOf(4000),
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
    // deleteById()
    // =========================================================

    @Test
    @DisplayName("REPO_DELETE_001 : Delete existing employee")
    void testDeleteById() {

        Integer employeeId = 999;

        employeeRepo.deleteById(employeeId);

        Optional<Employees> employee =
                employeeRepo.findById(employeeId);

        assertFalse(employee.isPresent());

        System.out.println(
                "Employee deleted successfully"
        );
    }

}