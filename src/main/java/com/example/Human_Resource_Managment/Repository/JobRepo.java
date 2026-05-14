package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "jobs", path = "jobs")
public interface JobRepo extends JpaRepository<Job,String> {
    Page<Job> findAll(Pageable pageable);
}
