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
class RegionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllRegions() throws Exception {
        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.regions").exists())
                .andExpect(jsonPath("$._embedded.regions", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$._embedded.regions[0].regionId").exists())
                .andExpect(jsonPath("$._embedded.regions[0].regionName").exists());
    }

    @Test
    void testGetRegionById() throws Exception {
        mockMvc.perform(get("/api/v1/regions/10?projection=regionView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("Europe"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testAddRegion() throws Exception {
        String regionJson = """
                {
                    "regionId": 101,
                    "regionName": "Antarctica"
                }
                """;

        mockMvc.perform(post("/api/v1/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regionJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        mockMvc.perform(get("/api/v1/regions/101?projection=regionView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("Antarctica"));
    }

    @Test
    void testUpdateRegionUsingPut() throws Exception {
        String createJson = """
                {
                    "regionId": 102,
                    "regionName": "Old Region"
                }
                """;

        mockMvc.perform(post("/api/v1/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        String updateJson = """
                {
                    "regionId": 102,
                    "regionName": "Antarctica Updated"
                }
                """;

        mockMvc.perform(put("/api/v1/regions/102")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/regions/102?projection=regionView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("Antarctica Updated"));
    }

    @Test
    void testUpdateRegionUsingPatch() throws Exception {
        String createJson = """
                {
                    "regionId": 103,
                    "regionName": "Patch Old"
                }
                """;

        mockMvc.perform(post("/api/v1/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        String patchJson = """
                {
                    "regionName": "Antarctica Final"
                }
                """;

        mockMvc.perform(patch("/api/v1/regions/103")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/regions/103?projection=regionView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regionName").value("Antarctica Final"));
    }

    @Test
    void testDeleteRegion() throws Exception {
        String createJson = """
                {
                    "regionId": 104,
                    "regionName": "Delete Region"
                }
                """;

        mockMvc.perform(post("/api/v1/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/regions/104"))
                .andExpect(status().isNoContent());
    }
}