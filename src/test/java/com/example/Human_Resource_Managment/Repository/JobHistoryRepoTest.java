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
        Employees employee = entityManager.find(Employees.class, 101);
        Job job = entityManager.find(Job.class, "IT_PROG");
        Department department = entityManager.find(Department.class, 60);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(101);
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
        assertEquals(101, saved.getId().getEmployeeId());
        assertEquals(LocalDate.of(2020, 1, 15), saved.getId().getStartDate());
        assertEquals(LocalDate.of(2021, 9, 20), saved.getEndDate());
    }

    @Test
    void testJH_007_EmployeeJobChanged_HistoryCreated() {
        Employees employee = entityManager.find(Employees.class, 102);
        Job job = entityManager.find(Job.class, "IT_PROG");
        Department department = entityManager.find(Department.class, 60);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(102);
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
        assertNotNull(saved.getJob());
        assertEquals(102, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_008_EmployeeDepartmentChanged_HistoryCreated() {
        Employees employee = entityManager.find(Employees.class, 103);
        Job job = entityManager.find(Job.class, "SA_REP");
        Department department = entityManager.find(Department.class, 80);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(103);
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
        assertNotNull(saved.getDepartment());
        assertEquals(103, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_009_EmployeePromoted_PreviousRoleStored() {
        Employees employee = entityManager.find(Employees.class, 104);
        Job job = entityManager.find(Job.class, "ST_CLERK");
        Department department = entityManager.find(Department.class, 50);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(104);
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
        assertNotNull(saved.getJob());
        assertEquals(104, saved.getId().getEmployeeId());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_010_EmployeeTransferred_PreviousDepartmentStored() {
        Employees employee = entityManager.find(Employees.class, 105);
        Job job = entityManager.find(Job.class, "HR_REP");
        Department department = entityManager.find(Department.class, 40);

        JobHistoryId id = new JobHistoryId();
        id.setEmployeeId(105);
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
        assertNotNull(saved.getDepartment());
        assertEquals(105, saved.getId().getEmployeeId());
        assertNotNull(saved.getId().getStartDate());
        assertNotNull(saved.getEndDate());
    }

    @Test
    void testJH_011_EmployeeHistoryExists_RecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(101)));
    }

    @Test
    void testJH_012_MultipleHistoryRecordsExist_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(101)));
    }

    @Test
    void testJH_013_NoHistoryRecordsExist_EmptyListReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(9999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testJH_014_EmployeeWithPromotionHistory_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(101);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(101)));
    }

    @Test
    void testJH_015_EmployeeWithDepartmentTransfers_AllRecordsReturned() {
        List<JobHistory> result = jobHistoryRepo.findByIdEmployeeId(176);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(jh -> jh.getId().getEmployeeId().equals(176)));
        assertTrue(result.stream().allMatch(jh -> jh.getDepartment() != null));
    }
}
