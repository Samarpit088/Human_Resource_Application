package com.example.Human_Resource_Managment.ApiTesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDepartments() throws Exception {

        mockMvc.perform(
                        get("/api/v1/departments")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addDepartment() throws Exception {

        String body = """
            {
              "departmentId": 999,
              "departmentName": "AI",

              "manager":
                "http://localhost:8080/employees/103",

              "location":
                "http://localhost:8080/locations/1400"
            }
            """;

        mockMvc.perform(

                        post("/api/v1/departments")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void updateDepartment() throws Exception {

        String body = """
        {
          "departmentName": "Updated Department",

          "manager":
            "http://localhost:8080/employees/103",

          "location":
            "http://localhost:8080/locations/1400"
        }
        """;

        mockMvc.perform(

                        patch("/api/v1/departments/60")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(

                        get("/api/v1/departments/60")
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.departmentName")
                                .value("Updated Department")
                )

                .andExpect(
                        jsonPath("$._links.manager.href")
                                .exists()
                )

                .andExpect(
                        jsonPath("$._links.location.href")
                                .exists()
                );
    }
}