package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Locations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * Detailed location projection with country and region information
 * For Geographic Hierarchy Page
 */
@Projection(
        name = "locationDetails",
        types = Locations.class
)
public interface LocationDetailsProjection {

    Long getLocationId();

    String getStreetAddress();

    String getPostalCode();

    String getCity();

    String getStateProvince();

    // Country information
    @Value("#{target.country.countryId}")
    String getCountryId();

    @Value("#{target.country.countryName}")
    String getCountryName();

    // Region information through country
    @Value("#{target.country.region.regionId}")
    Long getRegionId();

    @Value("#{target.country.region.regionName}")
    String getRegionName();
}
