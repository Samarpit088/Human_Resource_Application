package com.example.Human_Resource_Managment.ApiTesting;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartmentApiTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================
    // GET ALL DEPARTMENTS
    // =========================================================

    @Test
    @Order(1)
    void testGetAllDepartments() throws Exception {
        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departmentses").exists())
                .andExpect(jsonPath("$._embedded.departmentses", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.page.totalElements").exists());
    }

    // =========================================================
    // GET DEPARTMENT BY ID
    // =========================================================

    @Test
    @Order(2)
    void testGetDepartmentById() throws Exception {
        mockMvc.perform(get("/api/v1/departments/60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(60))
                .andExpect(jsonPath("$.departmentName").exists());
    }

    // =========================================================
    // SEARCH DEPARTMENTS
    // =========================================================

    @Test
    @Order(3)
    void testSearchDepartments() throws Exception {
        mockMvc.perform(get("/api/v1/departments/search")
                        .param("query", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departments").exists());
    }

    @Test
    @Order(4)
    void testSearchDepartmentsByLocation() throws Exception {
        mockMvc.perform(get("/api/v1/departments/search")
                        .param("locationId", "1700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departments").exists());
    }

    // =========================================================
    // CREATE DEPARTMENT - Skip due to serialization issues
    // =========================================================

    @Test
    @Order(5)
    void testCreateDepartment() throws Exception {
        String departmentJson = """
                {
                    "departmentId": 999,
                    "departmentName": "Test Department",
                    "location": { "locationId": 1700 },
                    "manager": { "employeeId": 100 }
                }
                """;

        // Note: This may fail due to Hibernate lazy loading serialization issues
        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentJson))
                .andExpect(status().isOk());
    }

    // =========================================================
    // UPDATE DEPARTMENT - Skip due to serialization issues
    // =========================================================

    @Test
    @Order(6)
    void testUpdateDepartment() throws Exception {
        String updateJson = """
                {
                    "departmentName": "Updated Department"
                }
                """;

        // Note: This may fail due to Hibernate lazy loading serialization issues
        mockMvc.perform(patch("/api/v1/departments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    // =========================================================
    // CHANGE DEPARTMENT MANAGER - Skip due to serialization issues
    // =========================================================

    @Test
    @Order(7)
    void testChangeDepartmentManager() throws Exception {
        String updateJson = """
                {
                    "managerId": 103
                }
                """;

        // Note: This may fail due to Hibernate lazy loading serialization issues
        mockMvc.perform(patch("/api/v1/departments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    // =========================================================
    // DELETE DEPARTMENT - Not implemented in controller
    // =========================================================

    // @Test
    // @Order(8)
    // void testDeleteDepartment() throws Exception {
    //     mockMvc.perform(delete("/api/v1/departments/999"))
    //             .andExpect(status().isNoContent());
    // }

    // =========================================================
    // ERROR HANDLING
    // =========================================================

    @Test
    @Order(9)
    void testGetNonExistentDepartment() throws Exception {
        mockMvc.perform(get("/api/v1/departments/99999"))
                .andExpect(status().isNotFound());
    }
}
