package com.example.Human_Resource_Managment.Repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Human_Resource_Managment.Entity.Countries;

@SpringBootTest
public class CountriesRepoTest {

    @Autowired
    private CountriesRepo countriesRepository;

//    @Test
//    void testFindById() {
//
//        Optional<Countries> country =
//                countriesRepository.findById("IN");
//
//        assertTrue(country.isPresent());
//    }
//
//    @Test
//    void testFindByCountryName() {
//
//        Optional<Countries> country =
//                countriesRepository.findByCountryName("India");
//
//        assertTrue(country.isPresent());
//    }
//
//    @Test
//    void testExistsByCountryName() {
//
//        boolean exists =
//                countriesRepository.existsByCountryName("India");
//
//        assertTrue(exists);
//    }

//    @Test
//    void testFindAll() {
//
//        assertFalse(countriesRepository.findAll().isEmpty());
//    }

//    @Test
//    void testSaveCountry() {
//
//        Countries country = new Countries();
//        country.setCountryId("TS");
//        country.setCountryName("Test Country");
//
//        Countries savedCountry =
//                countriesRepository.save(country);
//
//        assertNotNull(savedCountry);
//    }
}