package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Locations;
import org.springframework.data.rest.core.config.Projection;

/**
 * Basic location projection for list views
 */
@Projection(
        name = "locationView",
        types = Locations.class
)
public interface LocationProjection {

    Long getLocationId();

    String getCity();

    String getStateProvince();

    String getStreetAddress();

    String getPostalCode();
    //7799
}
