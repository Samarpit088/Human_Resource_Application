package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Locations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationsRepo extends JpaRepository<Locations, Integer> {

    Page<Locations> findAll(Pageable pageable);

}