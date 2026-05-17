package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import com.example.Human_Resource_Managment.Entity.JobHistoryId;
import com.example.Human_Resource_Managment.Projection.CurrentJobHistoryProjection;
import com.example.Human_Resource_Managment.Projection.JobHistoryDetailProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RepositoryRestResource(exported = false)
public interface JobHistoryRepo extends JpaRepository<JobHistory, JobHistoryId> {

    // Find all job history records for an employee
    List<JobHistory> findByIdEmployeeId(Long employeeId);
    
    // Find all job history records for an employee ordered by start date descending
    List<JobHistory> findByIdEmployeeIdOrderByIdStartDateDesc(Long employeeId);
    
    // Find job history record by employee and end date (use FAR_FUTURE_DATE for currently working)
    Optional<JobHistory> findByIdEmployeeIdAndEndDate(Long employeeId, LocalDate endDate);
    
    // Find all job history records by employee and end date
    List<JobHistory> findAllByIdEmployeeIdAndEndDate(Long employeeId, LocalDate endDate);
    
    // Find records by employee and start date before a given date
    List<JobHistory> findByIdEmployeeIdAndIdStartDateBefore(Long employeeId, LocalDate startDate);
    
    // Find records by employee and start date after a given date
    List<JobHistory> findByIdEmployeeIdAndIdStartDateAfter(Long employeeId, LocalDate startDate);
    
    // Find records by employee and end date after a given date
    List<JobHistory> findByIdEmployeeIdAndEndDateAfter(Long employeeId, LocalDate date);
    
    // Find records by employee and start date between two dates
    List<JobHistory> findByIdEmployeeIdAndIdStartDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    // Projection: Find job history with projection by employee and end date
    <T> Optional<T> findByIdEmployeeIdAndEndDate(Long employeeId, LocalDate endDate, Class<T> type);
    
    // Projection: Find all job history details with projection
    <T> List<T> findByIdEmployeeIdOrderByIdStartDateDesc(Long employeeId, Class<T> type);
}
