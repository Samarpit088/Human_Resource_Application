package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "employeeDetailedView", types = Employees.class)
public interface EmployeeDetailedView {

    Long getEmployeeId();

    String getFirstName();

    String getLastName();

    String getEmail();

    BigDecimal getSalary();
}