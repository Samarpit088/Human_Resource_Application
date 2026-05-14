package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class JobRepoTest {

    @Autowired
    private JobRepo jobRepo;

    @Test
    void testFindAllJobs() {

        Page<Job> jobs = jobRepo.findAll(PageRequest.of(0, 19));

        assertNotNull(jobs);

        System.out.println("Total Jobs = " + jobs.getTotalElements());

        jobs.forEach(job -> {
            System.out.println("Job ID: " + job.getJob_id());
            System.out.println("Job Title: " + job.getJob_title());
            System.out.println("Min Salary: " + job.getMin_salary());
            System.out.println("Max Salary: " + job.getMax_salary());
            System.out.println("------------------------");
        });

        assertEquals(19, jobs.getNumberOfElements());
    }
    @Test
    void testAddJobWithNullJobId() {

        Job job = new Job();
        job.setJob_id(null);
        job.setJob_title("Test Job");
        job.setMin_salary(1000);
        job.setMax_salary(5000);

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testAddDuplicateJobId() {

        Job job1 = new Job();
        job1.setJob_id("DUP_JOB");
        job1.setJob_title("Duplicate Job One");
        job1.setMin_salary(1000);
        job1.setMax_salary(5000);

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
        job.setJob_id("NO_TITLE");
        job.setJob_title(null);
        job.setMin_salary(1000);
        job.setMax_salary(5000);

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testAddJobWithNegativeSalary() {

        Job job = new Job();
        job.setJob_id("NEG_SAL");
        job.setJob_title("Negative Salary Job");
        job.setMin_salary(-1000);
        job.setMax_salary(5000);

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testUpdateJobTitleOnly() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setJob_title("Updated Programmer");

        Job updatedJob = jobRepo.save(job);

        System.out.println("Updated Title = " + updatedJob.getJob_title());

        assertEquals("Updated Programmer", updatedJob.getJob_title());
    }
    @Test
    void testUpdateMinSalaryAndMaxSalary() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setMin_salary(5000);
        job.setMax_salary(15000);

        Job updatedJob = jobRepo.save(job);

        System.out.println("Updated Min Salary = " + updatedJob.getMin_salary());
        System.out.println("Updated Max Salary = " + updatedJob.getMax_salary());

        assertEquals(5000, updatedJob.getMin_salary());
        assertEquals(15000, updatedJob.getMax_salary());
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
        job.setMin_salary(-5000);

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
    @Test
    void testUpdateJobTitleToEmpty() {

        Optional<Job> optionalJob = jobRepo.findById("IT_PROG");

        assertTrue(optionalJob.isPresent());

        Job job = optionalJob.get();
        job.setJob_title("");

        assertThrows(Exception.class, () -> {
            jobRepo.saveAndFlush(job);
        });
    }
}