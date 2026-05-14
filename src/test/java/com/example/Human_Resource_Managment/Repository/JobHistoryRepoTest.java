package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobHistoryRepoTest {

    @Mock
    private JobHistoryRepo jobHistoryRepo;

    private JobHistory jobHistory1;
    private JobHistory jobHistory2;
    private JobHistory jobHistory3;
    private JobHistory.JobHistoryId jobHistoryId1;
    private JobHistory.JobHistoryId jobHistoryId2;

    @BeforeEach
    void setUp() {
        jobHistoryId1 = new JobHistory.JobHistoryId(101, LocalDate.of(2013, 1, 15));
        jobHistoryId2 = new JobHistory.JobHistoryId(101, LocalDate.of(2015, 9, 21));

        jobHistory1 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2013, 1, 15))
                .endDate(LocalDate.of(2015, 9, 20))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        jobHistory2 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2015, 9, 21))
                .endDate(null)
                .jobId("AD_VP")
                .departmentId(90L)
                .build();

        jobHistory3 = JobHistory.builder()
                .employeeId(200)
                .startDate(LocalDate.of(2012, 6, 7))
                .endDate(LocalDate.of(2013, 9, 16))
                .jobId("HR_REP")
                .departmentId(40L)
                .build();
    }

    @Test
    void testSaveJobHistory() {
        when(jobHistoryRepo.save(any(JobHistory.class))).thenReturn(jobHistory1);

        JobHistory savedJobHistory = jobHistoryRepo.save(jobHistory1);

        assertNotNull(savedJobHistory);
        assertEquals(101, savedJobHistory.getEmployeeId());
        assertEquals("IT_PROG", savedJobHistory.getJobId());
        assertEquals(60L, savedJobHistory.getDepartmentId());
        verify(jobHistoryRepo, times(1)).save(jobHistory1);
    }

    @Test
    void testFindById() {
        when(jobHistoryRepo.findById(jobHistoryId1)).thenReturn(Optional.of(jobHistory1));

        Optional<JobHistory> foundJobHistory = jobHistoryRepo.findById(jobHistoryId1);

        assertTrue(foundJobHistory.isPresent());
        assertEquals(101, foundJobHistory.get().getEmployeeId());
        assertEquals(LocalDate.of(2013, 1, 15), foundJobHistory.get().getStartDate());
        verify(jobHistoryRepo, times(1)).findById(jobHistoryId1);
    }

    @Test
    void testFindById_NotFound() {
        JobHistory.JobHistoryId nonExistentId = new JobHistory.JobHistoryId(999, LocalDate.of(2010, 1, 1));
        when(jobHistoryRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<JobHistory> foundJobHistory = jobHistoryRepo.findById(nonExistentId);

        assertFalse(foundJobHistory.isPresent());
        verify(jobHistoryRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testFindByEmployeeId() {
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2);
        when(jobHistoryRepo.findByEmployeeId(101)).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(101);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getEmployeeId());
        assertEquals(101, result.get(1).getEmployeeId());
        verify(jobHistoryRepo, times(1)).findByEmployeeId(101);
    }

    @Test
    void testFindByEmployeeIdWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobHistory> jobHistoryPage = new PageImpl<>(Arrays.asList(jobHistory1, jobHistory2), pageable, 2);
        when(jobHistoryRepo.findByEmployeeId(101, pageable)).thenReturn(jobHistoryPage);

        Page<JobHistory> result = jobHistoryRepo.findByEmployeeId(101, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(jobHistoryRepo, times(1)).findByEmployeeId(101, pageable);
    }

    @Test
    void testFindByJobId() {
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1);
        when(jobHistoryRepo.findByJobId("IT_PROG")).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findByJobId("IT_PROG");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IT_PROG", result.get(0).getJobId());
        verify(jobHistoryRepo, times(1)).findByJobId("IT_PROG");
    }

    @Test
    void testFindByDepartmentId() {
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2);
        when(jobHistoryRepo.findByDepartmentId(60L)).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findByDepartmentId(60L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(60L, result.get(0).getDepartmentId());
        verify(jobHistoryRepo, times(1)).findByDepartmentId(60L);
    }

    @Test
    void testFindByStartDateBetween() {
        LocalDate startDate = LocalDate.of(2012, 1, 1);
        LocalDate endDate = LocalDate.of(2013, 12, 31);
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory3);
        when(jobHistoryRepo.findByStartDateBetween(startDate, endDate)).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findByStartDateBetween(startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getStartDate().isAfter(startDate.minusDays(1)));
        assertTrue(result.get(0).getStartDate().isBefore(endDate.plusDays(1)));
        verify(jobHistoryRepo, times(1)).findByStartDateBetween(startDate, endDate);
    }

    @Test
    void testFindAllWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobHistory> jobHistoryPage = new PageImpl<>(
                Arrays.asList(jobHistory1, jobHistory2, jobHistory3), 
                pageable, 
                3
        );
        when(jobHistoryRepo.findAll(pageable)).thenReturn(jobHistoryPage);

        Page<JobHistory> result = jobHistoryRepo.findAll(pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        verify(jobHistoryRepo, times(1)).findAll(pageable);
    }

    @Test
    void testFindAll() {
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2, jobHistory3);
        when(jobHistoryRepo.findAll()).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(jobHistoryRepo, times(1)).findAll();
    }

    @Test
    void testDeleteById() {
        doNothing().when(jobHistoryRepo).deleteById(jobHistoryId1);

        jobHistoryRepo.deleteById(jobHistoryId1);

        verify(jobHistoryRepo, times(1)).deleteById(jobHistoryId1);
    }

    @Test
    void testDelete() {
        doNothing().when(jobHistoryRepo).delete(jobHistory1);

        jobHistoryRepo.delete(jobHistory1);

        verify(jobHistoryRepo, times(1)).delete(jobHistory1);
    }

    @Test
    void testExistsById() {
        when(jobHistoryRepo.existsById(jobHistoryId1)).thenReturn(true);

        boolean exists = jobHistoryRepo.existsById(jobHistoryId1);

        assertTrue(exists);
        verify(jobHistoryRepo, times(1)).existsById(jobHistoryId1);
    }

    @Test
    void testExistsById_NotFound() {
        JobHistory.JobHistoryId nonExistentId = new JobHistory.JobHistoryId(999, LocalDate.of(2010, 1, 1));
        when(jobHistoryRepo.existsById(nonExistentId)).thenReturn(false);

        boolean exists = jobHistoryRepo.existsById(nonExistentId);

        assertFalse(exists);
        verify(jobHistoryRepo, times(1)).existsById(nonExistentId);
    }

    @Test
    void testCount() {
        when(jobHistoryRepo.count()).thenReturn(3L);

        long count = jobHistoryRepo.count();

        assertEquals(3L, count);
        verify(jobHistoryRepo, times(1)).count();
    }

    @Test
    void testJobHistoryIdEquality() {
        JobHistory.JobHistoryId id1 = new JobHistory.JobHistoryId(101, LocalDate.of(2013, 1, 15));
        JobHistory.JobHistoryId id2 = new JobHistory.JobHistoryId(101, LocalDate.of(2013, 1, 15));
        JobHistory.JobHistoryId id3 = new JobHistory.JobHistoryId(200, LocalDate.of(2012, 6, 7));

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testFindByEmployeeId_EmptyResult() {
        when(jobHistoryRepo.findByEmployeeId(999)).thenReturn(Arrays.asList());

        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jobHistoryRepo, times(1)).findByEmployeeId(999);
    }

    @Test
    void testFindByJobId_MultipleResults() {
        JobHistory jobHistory4 = JobHistory.builder()
                .employeeId(103)
                .startDate(LocalDate.of(2016, 1, 3))
                .endDate(LocalDate.of(2018, 12, 31))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory4);
        when(jobHistoryRepo.findByJobId("IT_PROG")).thenReturn(jobHistories);

        List<JobHistory> result = jobHistoryRepo.findByJobId("IT_PROG");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(jh -> "IT_PROG".equals(jh.getJobId())));
        verify(jobHistoryRepo, times(1)).findByJobId("IT_PROG");
    }

    @Test
    void testFindByDepartmentId_EmptyResult() {
        when(jobHistoryRepo.findByDepartmentId(999L)).thenReturn(Arrays.asList());

        List<JobHistory> result = jobHistoryRepo.findByDepartmentId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jobHistoryRepo, times(1)).findByDepartmentId(999L);
    }

    @Test
    void testSaveJobHistory_WithNullEndDate() {
        when(jobHistoryRepo.save(any(JobHistory.class))).thenReturn(jobHistory2);

        JobHistory savedJobHistory = jobHistoryRepo.save(jobHistory2);

        assertNotNull(savedJobHistory);
        assertNull(savedJobHistory.getEndDate());
        assertEquals("AD_VP", savedJobHistory.getJobId());
        verify(jobHistoryRepo, times(1)).save(jobHistory2);
    }
}
