package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Projection.EmployeeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.util.Optional;

@RepositoryRestResource(path = "employees",
        excerptProjection = EmployeeProjection.class)
public interface EmployeeRepo
        extends JpaRepository<Employees, Long> {

    Optional<Employees> findByEmail(String email);

    boolean existsByEmail(String email);

    // =====================================================
    // DEPARTMENT
    // employee.department.departmentId
    // =====================================================

    Page<Employees> findByDepartmentDepartmentId(
            Long departmentId,
            Pageable pageable
    );

    // =====================================================
    // JOB
    // employee.job.jobId
    // =====================================================

    Page<Employees> findByJobJobId(
            String jobId,
            Pageable pageable
    );

    // =====================================================
    // SALARY
    // =====================================================

    Page<Employees> findBySalaryBetween(
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable
    );

    // =====================================================
    // FIRST NAME SEARCH
    // =====================================================

    Page<Employees> findByFirstNameContainingIgnoreCase(
            String firstName,
            Pageable pageable
    );

    // =====================================================
    // MANAGER
    // employee.manager.employeeId
    // =====================================================

    Page<Employees> findByManagerEmployeeId(
            Long managerId,
            Pageable pageable
    );

    // =====================================================
    // REGION
    // employee.department.location.country.region.regionId
    // =====================================================

    Page<Employees>
    findByDepartmentLocationCountryRegionRegionId(
            Long regionId,
            Pageable pageable
    );

    // =====================================================
    // COUNTRY NAME
    // employee.department.location.country.countryName
    // =====================================================

    Page<Employees>
    findByDepartmentLocationCountryCountryNameContainingIgnoreCase(
            String countryName,
            Pageable pageable
    );
}