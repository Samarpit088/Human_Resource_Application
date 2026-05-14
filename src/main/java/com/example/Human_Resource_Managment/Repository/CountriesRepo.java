package com.example.Human_Resource_Managment.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Human_Resource_Managment.Entity.Countries;

@Repository
public interface CountriesRepo extends JpaRepository<Countries, String> {

//    Optional<Countries> findByCountryName(String countryName);
//
//    boolean existsByCountryName(String countryName);
//
//    void deleteByCountryId(String countryId);

    Page<Countries> findAll(Pageable pageable);

//    Page<Countries> findByRegionRegionId(Integer regionId, Pageable pageable);
//
//    Page<Countries> findByCountryNameContainingIgnoreCase(String countryName, Pageable pageable);
}