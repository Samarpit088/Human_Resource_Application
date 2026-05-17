package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Job;
import com.example.Human_Resource_Managment.Projection.JobListProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "jobs", excerptProjection = JobListProjection.class)
public interface JobRepo extends JpaRepository<Job,String> {
}
