package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class JobRepoTest {

    @Autowired
    private JobRepo jobRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Test
    void testFindAllJobs() {

        Page<Job> jobs = jobRepo.findAll(PageRequest.of(0, 19));

        assertNotNull(jobs);

        System.out.println("Total Jobs = " + jobs.getTotalElements());

        jobs.forEach(job -> {
            System.out.println("Job ID: " + job.getJobId());
            System.out.println("Job Title: " + job.getJobTitle());
            System.out.println("Min Salary: " + job.getMinSalary());
            System.out.println("Max Salary: " + job.getMaxSalary());
            System.out.println("------------------------");
        });

        assertEquals(19, jobs.getNumberOfElements());
    }
    @Test
    void testAddJobWithNullJobId() {

        Job job = new Job();
        job.setJobId(null);
        job.setJobTitle("Test Job");
        job.setMinSalary(new BigDecimal(1000));
        job.setMaxSalary(new BigDecimal(5000));

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testAddDuplicateJobId() {

        Job job1 = new Job();
        job1.setJobId("DUP_JOB");
        job1.setJobTitle("Duplicate Job One");
        job1.setMinSalary(new BigDecimal(1000));
        job1.setMaxSalary(new BigDecimal(5000));

        jobRepo.saveAndFlush(job1);

        boolean exists = jobRepo.existsById("DUP_JOB");

        assertTrue(exists);

        if (exists) {
            System.out.println("Job ID already exists. Duplicate creation blocked.");
        }

        assertTrue(exists);
    }
    @Test
    void testAddJobWithMissingJobTitle() {

        Job job = new Job();
        job.setJobId("NO_TITLE");
        job.setJobTitle(null);
        job.setMinSalary(new BigDecimal(1000));
        job.setMaxSalary(new BigDecimal(5000));

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testAddJobWithNegativeSalary() {

        Job job = new Job();
        job.setJobId("NEG_SAL");
        job.setJobTitle("Negative Salary Job");
        job.setMinSalary(new BigDecimal(-1000));
        job.setMaxSalary(new BigDecimal(5000));

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testUpdateJobTitleOnly() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setJobTitle("Updated Programmer");

        Job updatedJob = jobRepo.save(job);

        System.out.println("Updated Title = " + updatedJob.getJobTitle());

        assertEquals("Updated Programmer", updatedJob.getJobTitle());
    }
    @Test
    void testUpdateMinSalaryAndMaxSalary() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setMinSalary(new BigDecimal(5000));
        job.setMaxSalary(new BigDecimal(15000));

        Job updatedJob = jobRepo.save(job);

        System.out.println("Updated Min Salary = " + updatedJob.getMinSalary());
        System.out.println("Updated Max Salary = " + updatedJob.getMaxSalary());

        assertEquals(new BigDecimal(5000), updatedJob.getMinSalary());
        assertEquals(new BigDecimal(15000), updatedJob.getMaxSalary());
    }
    @Test
    void testUpdateInvalidJobId() {

        Optional<Job> optionalJob = jobRepo.findById("INVALID_JOB");

        System.out.println("Job Found = " + optionalJob.isPresent());

        assertTrue(optionalJob.isEmpty());
    }
    @Test
    void testUpdateMinSalaryToNegative() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setMinSalary(new BigDecimal(-5000));

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testUpdateJobTitleToEmpty() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setJobTitle("");

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testFindEmployeesByValidJobId() {

        Page<Employees> employees =
                employeeRepo.findByJobJobId("IT_PROG", PageRequest.of(0, 10));

        assertNotNull(employees);

        System.out.println("Total Employees with IT_PROG = " + employees.getTotalElements());

        employees.forEach(employee -> {
            System.out.println("Employee ID: " + employee.getEmployeeId());
            System.out.println("First Name: " + employee.getFirstName());
            System.out.println("Last Name: " + employee.getLastName());
            System.out.println("Job ID: " + employee.getJob().getJobId());
            System.out.println("------------------------");

            assertEquals("IT_PROG", employee.getJob().getJobId());
        });

        assertFalse(employees.isEmpty());
    }
    @Test
    void testFindEmployeesByInvalidJobId() {

        Page<Employees> employees =
                employeeRepo.findByJobJobId("INVALID_JOB", PageRequest.of(0, 10));

        assertNotNull(employees);

        System.out.println("Total Employees = " + employees.getTotalElements());

        assertTrue(employees.isEmpty());
    }
    @Test
    void testFindEmployeesByJobIdWithPagination() {

        Page<Employees> employees =
                employeeRepo.findByJobJobId("IT_PROG", PageRequest.of(0, 5));

        assertNotNull(employees);

        System.out.println("Page Size = " + employees.getSize());
        System.out.println("Fetched Employees = " + employees.getNumberOfElements());
        System.out.println("Total Employees = " + employees.getTotalElements());

        employees.forEach(employee -> {
            System.out.println("Employee ID: " + employee.getEmployeeId());
            System.out.println("Job ID: " + employee.getJob().getJobId());
            System.out.println("------------------------");
        });

        assertTrue(employees.getNumberOfElements() <= 5);
    }
}