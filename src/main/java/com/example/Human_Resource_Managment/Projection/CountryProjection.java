package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Countries;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "countryView",
        types = Countries.class
)
public interface CountryProjection {

    String getCountryId();

    String getCountryName();
}
