package com.example.Human_Resource_Managment.ApiTesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API tests for Locations endpoints
 * Tests Spring Data REST endpoints and custom query methods
 * 
 * Geographic Hierarchy: regions → countries → locations → departments → employees
 */
@SpringBootTest
@AutoConfigureMockMvc
class LocationsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllLocations() throws Exception {
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locationses").exists())
                .andExpect(jsonPath("$._embedded.locationses[0].city").exists());
    }

    @Test
    void testGetLocationById() throws Exception {
        mockMvc.perform(get("/api/v1/locations/1700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Seattle"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.country.href").exists());
    }

    @Test
    void testFindLocationsByRegionId() throws Exception {
        // Region ID 20 = Americas
        mockMvc.perform(get("/api/v1/locations/search/findByCountryRegionRegionId")
                        .param("regionId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locationses").exists())
                .andExpect(jsonPath("$._embedded.locationses", hasSize(greaterThan(0))));
    }

    @Test
    void testFindLocationsByCountryId() throws Exception {
        // Country ID "US" = United States
        mockMvc.perform(get("/api/v1/locations/search/findByCountryCountryId")
                        .param("countryId", "US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locationses").exists())
                .andExpect(jsonPath("$._embedded.locationses", hasSize(greaterThan(0))));
    }

    @Test
    void testFindLocationsByCity() throws Exception {
        mockMvc.perform(get("/api/v1/locations/search/findByCityContainingIgnoreCase")
                        .param("city", "Seattle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locationses").exists())
                .andExpect(jsonPath("$._embedded.locationses[0].city", containsStringIgnoringCase("Seattle")));
    }

    @Test
    void testFindLocationsByRegionAndCountry() throws Exception {
        mockMvc.perform(get("/api/v1/locations/search/findByCountryRegionRegionIdAndCountryCountryId")
                        .param("regionId", "20")
                        .param("countryId", "US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.locationses").exists());
    }

    @Test
    void testFindByLocationId() throws Exception {
        mockMvc.perform(get("/api/v1/locations/search/findByLocationId")
                        .param("locationId", "1700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Seattle"));
    }

    @Test
    void testLocationWithDepartments() throws Exception {
        // Location 1700 (Seattle) should have departments
        mockMvc.perform(get("/api/v1/departments/search/findByLocationLocationId")
                        .param("locationId", "1700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departments").exists())
                .andExpect(jsonPath("$._embedded.departments", hasSize(greaterThan(0))));
    }
}
