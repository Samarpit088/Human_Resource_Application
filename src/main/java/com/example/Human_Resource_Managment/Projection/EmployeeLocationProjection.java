package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Employees;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee projection with department and location information
 * For Organization Structure Page
 */
@Projection(
        name = "employeeLocation",
        types = Employees.class
)
public interface EmployeeLocationProjection {

    Long getEmployeeId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhoneNumber();

    LocalDate getHireDate();

    BigDecimal getSalary();

    // Job information
    @Value("#{target.job?.jobId}")
    String getJobId();

    @Value("#{target.job?.jobTitle}")
    String getJobTitle();

    // Manager information
    @Value("#{target.manager?.employeeId}")
    Long getManagerId();

    @Value("#{target.manager?.firstName}")
    String getManagerFirstName();

    @Value("#{target.manager?.lastName}")
    String getManagerLastName();

    // Department information
    @Value("#{target.department?.departmentId}")
    Long getDepartmentId();

    @Value("#{target.department?.departmentName}")
    String getDepartmentName();

    // Location information through department
    @Value("#{target.department?.location?.locationId}")
    Long getLocationId();

    @Value("#{target.department?.location?.city}")
    String getLocationCity();

    @Value("#{target.department?.location?.stateProvince}")
    String getLocationStateProvince();

    // Country information
    @Value("#{target.department?.location?.country?.countryId}")
    String getCountryId();

    @Value("#{target.department?.location?.country?.countryName}")
    String getCountryName();

    // Region information
    @Value("#{target.department?.location?.country?.region?.regionId}")
    Long getRegionId();

    @Value("#{target.department?.location?.country?.region?.regionName}")
    String getRegionName();
}
