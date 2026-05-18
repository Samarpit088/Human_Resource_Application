package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

@Projection(
        name = "employeeView",
        types = Employees.class
)
public interface EmployeeViewProjection {

    @Value("#{target.employeeId}")
    Long getEmployeeId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhoneNumber();

    LocalDate getHireDate();

    BigDecimal getSalary();

    BigDecimal getCommissionPct();

    Job getJob();

    Department getDepartment();
}