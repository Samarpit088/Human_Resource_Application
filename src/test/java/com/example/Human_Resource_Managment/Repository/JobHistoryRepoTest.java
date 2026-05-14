package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Job;
import com.example.Human_Resource_Managment.Entity.JobHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JobHistoryRepoTest {

    @Autowired
    private JobHistoryRepo jobHistoryRepo;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        jobHistoryRepo.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testJH_006_ValidJobHistoryRecord_HistorySavedSuccessfully() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2013, 1, 15))
                .endDate(LocalDate.of(2015, 9, 20))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(101, saved.getEmployeeId());
        assertEquals(LocalDate.of(2013, 1, 15), saved.getStartDate());
        assertEquals(LocalDate.of(2015, 9, 20), saved.getEndDate());
        assertEquals("IT_PROG", saved.getJobId());
        assertEquals(60L, saved.getDepartmentId());
    }

    @Test
    void testJH_007_EmployeeJobChanged_HistoryCreated() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2013, 1, 15))
                .endDate(LocalDate.of(2015, 9, 20))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals("IT_PROG", saved.getJobId());
        assertEquals(101, saved.getEmployeeId());
        assertNotNull(saved.getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_008_EmployeeDepartmentChanged_HistoryCreated() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(102)
                .startDate(LocalDate.of(2014, 3, 10))
                .endDate(LocalDate.of(2016, 5, 15))
                .jobId("SA_REP")
                .departmentId(80L)
                .build();

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(80L, saved.getDepartmentId());
        assertEquals(102, saved.getEmployeeId());
        assertEquals("SA_REP", saved.getJobId());
        assertNotNull(saved.getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_009_EmployeePromoted_PreviousRoleStored() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(103)
                .startDate(LocalDate.of(2012, 6, 1))
                .endDate(LocalDate.of(2015, 12, 31))
                .jobId("ST_CLERK")
                .departmentId(50L)
                .build();

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals("ST_CLERK", saved.getJobId());
        assertEquals(103, saved.getEmployeeId());
        assertEquals(50L, saved.getDepartmentId());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_010_EmployeeTransferred_PreviousDepartmentStored() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(104)
                .startDate(LocalDate.of(2011, 8, 20))
                .endDate(LocalDate.of(2014, 7, 19))
                .jobId("HR_REP")
                .departmentId(40L)
                .build();

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(40L, saved.getDepartmentId());
        assertEquals(104, saved.getEmployeeId());
        assertEquals("HR_REP", saved.getJobId());
        assertNotNull(saved.getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_011_EmployeeHistoryExists_RecordsReturned() {
        JobHistory jobHistory = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2013, 1, 15))
                .endDate(LocalDate.of(2015, 9, 20))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(101);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getEmployeeId());
    }

    @Test
    void testJH_012_MultipleHistoryRecordsExist_AllRecordsReturned() {
        JobHistory jobHistory1 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2013, 1, 15))
                .endDate(LocalDate.of(2015, 9, 20))
                .jobId("AD_PRES")
                .departmentId(60L)
                .build();

        JobHistory jobHistory2 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2015, 9, 21))
                .endDate(LocalDate.of(2018, 12, 31))
                .jobId("AD_VP")
                .departmentId(60L)
                .build();

        JobHistory jobHistory3 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2019, 1, 1))
                .endDate(LocalDate.of(2021, 12, 31))
                .jobId("AD_ASST")
                .departmentId(90L)
                .build();

        jobHistoryRepo.save(jobHistory1);
        jobHistoryRepo.save(jobHistory2);
        jobHistoryRepo.save(jobHistory3);
        entityManager.flush();

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(101);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testJH_013_NoHistoryRecordsExist_EmptyListReturned() {
        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testJH_014_EmployeeWithPromotionHistory_AllRecordsReturned() {
        JobHistory promotion1 = JobHistory.builder()
                .employeeId(105)
                .startDate(LocalDate.of(2010, 1, 1))
                .endDate(LocalDate.of(2013, 12, 31))
                .jobId("AD_PRES")
                .departmentId(60L)
                .build();

        JobHistory promotion2 = JobHistory.builder()
                .employeeId(105)
                .startDate(LocalDate.of(2014, 1, 1))
                .endDate(LocalDate.of(2017, 12, 31))
                .jobId("AD_VP")
                .departmentId(60L)
                .build();

        jobHistoryRepo.save(promotion1);
        jobHistoryRepo.save(promotion2);
        entityManager.flush();

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(105);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(jh -> jh.getEmployeeId().equals(105)));
    }

    @Test
    void testJH_015_EmployeeWithDepartmentTransfers_AllRecordsReturned() {
        JobHistory transfer1 = JobHistory.builder()
                .employeeId(106)
                .startDate(LocalDate.of(2009, 5, 15))
                .endDate(LocalDate.of(2012, 4, 30))
                .jobId("FI_ACCOUNT")
                .departmentId(100L)
                .build();

        JobHistory transfer2 = JobHistory.builder()
                .employeeId(106)
                .startDate(LocalDate.of(2012, 5, 1))
                .endDate(LocalDate.of(2015, 8, 31))
                .jobId("FI_ACCOUNT")
                .departmentId(110L)
                .build();

        jobHistoryRepo.save(transfer1);
        jobHistoryRepo.save(transfer2);
        entityManager.flush();

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(106);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(jh -> jh.getEmployeeId().equals(106)));
        assertTrue(result.stream().allMatch(jh -> jh.getDepartmentId() != null));
    }
}
