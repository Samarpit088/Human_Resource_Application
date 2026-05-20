package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource(exported = false)
public interface DepartmentRepo
        extends JpaRepository<Department, Long> {
    
    @EntityGraph(attributePaths = {"manager", "location"})
    @Override
    Page<Department> findAll(Pageable pageable);
    
    @EntityGraph(attributePaths = {"manager", "location"})
    @Override
    Optional<Department> findById(Long id);
    
    List<Department> findByLocationLocationId(Long locationId);
}
