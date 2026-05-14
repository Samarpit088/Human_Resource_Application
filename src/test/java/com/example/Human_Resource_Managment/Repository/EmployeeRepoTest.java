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
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/hr",
        "spring.datasource.username=root",
        "spring.datasource.password=root1234",
        "spring.jpa.hibernate.ddl-auto=none",
//        "spring.jpa.show-sql=true"
})
class EmployeeRepoTest {

    @Autowired
    private EmployeeRepo employeeRepo;

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

}