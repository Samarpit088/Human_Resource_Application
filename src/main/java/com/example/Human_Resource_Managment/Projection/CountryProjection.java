package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Countries;
import com.example.Human_Resource_Managment.Entity.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "countryView",
        types = Countries.class
)
public interface CountryProjection {

    String getCountryId();

    String getCountryName();
    
    @Value("#{target.region?.regionId}")
    Long getRegionId();
    
    @Value("#{target.region?.regionName}")
    String getRegionName();
}
