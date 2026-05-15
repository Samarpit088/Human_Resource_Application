package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.JobHistory;
import com.example.Human_Resource_Managment.Entity.JobHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobHistoryRepo extends JpaRepository<JobHistory, JobHistoryId> {

    List<JobHistory> findByIdEmployeeId(Integer employeeId);
}
