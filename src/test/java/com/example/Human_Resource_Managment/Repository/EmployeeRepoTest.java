package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepoTest {

    @Autowired
    private EmployeeRepo employeeRepo;

    // =========================================================
    // findById()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDID_001 : Valid employee id")
    void testFindById_ValidId() {

        Optional<Employees> employee =
                employeeRepo.findById(100);

        assertTrue(employee.isPresent());
    }

    @Test
    @DisplayName("REPO_FINDID_002 : Invalid employee id")
    void testFindById_InvalidId() {

        Optional<Employees> employee =
                employeeRepo.findById(9999);

        assertFalse(employee.isPresent());
    }

    // =========================================================
    // findAll()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDALL_001 : Fetch all employees")
    void testFindAll() {

        List<Employees> employees =
                employeeRepo.findAll();

        assertFalse(employees.isEmpty());
    }

//    // =========================================================
//    // save()
//    // =========================================================
//
//    @Test
//    @DisplayName("REPO_SAVE_001 : Save employee")
//    void testSaveEmployee() {
//
//        Departments department = new Departments();
//        department.setDepartmentId(60);
//
//        Jobs job = new Jobs();
//        job.setJobId("IT_PROG");
//
//        Employees manager = new Employees();
//        manager.setEmployeeId(103);
//
//        Employees employee = new Employees();
//
//        employee.setFirstName("Navya");
//        employee.setLastName("Aggarwal");
//        employee.setEmail("NAVAGGAR");
//        employee.setPhoneNumber("9999999999");
//        employee.setSalary(BigDecimal.valueOf(5000));
//        employee.setDepartment(department);
//        employee.setJob(job);
//        employee.setManager(manager);
//
//        Employees savedEmployee =
//                employeeRepo.save(employee);
//
//        assertNotNull(savedEmployee.getEmployeeId());
//    }
//
//    // =========================================================
//    // deleteById()
//    // =========================================================
//
//    @Test
//    @DisplayName("REPO_DELETE_001 : Delete employee")
//    void testDeleteById() {
//
//        Departments department = new Departments();
//        department.setDepartmentId(60);
//
//        Jobs job = new Jobs();
//        job.setJobId("IT_PROG");
//
//        Employees employee = new Employees();
//
//        employee.setFirstName("Temp");
//        employee.setLastName("Delete");
//        employee.setEmail("TEMPDEL");
//        employee.setPhoneNumber("8888888888");
//        employee.setSalary(BigDecimal.valueOf(4000));
//        employee.setDepartment(department);
//        employee.setJob(job);
//
//        Employees savedEmployee =
//                employeeRepo.save(employee);
//
//        Integer employeeId =
//                savedEmployee.getEmployeeId();
//
//        employeeRepo.deleteById(employeeId);
//
//        Optional<Employees> deletedEmployee =
//                employeeRepo.findById(employeeId);
//
//        assertFalse(deletedEmployee.isPresent());
//    }

    // =========================================================
    // findByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDEMAIL_001 : Valid email")
    void testFindByEmail() {

        Optional<Employees> employee =
                employeeRepo.findByEmail("SKING");

        assertTrue(employee.isPresent());
    }

    @Test
    @DisplayName("REPO_FINDEMAIL_002 : Invalid email")
    void testFindByEmail_Invalid() {

        Optional<Employees> employee =
                employeeRepo.findByEmail("INVALID");

        assertFalse(employee.isPresent());
    }

    // =========================================================
    // existsByEmail()
    // =========================================================

    @Test
    @DisplayName("REPO_EXISTSEMAIL_001 : Existing email")
    void testExistsByEmail() {

        boolean exists =
                employeeRepo.existsByEmail("SKING");

        assertTrue(exists);
    }

    @Test
    @DisplayName("REPO_EXISTSEMAIL_002 : Non existing email")
    void testExistsByEmail_Invalid() {

        boolean exists =
                employeeRepo.existsByEmail("XYZ");

        assertFalse(exists);
    }

    // =========================================================
    // findByDepartmentDepartmentId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDDEPT_001 : Valid department id")
    void testFindByDepartmentDepartmentId() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findByDepartmentDepartmentId(
                        60,
                        pageable
                );

        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("REPO_FINDDEPT_002 : Invalid department id")
    void testFindByDepartmentDepartmentId_Invalid() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findByDepartmentDepartmentId(
                        999,
                        pageable
                );

        assertTrue(employees.isEmpty());
    }

    // =========================================================
    // findByJobJobId()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDJOB_001 : Valid job id")
    void testFindByJobJobId() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findByJobJobId(
                        "IT_PROG",
                        pageable
                );

        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("REPO_FINDJOB_002 : Invalid job id")
    void testFindByJobJobId_Invalid() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findByJobJobId(
                        "TEST_JOB",
                        pageable
                );

        assertTrue(employees.isEmpty());
    }

    // =========================================================
    // findBySalaryBetween()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDSALARY_001 : Valid salary range")
    void testFindBySalaryBetween() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findBySalaryBetween(
                        BigDecimal.valueOf(4000),
                        BigDecimal.valueOf(10000),
                        pageable
                );

        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("REPO_FINDSALARY_002 : No employees in range")
    void testFindBySalaryBetween_NoMatch() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo.findBySalaryBetween(
                        BigDecimal.valueOf(100000),
                        BigDecimal.valueOf(200000),
                        pageable
                );

        assertTrue(employees.isEmpty());
    }

    // =========================================================
    // findByFirstNameContainingIgnoreCase()
    // =========================================================

    @Test
    @DisplayName("REPO_FINDNAME_001 : Exact first name")
    void testFindByFirstNameContainingIgnoreCase() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo
                        .findByFirstNameContainingIgnoreCase(
                                "Steven",
                                pageable
                        );

        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("REPO_FINDNAME_002 : Partial keyword")
    void testFindByFirstNameContainingIgnoreCase_Partial() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo
                        .findByFirstNameContainingIgnoreCase(
                                "ste",
                                pageable
                        );

        assertFalse(employees.isEmpty());
    }

    @Test
    @DisplayName("REPO_FINDNAME_003 : No matching records")
    void testFindByFirstNameContainingIgnoreCase_NoMatch() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employees> employees =
                employeeRepo
                        .findByFirstNameContainingIgnoreCase(
                                "xyz",
                                pageable
                        );

        assertTrue(employees.isEmpty());
    }
}