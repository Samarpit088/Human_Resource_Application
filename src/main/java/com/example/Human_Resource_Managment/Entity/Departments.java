package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Departments {
    @Id
    long department_Id;
    String department_name;

    long manager_id;

    long location_id;
}
