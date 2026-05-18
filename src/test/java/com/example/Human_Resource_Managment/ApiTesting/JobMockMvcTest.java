package com.example.Human_Resource_Managment.ApiTesting;

import com.example.Human_Resource_Managment.Entity.Job;
import com.example.Human_Resource_Managment.Repository.JobRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JobMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepo jobRepo;

    @BeforeEach
    void setup() {

        Job job = new Job();
        job.setJobId("IT_PROG");
        job.setJobTitle("Programmer");
        job.setMinSalary(BigDecimal.valueOf(4000));
        job.setMaxSalary(BigDecimal.valueOf(10000));

        jobRepo.save(job);
    }

    @Test
    void getAllJobs_ShouldContainItProg() throws Exception {

        mockMvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.jobs[?(@.jobId == 'IT_PROG')]").exists());
    }
    @Test
    void updateJob_ShouldUpdateExistingJob() throws Exception {

        String updatedJson = """
        {
          "jobTitle": "Updated Programmer",
          "minSalary": 7000,
          "maxSalary": 15000
        }
        """;

        mockMvc.perform(
                        put("/api/v1/jobs/IT_PROG")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedJson)
                )
                .andExpect(status().isNoContent());
    }
    @Test
    void addJob_ShouldCreateNewJob() throws Exception {

        String newJobJson = """
        {
          "jobId": "TEST_JOB",
          "jobTitle": "Test Engineer",
          "minSalary": 5000,
          "maxSalary": 12000
        }
        """;

        mockMvc.perform(
                        post("/api/v1/jobs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newJobJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
    @Test
    void findEmployeesByJobId_ShouldReturnEmployees() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees/search/findByJobJobId")
                                .param("jobId", "IT_PROG")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeses").exists());
    }
    @Test
    void findEmployeesByJobId_WithProjection_ShouldReturnProjectedEmployees() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees/search/findByJobJobId")
                                .param("jobId", "IT_PROG")
                                .param("projection", "employeeDetailedView")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeses").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].employeeId").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].firstName").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].lastName").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].email").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].salary").exists());
    }
    @Test
    void findEmployeesByJobId_WithProjectionAndPaging_ShouldReturnProjectedEmployees() throws Exception {

        mockMvc.perform(
                        get("/api/v1/employees/search/findByJobJobId")
                                .param("jobId", "IT_PROG")
                                .param("projection", "employeeDetailedView")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())

                // projection data
                .andExpect(jsonPath("$._embedded.employeeses").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].employeeId").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].firstName").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].lastName").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].email").exists())
                .andExpect(jsonPath("$._embedded.employeeses[0].salary").exists())

                // paging data
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").exists())
                .andExpect(jsonPath("$.page.totalPages").exists())

                // pagination links
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists());
    }
    @Test
    void getAllJobs_WithProjectionAndPaging_ShouldReturnPagedJobs() throws Exception {

        mockMvc.perform(
                        get("/api/v1/jobs")
                                .param("projection", "jobList")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.jobs").exists())
                .andExpect(jsonPath("$._embedded.jobs.length()").value(2))
                .andExpect(jsonPath("$._embedded.jobs[0].jobId").exists())
                .andExpect(jsonPath("$._embedded.jobs[0].jobTitle").exists())
                .andExpect(jsonPath("$._embedded.jobs[0].minSalary").exists())
                .andExpect(jsonPath("$._embedded.jobs[0].maxSalary").exists())
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").exists())
                .andExpect(jsonPath("$.page.totalPages").exists())

                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").exists())
                .andExpect(jsonPath("$.page.totalPages").exists())

                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.first.href").exists())
                .andExpect(jsonPath("$._links.last.href").exists())

                .andExpect(jsonPath("$._embedded.jobs.length()").value(2));;
    }
}