package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Department;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * Department projection with location and geographic information
 * For Organization Structure Page
 */
@Projection(
        name = "departmentLocation",
        types = Department.class
)
public interface DepartmentLocationProjection {

    Long getDepartmentId();

    String getDepartmentName();

    // Manager information
    @Value("#{target.manager?.employeeId}")
    Long getManagerId();

    @Value("#{target.manager?.firstName}")
    String getManagerFirstName();

    @Value("#{target.manager?.lastName}")
    String getManagerLastName();

    // Location information
    @Value("#{target.location?.locationId}")
    Long getLocationId();

    @Value("#{target.location?.city}")
    String getLocationCity();

    @Value("#{target.location?.stateProvince}")
    String getLocationStateProvince();

    @Value("#{target.location?.streetAddress}")
    String getLocationStreetAddress();

    // Country information through location
    @Value("#{target.location?.country?.countryId}")
    String getCountryId();

    @Value("#{target.location?.country?.countryName}")
    String getCountryName();

    // Region information through location and country
    @Value("#{target.location?.country?.region?.regionId}")
    Long getRegionId();

    @Value("#{target.location?.country?.region?.regionName}")
    String getRegionName();
}
