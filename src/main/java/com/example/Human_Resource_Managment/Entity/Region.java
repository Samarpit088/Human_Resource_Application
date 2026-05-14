package com.example.Human_Resource_Managment.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Region {
    @Id
    long region_Id;
    String region_Name;
}
