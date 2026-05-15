package com.example.Human_Resource_Managment.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Human_Resource_Managment.Entity.Region;

@Repository
public interface RegionRepo extends JpaRepository<Region, Integer> {


}