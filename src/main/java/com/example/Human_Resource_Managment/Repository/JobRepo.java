package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepo extends JpaRepository<Job,String> {
}
