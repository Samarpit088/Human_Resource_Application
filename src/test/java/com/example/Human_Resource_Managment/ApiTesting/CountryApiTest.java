package com.example.Human_Resource_Managment.ApiTesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CountryApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllCountries() throws Exception {
        mockMvc.perform(get("/api/v1/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.countrieses").exists())
                .andExpect(jsonPath("$._embedded.countrieses", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$._embedded.countrieses[0].countryId").exists())
                .andExpect(jsonPath("$._embedded.countrieses[0].countryName").exists());
    }

    @Test
    void testGetCountryById() throws Exception {
        mockMvc.perform(get("/api/v1/countries/IN?projection=countryView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("India"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testAddCountry() throws Exception {
        String countryJson = """
                {
                    "countryId": "T1",
                    "countryName": "Test Country",
                    "region": "http://localhost/api/v1/regions/10"
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(countryJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(get("/api/v1/countries/T1?projection=countryView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Test Country"));
    }

    @Test
    void testUpdateCountryUsingPut() throws Exception {
        String createJson = """
                {
                    "countryId": "T2",
                    "countryName": "Old Country",
                    "region": "http://localhost/api/v1/regions/10"
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        String updateJson = """
                {
                    "countryId": "T2",
                    "countryName": "Updated Country",
                    "region": "http://localhost/api/v1/regions/20"
                }
                """;

        mockMvc.perform(put("/api/v1/countries/T2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/countries/T2?projection=countryView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Updated Country"));
    }

    @Test
    void testUpdateCountryUsingPatch() throws Exception {
        String createJson = """
                {
                    "countryId": "T3",
                    "countryName": "Patch Old",
                    "region": "http://localhost/api/v1/regions/10"
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        String patchJson = """
                {
                    "countryName": "Patch Updated"
                }
                """;

        mockMvc.perform(patch("/api/v1/countries/T3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/countries/T3?projection=countryView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Patch Updated"));
    }

    @Test
    void testDeleteCountry() throws Exception {
        String createJson = """
                {
                    "countryId": "T4",
                    "countryName": "Delete Country",
                    "region": "http://localhost/api/v1/regions/10"
                }
                """;

        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/countries/T4"))
                .andExpect(status().isNoContent());
    }
}