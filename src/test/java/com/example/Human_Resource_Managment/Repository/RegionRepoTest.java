package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Region;
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
class RegionRepoTest {

    @Autowired
    private RegionRepo regionRepo;

    @Test
    void testFindAllRegions() {

        // Fetch first page with 5 records
        Page<Region> regions = regionRepo.findAll(PageRequest.of(0, 5));

        // Assertions
        assertNotNull(regions);

        // Print total regions count
        System.out.println("Total Regions = " + regions.getTotalElements());

        // Print fetched regions
        regions.forEach(region -> {
            System.out.println("Region ID: " + region.getRegionId());
            System.out.println("Region Name: " + region.getRegionName());
            System.out.println("------------------------");
        });

        // Optional check
        assertFalse(regions.isEmpty());
    }
    @Test
    void testFindRegionById() {

        Optional<Region> region =
                regionRepo.findById(10);

        assertTrue(region.isPresent());

        System.out.println("Region Found = "
                + region.get().getRegionName());
    }

    @Test
    void testExistsById() {

        boolean exists =
                regionRepo.existsById(10);

        assertTrue(exists);

        System.out.println("Region Exists");
    }
}