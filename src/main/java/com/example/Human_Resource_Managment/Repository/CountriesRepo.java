package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Countries;
import com.example.Human_Resource_Managment.Entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface CountriesRepo extends JpaRepository<Countries,Long> {
    Page<Countries> findAll(Pageable pageable);
}
