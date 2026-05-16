package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "employeeView",
        types = Employees.class
)
public interface EmployeeProjection {

    Long getEmployeeId();

    String getFirstName();

    String getLastName();


}
