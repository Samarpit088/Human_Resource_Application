package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "employeeSummary",
        types = Employees.class
)
public interface EmployeeProjection {

    Long getEmployeeId();

    String getFirstName();

    String getLastName();

    JobInfo getJob();

    DepartmentInfo getDepartment();

    interface JobInfo {

        String getJobTitle();
    }

    interface DepartmentInfo {

        String getDepartmentName();
    }
}