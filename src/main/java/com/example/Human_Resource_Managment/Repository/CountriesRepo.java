package com.example.Human_Resource_Managment.Repository;

import java.util.Optional;

import com.example.Human_Resource_Managment.Projection.CountryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.Human_Resource_Managment.Entity.Countries;

@RepositoryRestResource(
        path = "countries",
        excerptProjection = CountryProjection.class
)
public interface CountriesRepo extends JpaRepository<Countries, String> {


}