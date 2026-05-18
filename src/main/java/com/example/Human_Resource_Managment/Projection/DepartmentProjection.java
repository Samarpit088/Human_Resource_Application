package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Department;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "departmentView",
        types = Department.class
)
public interface DepartmentProjection {

    Long getDepartmentId();

    String getDepartmentName();

    @Value("#{target.location.locationId}")
    Long getLocationId();


}