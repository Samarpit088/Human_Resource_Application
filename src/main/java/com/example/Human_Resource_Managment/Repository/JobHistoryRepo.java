package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobHistoryRepo extends JpaRepository<JobHistory,Long> {
    List<JobHistory> findByEmployeeId(Long employeeId);
}
