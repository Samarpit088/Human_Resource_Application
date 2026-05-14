package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobHistoryRepo extends JpaRepository<JobHistory, JobHistory.JobHistoryId> {

    List<JobHistory> findByEmployeeId(Integer employeeId);
    
    Page<JobHistory> findByEmployeeId(Integer employeeId, Pageable pageable);
    
    List<JobHistory> findByJobId(String jobId);
    
    List<JobHistory> findByDepartmentId(Long departmentId);
    
    List<JobHistory> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    Page<JobHistory> findAll(Pageable pageable);
}
