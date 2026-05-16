package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Region;
import com.example.Human_Resource_Managment.Projection.RegionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
        path = "regions",
        excerptProjection = RegionProjection.class
)
public interface RegionRepo extends JpaRepository<Region, Long> {
}