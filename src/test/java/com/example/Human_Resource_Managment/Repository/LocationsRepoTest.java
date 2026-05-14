package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Locations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LocationsRepoTest {

    @Autowired
    private LocationsRepo locationsRepo;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    void testListAllLocations_ValidPageAndSize_ReturnsPaginatedLocationList() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Locations> result = locationsRepo.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
        assertEquals(5, result.getSize());
        assertTrue(result.getContent().size() <= 5);
    }

    @Test
    void testListAllLocations_FirstPageRetrieval_CorrectRecordsReturned() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Locations> result = locationsRepo.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getTotalElements() > 0);
        assertEquals(0, result.getNumber());
        assertTrue(result.isFirst());
    }

    @Test
    void testListAllLocations_LastPageRetrieval_CorrectLastPageRecords() {
        long totalLocations = locationsRepo.count();
        int pageSize = 5;
        int lastPageNumber = (int) ((totalLocations - 1) / pageSize);

        Pageable pageable = PageRequest.of(lastPageNumber, pageSize);
        Page<Locations> result = locationsRepo.findAll(pageable);

        assertNotNull(result);
        assertEquals(totalLocations, result.getTotalElements());
        assertEquals(lastPageNumber, result.getNumber());
        assertTrue(result.isLast());
    }

    @Test
    void testSearchLocationById_ValidLocationId_ReturnLocationDetails() {
        Optional<Locations> result = locationsRepo.findById(1000);

        assertTrue(result.isPresent());
        assertEquals(1000, result.get().getLocationId());
        assertNotNull(result.get().getCity());
    }

    @Test
    void testSearchLocationById_MaximumIdValue_SuccessfulRetrieval() {
        Optional<Locations> maxLocation = locationsRepo.findAll()
                .stream()
                .max((l1, l2) -> Integer.compare(l1.getLocationId(), l2.getLocationId()));

        assertTrue(maxLocation.isPresent());

        Integer maxId = maxLocation.get().getLocationId();
        Optional<Locations> result = locationsRepo.findById(maxId);

        assertTrue(result.isPresent());
        assertEquals(maxId, result.get().getLocationId());
    }

    @Test
    void testAddNewLocation_ValidLocationData_LocationSavedSuccessfully() {
        int newId = 9000;

        Locations location = Locations.builder()
                .LocationId(newId)
                .streetAddress("123 Test St")
                .postalCode("12345")
                .city("TestCity")
                .stateProvince("TC")
                .countryId("US")
                .build();

        Locations saved = locationsRepo.save(location);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(newId, saved.getLocationId());
        assertEquals("TestCity", saved.getCity());
    }

    @Test
    void testAddNewLocation_ValidCountryMapping_LocationPersistedCorrectly() {
        int newId = 9001;

        Locations location = Locations.builder()
                .LocationId(newId)
                .streetAddress("456 Country St")
                .postalCode("54321")
                .city("CountryCity")
                .stateProvince("CC")
                .countryId("US")
                .build();

        Locations saved = locationsRepo.save(location);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals("US", saved.getCountryId());
        assertEquals(newId, saved.getLocationId());
    }

    @Test
    void testAddNewLocation_DuplicateLocationId_SaveOperationFails() {
        int newId = 9002;

        Locations location1 = Locations.builder()
                .LocationId(newId)
                .streetAddress("123 Main St")
                .postalCode("12345")
                .city("FirstCity")
                .stateProvince("NY")
                .countryId("US")
                .build();

        entityManager.persist(location1);
        entityManager.flush();

        Locations location2 = Locations.builder()
                .LocationId(newId)
                .streetAddress("456 Different St")
                .postalCode("67890")
                .city("SecondCity")
                .stateProvince("MA")
                .countryId("US")
                .build();

        assertThrows(Exception.class, () -> {
            entityManager.persist(location2);
            entityManager.flush();
        });
    }

    @Test
    void testModifyExistingLocation_ValidUpdateRequest_LocationUpdatedSuccessfully() {
        int existingId = 1000;

        Locations existingLocation = locationsRepo.findById(existingId).orElseThrow();

        existingLocation.setCity("Updated City");
        existingLocation.setStreetAddress("999 Updated St");

        Locations updated = locationsRepo.save(existingLocation);
        entityManager.flush();

        assertEquals("Updated City", updated.getCity());
        assertEquals("999 Updated St", updated.getStreetAddress());
        assertEquals(existingId, updated.getLocationId());
    }

    @Test
    void testModifyExistingLocation_PartialFieldUpdate_SuccessfulUpdate() {
        int existingId = 1000;

        Locations existingLocation = locationsRepo.findById(existingId).orElseThrow();
        String originalCity = existingLocation.getCity();
        String originalStreet = existingLocation.getStreetAddress();

        existingLocation.setPostalCode("99999");

        Locations updated = locationsRepo.save(existingLocation);
        entityManager.flush();

        assertEquals("99999", updated.getPostalCode());
        assertEquals(originalCity, updated.getCity());
        assertEquals(originalStreet, updated.getStreetAddress());
    }

    @Test
    void testModifyExistingLocation_InvalidLocationId_UpdateOperationFails() {
        Optional<Locations> result = locationsRepo.findById(99999);

        assertFalse(result.isPresent());
    }

    @Test
    void testModifyExistingLocation_NullObject_ExceptionOccurs() {
        assertThrows(Exception.class, () -> {
            locationsRepo.save(null);
            entityManager.flush();
        });
    }

    @Test
    void testCheckLocationExists_ExistingLocationId_ReturnTrue() {
        int existingId = 1000;

        boolean exists = locationsRepo.existsById(existingId);

        assertTrue(exists);
    }

    @Test
    void testCheckLocationExists_NonExistingLocationId_ReturnFalse() {
        boolean exists = locationsRepo.existsById(99999);

        assertFalse(exists);
    }

    @Test
    void testCheckLocationExists_NullId_ExceptionOccurs() {
        assertThrows(Exception.class, () -> {
            locationsRepo.existsById(null);
        });
    }
}