package com.example.Human_Resource_Managment.Projection;

import com.example.Human_Resource_Managment.Entity.Region;
import org.springframework.data.rest.core.config.Projection;

@Projection(
        name = "regionView",
        types = Region.class
)
public interface RegionProjection {

    Long getRegionId();

    String getRegionName();
}
