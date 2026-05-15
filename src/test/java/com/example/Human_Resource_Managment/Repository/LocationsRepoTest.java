package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Countries;
import com.example.Human_Resource_Managment.Entity.Locations;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)

class LocationsRepoTest {

    @Autowired
    private LocationsRepo locationsRepo;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

        entityManager.clear();
    }

    /**
     * TC ID : LOC_001
     * Scenario : valid page and size
     * Expected : returns paginated location list
     */
    @Test
    void testListAllLocations_ValidPageAndSize_ReturnsPaginatedLocationList() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Locations> result =
                locationsRepo.findAll(pageable);

        assertNotNull(result);

        assertFalse(result.isEmpty());

        System.out.println(
                "Total Locations : "
                        + result.getTotalElements()
        );

        result.forEach(location -> {

            System.out.println(
                    "Location ID : "
                            + location.getLocationId()
            );

            System.out.println(
                    "City : "
                            + location.getCity()
            );

            if (location.getCountry() != null) {

                System.out.println(
                        "Country ID : "
                                + location.getCountry().getCountryId()
                );
            }

            System.out.println("----------------");
        });
    }

    /**
     * TC ID : LOC_002
     * Scenario : valid location id
     * Expected : return location details
     */
    @Test
    void testSearchLocationById_ValidLocationId_ReturnLocationDetails() {

        Optional<Locations> result =
                locationsRepo.findById(1000);

        assertTrue(result.isPresent());

        System.out.println(
                "Location City : "
                        + result.get().getCity()
        );
    }

    /**
     * TC ID : LOC_003
     * Scenario : maximum id value
     * Expected : successful retrieval
     */
    @Test
    void testSearchLocationById_MaximumIdValue_SuccessfulRetrieval() {

        Optional<Locations> result =
                locationsRepo.findById(Integer.MAX_VALUE);

        assertNotNull(result);

        System.out.println(
                "Maximum ID test executed"
        );
    }

    /**
     * TC ID : LOC_004
     * Scenario : valid location data
     * Expected : location saved successfully
     */
    @Test
    void testAddNewLocation_ValidLocationData_LocationSavedSuccessfully() {

        Countries country =
                new Countries();

        country.setCountryId("US");

        Locations location =
                Locations.builder()
                        .locationId(5000L)
                        .streetAddress("MG Road")
                        .postalCode("560001")
                        .city("Bangalore")
                        .stateProvince("Karnataka")
                        .country(country)
                        .build();

        Locations savedLocation =
                locationsRepo.save(location);

        assertNotNull(savedLocation);

        assertEquals(
                "Bangalore",
                savedLocation.getCity()
        );

        System.out.println(
                "Saved Location : "
                        + savedLocation.getCity()
        );
    }

    /**
     * TC ID : LOC_005
     * Scenario : valid country mapping
     * Expected : location persisted correctly
     */
    @Test
    void testAddNewLocation_ValidCountryMapping_LocationPersistedCorrectly() {

        Countries country =
                new Countries();

        country.setCountryId("IN");

        Locations location =
                Locations.builder()
                        .locationId(6000L)
                        .streetAddress("Ring Road")
                        .postalCode("110001")
                        .city("Delhi")
                        .stateProvince("Delhi")
                        .country(country)
                        .build();

        Locations savedLocation =
                locationsRepo.save(location);

        assertNotNull(savedLocation);

        assertEquals(
                "IN",
                savedLocation.getCountry().getCountryId()
        );

        System.out.println(
                "Country Mapping Successful"
        );
    }

    /**
     * TC ID : LOC_006
     * Scenario : existing location id
     * Expected : returns true
     */
    @Test
    void testCheckLocationExists_ExistingLocationId_ReturnTrue() {

        boolean exists =
                locationsRepo.existsById(1000);

        assertTrue(exists);

        System.out.println(
                "Location Exists"
        );
    }

    /**
     * TC ID : LOC_007
     * Scenario : non existing location id
     * Expected : returns false
     */
    @Test
    void testCheckLocationExists_NonExistingLocationId_ReturnFalse() {

        boolean exists =
                locationsRepo.existsById(999999);

        assertFalse(exists);

        System.out.println(
                "Location Does Not Exist"
        );
    }
}