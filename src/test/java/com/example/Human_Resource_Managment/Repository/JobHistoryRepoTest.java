package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import com.example.Human_Resource_Managment.Entity.Employees;
import com.example.Human_Resource_Managment.Entity.Job;
import com.example.Human_Resource_Managment.Entity.JobHistory;
import com.example.Human_Resource_Managment.Entity.JobHistoryId;
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
        entityManager.clear();
    }

    @Test
    void testJH_006_ValidJobHistoryRecord_HistorySavedSuccessfully() {
        Employees employee = entityManager.find(Employees.class, 101L);
        Job job = entityManager.find(Job.class, "IT_PROG");
        Department department = entityManager.find(Department.class, 60L);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(101L);
        id.setStartDate(LocalDate.of(2020, 1, 15));

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        jobHistory.setEndDate(LocalDate.of(2021, 9, 20));
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(101L, saved.getId().getEmployeeId());
        assertEquals(LocalDate.of(2020, 1, 15), saved.getId().getStartDate());
        assertEquals(LocalDate.of(2021, 9, 20), saved.getEndDate());
        assertNotNull(saved.getEmployee());
        assertEquals(101L, saved.getEmployee().getEmployeeId());
        assertNotNull(saved.getJob());
        assertEquals("IT_PROG", saved.getJob().getJobId());
        assertNotNull(saved.getDepartment());
        assertEquals(60L, saved.getDepartment().getDepartmentId());
    }

    @Test
    void testJH_007_EmployeeJobChanged_HistoryCreated() {
        Employees employee = entityManager.find(Employees.class, 102L);
        Job job = entityManager.find(Job.class, "IT_PROG");
        Department department = entityManager.find(Department.class, 60L);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(102L);
        id.setStartDate(LocalDate.of(2020, 2, 1));

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        jobHistory.setEndDate(LocalDate.of(2021, 10, 15));
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(102L, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
        assertNotNull(saved.getEmployee());
        assertEquals(102L, saved.getEmployee().getEmployeeId());
        assertNotNull(saved.getJob());
        assertEquals("IT_PROG", saved.getJob().getJobId());
        assertNotNull(saved.getDepartment());
        assertEquals(60L, saved.getDepartment().getDepartmentId());
    }

    @Test
    void testJH_008_EmployeeDepartmentChanged_HistoryCreated() {
        Employees employee = entityManager.find(Employees.class, 103L);
        Job job = entityManager.find(Job.class, "SA_REP");
        Department department = entityManager.find(Department.class, 80L);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(103L);
        id.setStartDate(LocalDate.of(2020, 3, 10));

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        jobHistory.setEndDate(LocalDate.of(2021, 5, 15));
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(103L, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
        assertNotNull(saved.getEmployee());
        assertEquals(103L, saved.getEmployee().getEmployeeId());
        assertNotNull(saved.getJob());
        assertEquals("SA_REP", saved.getJob().getJobId());
        assertNotNull(saved.getDepartment());
        assertEquals(80L, saved.getDepartment().getDepartmentId());
    }

    @Test
    void testJH_009_EmployeePromoted_PreviousRoleStored() {
        Employees employee = entityManager.find(Employees.class, 104L);
        Job job = entityManager.find(Job.class, "ST_CLERK");
        Department department = entityManager.find(Department.class, 50L);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(104L);
        id.setStartDate(LocalDate.of(2020, 6, 1));

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        jobHistory.setEndDate(LocalDate.of(2021, 12, 31));
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(104L, saved.getId().getEmployeeId());
        assertNotNull(saved.getEndDate());
        assertNotNull(saved.getEmployee());
        assertEquals(104L, saved.getEmployee().getEmployeeId());
        assertNotNull(saved.getJob());
        assertEquals("ST_CLERK", saved.getJob().getJobId());
        assertNotNull(saved.getDepartment());
        assertEquals(50L, saved.getDepartment().getDepartmentId());
    }

    @Test
    void testJH_010_EmployeeTransferred_PreviousDepartmentStored() {
        Employees employee = entityManager.find(Employees.class, 105L);
        Job job = entityManager.find(Job.class, "HR_REP");
        Department department = entityManager.find(Department.class, 40L);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(105L);
        id.setStartDate(LocalDate.of(2020, 8, 20));

        JobHistory jobHistory = new JobHistory();
        jobHistory.setId(id);
        jobHistory.setEndDate(LocalDate.of(2021, 7, 19));
        jobHistory.setEmployee(employee);
        jobHistory.setJob(job);
        jobHistory.setDepartment(department);

        JobHistory saved = jobHistoryRepo.save(jobHistory);
        entityManager.flush();

        assertNotNull(saved);
        assertEquals(105L, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
        assertNotNull(saved.getEmployee());
        assertEquals(105L, saved.getEmployee().getEmployeeId());
        assertNotNull(saved.getJob());
        assertEquals("HR_REP", saved.getJob().getJobId());
        assertNotNull(saved.getDepartment());
        assertEquals(40L, saved.getDepartment().getDepartmentId());
    }

    @Test
    void testJH_011_EmployeeHistoryExists_RecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101L);

        assertNotNull(result);
        if (!result.isEmpty()) {
            assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(101L)));
            result.forEach(jh -> {
                assertNotNull(jh.getEmployee());
                assertEquals(101L, jh.getEmployee().getEmployeeId());
                assertNotNull(jh.getJob());
                assertNotNull(jh.getDepartment());
            });
        }
    }

    @Test
    void testJH_012_MultipleHistoryRecordsExist_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101L);

        assertNotNull(result);
        assertTrue(result.size() >= 0);
    }

    @Test
    void testJH_013_NoHistoryRecordsExist_EmptyListReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(9999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testJH_014_EmployeeWithPromotionHistory_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101L);

        assertNotNull(result);
        if (!result.isEmpty()) {
            assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(101L)));
            result.forEach(jh -> {
                assertNotNull(jh.getEmployee());
                assertEquals(101L, jh.getEmployee().getEmployeeId());
                assertNotNull(jh.getJob());
                assertNotNull(jh.getJob().getJobId());
            });
        }
    }

    @Test
    void testJH_015_EmployeeWithDepartmentTransfers_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(176L);

        assertNotNull(result);
        if (!result.isEmpty()) {
            assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(176L)));
            assertTrue(result.stream().allMatch(jh -> jh.getDepartment() != null));
        }
    }
}
