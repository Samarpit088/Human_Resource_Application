package com.example.Human_Resource_Managment.Repository;

import com.example.Human_Resource_Managment.Entity.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobsRepo extends JpaRepository<Jobs,Long> {
    Page<Jobs> findAll(Pageable pageable);
}
