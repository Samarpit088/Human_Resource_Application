package com.example.Human_Resource_Managment.Repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Human_Resource_Managment.Entity.Region;

@SpringBootTest
class RegionRepoTest {

    @Autowired
    private RegionRepo regionRepository;

//    @Test
//    void testFindById() {
//
//        Optional<Region> region = regionRepository.findById(1);
//
//        assertTrue(region.isPresent());
//    }
//
//    @Test
//    void testFindByRegionName() {
//
//        Optional<Region> region =
//                regionRepository.findByRegionName("Europe");
//
//        assertTrue(region.isPresent());
//    }
//
//    @Test
//    void testExistsByRegionName() {
//
//        boolean exists =
//                regionRepository.existsByRegionName("Europe");
//
//        assertTrue(exists);
//    }

//    @Test
//    void testFindAll() {
//
//        assertFalse(regionRepository.findAll().isEmpty());
//    }g

//    @Test
//    void testSaveRegion() {
//
//        Region region = new Region();
//        region.setRegionId(100);
//        region.setRegionName("Test Region");
//
//        Region savedRegion = regionRepository.save(region);
//
//        assertNotNull(savedRegion);
//    }
}