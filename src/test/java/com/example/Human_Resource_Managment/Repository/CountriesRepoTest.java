package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Countries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class CountriesRepoTest {

    @Autowired
    private CountriesRepo countriesRepo;

    @Test
    void testFindAllCountries() {

        // Fetch first page with 5 records
        Page<Countries> countries =
                countriesRepo.findAll(PageRequest.of(0, 5));

        // Assertions
        assertNotNull(countries);

        // Print total countries count
        System.out.println("Total Countries = " + countries.getTotalElements());

        // Print fetched countries
        countries.forEach(country -> {
            System.out.println("Country ID: " + country.getCountryId());
            System.out.println("Country Name: " + country.getCountryName());

            if (country.getRegion() != null) {
                System.out.println("Region Name: "
                        + country.getRegion().getRegionName());
            }

            System.out.println("------------------------");
        });

        // Optional check
        assertFalse(countries.isEmpty());
    }
    @Test
    void testFindCountryById() {

        Optional<Countries> country =
                countriesRepo.findById("IN");

        assertTrue(country.isPresent());

        System.out.println("Country Found = "
                + country.get().getCountryName());
    }

    @Test
    void testExistsById() {

        boolean exists =
                countriesRepo.existsById("IN");

        assertTrue(exists);

        System.out.println("Country Exists");
    }
}