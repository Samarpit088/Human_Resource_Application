package com.example.Human_Resource_Managment.Projection;



import com.example.Human_Resource_Managment.Entity.Department;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(
        name = "departmentDetails",
        types = Department.class
)
public interface DepartmentDetailsProjection {

    Long getDepartmentId();

    String getDepartmentName();

    ManagerProjection getManager();

    List<EmployeeDetailsProjection> getEmployees();
}
