package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Departments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepo extends JpaRepository<Departments,Long> {

    Page<Departments>findAll(Pageable pageable);
}
