package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employees, Integer> {
    Optional<Employees> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employees> findByDepartmentDepartmentId(
            Integer departmentId,
            Pageable pageable
    );

    Page<Employees> findByJobJobId(
            String jobId,
            Pageable pageable
    );

    Page<Employees> findBySalaryBetween(
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable
    );

    Page<Employees> findByFirstNameContainingIgnoreCase(
            String firstName,
            Pageable pageable
    );
}