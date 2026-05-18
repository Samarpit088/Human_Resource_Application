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
class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================
    // GET ALL EMPLOYEES
    // =========================================================

    @Test
    @Order(1)
    void testGetAllEmployees() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees")
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.employeeses")
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$._embedded.employeeses",
                                hasSize(greaterThan(0))
                        )
                );
    }

    // =========================================================
    // GET EMPLOYEE BY ID
    // =========================================================

    @Test
    @Order(2)
    void testGetEmployeeById() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees/100")
                                .param(
                                        "projection",
                                        "employeeView"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.employeeId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .exists()
                );
    }

    // =========================================================
    // EMPLOYEE SUMMARY PROJECTION
    // =========================================================

    @Test
    @Order(3)
    void testEmployeeSummaryProjection() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees")
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$._embedded.employeeses[0].employeeId"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$._embedded.employeeses[0].firstName"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$._embedded.employeeses[0].job.jobTitle"
                        ).exists()
                )
                .andExpect(
                        jsonPath(
                                "$._embedded.employeeses[0].department.departmentName"
                        ).exists()
                );
    }

    // =========================================================
    // EMPLOYEE VIEW PROJECTION
    // =========================================================

    @Test
    @Order(4)
    void testEmployeeViewProjection() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees/100")
                                .param(
                                        "projection",
                                        "employeeView"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.employeeId")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.job.jobTitle")
                                .exists()
                );
    }

    // =========================================================
    // CREATE EMPLOYEE
    // =========================================================

    @Test
    @Order(5)
    void testCreateEmployee() throws Exception {

        String employeeJson = """
                {
                    "employeeId": 999,
                    "firstName": "Navya",
                    "lastName": "Aggarwal",
                    "email": "NAVYA999",
                    "phoneNumber": "9999999999",
                    "hireDate": "2025-08-17",
                    "salary": 5000,

                    "job":
                    "http://localhost/api/v1/jobs/IT_PROG",

                    "department":
                    "http://localhost/api/v1/departments/60",

                    "manager":
                    "http://localhost/api/v1/employees/103"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/employees")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(employeeJson)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().exists("Location")
                );

        mockMvc.perform(
                        get("/api/v1/employees/999")
                                .param(
                                        "projection",
                                        "employeeView"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Navya")
                );
    }

    // =========================================================
    // UPDATE EMPLOYEE USING PUT
    // =========================================================

    @Test
    @Order(6)
    void testUpdateEmployeeUsingPut() throws Exception {

        String updateJson = """
                {
                    "employeeId": 999,
                    "firstName": "Navya",
                    "lastName": "Updated",
                    "email": "NAVYA999",
                    "phoneNumber": "9999999999",
                    "hireDate": "2025-08-17",
                    "salary": 9000,

                    "job":
                    "http://localhost/api/v1/jobs/IT_PROG",

                    "department":
                    "http://localhost/api/v1/departments/60",

                    "manager":
                    "http://localhost/api/v1/employees/103"
                }
                """;

        mockMvc.perform(
                        put("/api/v1/employees/999")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(updateJson)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/v1/employees/999")
                                .param(
                                        "projection",
                                        "employeeView"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Updated")
                );
    }

    // =========================================================
    // UPDATE EMPLOYEE USING PATCH
    // =========================================================

    @Test
    @Order(7)
    void testUpdateEmployeeUsingPatch() throws Exception {

        String patchJson = """
                {
                    "salary": 12000
                }
                """;

        mockMvc.perform(
                        patch("/api/v1/employees/999")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(patchJson)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/v1/employees/999")
                                .param(
                                        "projection",
                                        "employeeView"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.salary")
                                .value(12000)
                );
    }

    // =========================================================
    // SEARCH BY EMAIL
    // =========================================================

    @Test
    @Order(8)
    void testFindByEmail() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/search/findByEmail"
                        )
                                .param(
                                        "email",
                                        "SKING"
                                )
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.employeeId")
                                .exists()
                );
    }

    // =========================================================
    // SEARCH BY DEPARTMENT
    // =========================================================

    @Test
    @Order(9)
    void testFindByDepartmentDepartmentId() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/search/findByDepartmentDepartmentId"
                        )
                                .param(
                                        "departmentId",
                                        "60"
                                )
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.employeeses")
                                .exists()
                );
    }

    // =========================================================
    // SEARCH BY JOB ID
    // =========================================================

    @Test
    @Order(10)
    void testFindByJobJobId() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/search/findByJobJobId"
                        )
                                .param(
                                        "jobId",
                                        "IT_PROG"
                                )
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.employeeses")
                                .exists()
                );
    }

    // =========================================================
    // SEARCH BY SALARY RANGE
    // =========================================================

    @Test
    @Order(11)
    void testFindBySalaryBetween() throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/search/findBySalaryBetween"
                        )
                                .param(
                                        "minSalary",
                                        "4000"
                                )
                                .param(
                                        "maxSalary",
                                        "10000"
                                )
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.employeeses")
                                .exists()
                );
    }

    // =========================================================
    // SEARCH BY FIRST NAME
    // =========================================================

    @Test
    @Order(12)
    void testFindByFirstNameContainingIgnoreCase()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/employees/search/findByFirstNameContainingIgnoreCase"
                        )
                                .param(
                                        "firstName",
                                        "ste"
                                )
                                .param(
                                        "projection",
                                        "employeeSummary"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$._embedded.employeeses")
                                .exists()
                );
    }

    // =========================================================
    // DELETE EMPLOYEE
    // =========================================================

    @Test
    @Order(13)
    void testDeleteEmployee() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/employees/999")
                )
                .andExpect(status().isNoContent());
    }
}