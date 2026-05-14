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
        // Setup test data
        jobHistoryId1 = new JobHistory.JobHistoryId(101, LocalDate.of(2020, 1, 15));
        jobHistoryId2 = new JobHistory.JobHistoryId(101, LocalDate.of(2022, 6, 1));

        jobHistory1 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2020, 1, 15))
                .endDate(LocalDate.of(2022, 5, 31))
                .jobId("IT_PROG")
                .departmentId(60L)
                .build();

        jobHistory2 = JobHistory.builder()
                .employeeId(101)
                .startDate(LocalDate.of(2022, 6, 1))
                .endDate(null)
                .jobId("IT_MANAGER")
                .departmentId(60L)
                .build();

        jobHistory3 = JobHistory.builder()
                .employeeId(102)
                .startDate(LocalDate.of(2021, 3, 10))
                .endDate(LocalDate.of(2023, 12, 31))
                .jobId("IT_PROG")
                .departmentId(90L)
                .build();
    }

    @Test
    void testSaveJobHistory() {
        // Arrange
        when(jobHistoryRepo.save(any(JobHistory.class))).thenReturn(jobHistory1);

        // Act
        JobHistory savedJobHistory = jobHistoryRepo.save(jobHistory1);

        // Assert
        assertNotNull(savedJobHistory);
        assertEquals(101, savedJobHistory.getEmployeeId());
        assertEquals("IT_PROG", savedJobHistory.getJobId());
        assertEquals(60L, savedJobHistory.getDepartmentId());
        verify(jobHistoryRepo, times(1)).save(jobHistory1);
    }

    @Test
    void testFindById() {
        // Arrange
        when(jobHistoryRepo.findById(jobHistoryId1)).thenReturn(Optional.of(jobHistory1));

        // Act
        Optional<JobHistory> foundJobHistory = jobHistoryRepo.findById(jobHistoryId1);

        // Assert
        assertTrue(foundJobHistory.isPresent());
        assertEquals(101, foundJobHistory.get().getEmployeeId());
        assertEquals(LocalDate.of(2020, 1, 15), foundJobHistory.get().getStartDate());
        verify(jobHistoryRepo, times(1)).findById(jobHistoryId1);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        JobHistory.JobHistoryId nonExistentId = new JobHistory.JobHistoryId(999, LocalDate.of(2020, 1, 1));
        when(jobHistoryRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<JobHistory> foundJobHistory = jobHistoryRepo.findById(nonExistentId);

        // Assert
        assertFalse(foundJobHistory.isPresent());
        verify(jobHistoryRepo, times(1)).findById(nonExistentId);
    }

    @Test
    void testFindByEmployeeId() {
        // Arrange
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2);
        when(jobHistoryRepo.findByEmployeeId(101)).thenReturn(jobHistories);

        // Act
        List<JobHistory> result = jobHistoryRepo.findByEmployeeId(101);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getEmployeeId());
        assertEquals(101, result.get(1).getEmployeeId());
        verify(jobHistoryRepo, times(1)).findByEmployeeId(101);
    }

    @Test
    void testFindByEmployeeIdWithPageable() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobHistory> jobHistoryPage = new PageImpl<>(Arrays.asList(jobHistory1, jobHistory2), pageable, 2);
        when(jobHistoryRepo.findByEmployeeId(101, pageable)).thenReturn(jobHistoryPage);

        // Act
        Page<JobHistory> result = jobHistoryRepo.findByEmployeeId(101, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(jobHistoryRepo, times(1)).findByEmployeeId(101, pageable);
    }

    @Test
    void testFindByJobId() {
        // Arrange
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory3);
        when(jobHistoryRepo.findByJobId("IT_PROG")).thenReturn(jobHistories);

        // Act
        List<JobHistory> result = jobHistoryRepo.findByJobId("IT_PROG");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("IT_PROG", result.get(0).getJobId());
        assertEquals("IT_PROG", result.get(1).getJobId());
        verify(jobHistoryRepo, times(1)).findByJobId("IT_PROG");
    }

    @Test
    void testFindByDepartmentId() {
        // Arrange
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2);
        when(jobHistoryRepo.findByDepartmentId(60L)).thenReturn(jobHistories);

        // Act
        List<JobHistory> result = jobHistoryRepo.findByDepartmentId(60L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(60L, result.get(0).getDepartmentId());
        assertEquals(60L, result.get(1).getDepartmentId());
        verify(jobHistoryRepo, times(1)).findByDepartmentId(60L);
    }

    @Test
    void testFindByStartDateBetween() {
        // Arrange
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 12, 31);
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory3);
        when(jobHistoryRepo.findByStartDateBetween(startDate, endDate)).thenReturn(jobHistories);

        // Act
        List<JobHistory> result = jobHistoryRepo.findByStartDateBetween(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getStartDate().isAfter(startDate.minusDays(1)));
        assertTrue(result.get(0).getStartDate().isBefore(endDate.plusDays(1)));
        verify(jobHistoryRepo, times(1)).findByStartDateBetween(startDate, endDate);
    }

    @Test
    void testFindAllWithPageable() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobHistory> jobHistoryPage = new PageImpl<>(
                Arrays.asList(jobHistory1, jobHistory2, jobHistory3), 
                pageable, 
                3
        );
        when(jobHistoryRepo.findAll(pageable)).thenReturn(jobHistoryPage);

        // Act
        Page<JobHistory> result = jobHistoryRepo.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        verify(jobHistoryRepo, times(1)).findAll(pageable);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<JobHistory> jobHistories = Arrays.asList(jobHistory1, jobHistory2, jobHistory3);
        when(jobHistoryRepo.findAll()).thenReturn(jobHistories);

        // Act
        List<JobHistory> result = jobHistoryRepo.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(jobHistoryRepo, times(1)).findAll();
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(jobHistoryRepo).deleteById(jobHistoryId1);

        // Act
        jobHistoryRepo.deleteById(jobHistoryId1);

        // Assert
        verify(jobHistoryRepo, times(1)).deleteById(jobHistoryId1);
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(jobHistoryRepo).delete(jobHistory1);

        // Act
        jobHistoryRepo.delete(jobHistory1);

        // Assert
        verify(jobHistoryRepo, times(1)).delete(jobHistory1);
    }

    @Test
    void testExistsById() {
        // Arrange
        when(jobHistoryRepo.existsById(jobHistoryId1)).thenReturn(true);

        // Act
        boolean exists = jobHistoryRepo.existsById(jobHistoryId1);

        // Assert
        assertTrue(exists);
        verify(jobHistoryRepo, times(1)).existsById(jobHistoryId1);
    }

    @Test
    void testExistsById_NotFound() {
        // Arrange
        JobHistory.JobHistoryId nonExistentId = new JobHistory.JobHistoryId(999, LocalDate.of(2020, 1, 1));
        when(jobHistoryRepo.existsById(nonExistentId)).thenReturn(false);

        // Act
        boolean exists = jobHistoryRepo.existsById(nonExistentId);

        // Assert
        assertFalse(exists);
        verify(jobHistoryRepo, times(1)).existsById(nonExistentId);
    }

    @Test
    void testCount() {
        // Arrange
        when(jobHistoryRepo.count()).thenReturn(3L);

        // Act
        long count = jobHistoryRepo.count();

        // Assert
        assertEquals(3L, count);
        verify(jobHistoryRepo, times(1)).count();
    }

    @Test
    void testJobHistoryIdEquality() {
        // Test composite key equality
        JobHistory.JobHistoryId id1 = new JobHistory.JobHistoryId(101, LocalDate.of(2020, 1, 15));
        JobHistory.JobHistoryId id2 = new JobHistory.JobHistoryId(101, LocalDate.of(2020, 1, 15));
        JobHistory.JobHistoryId id3 = new JobHistory.JobHistoryId(102, LocalDate.of(2020, 1, 15));

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
