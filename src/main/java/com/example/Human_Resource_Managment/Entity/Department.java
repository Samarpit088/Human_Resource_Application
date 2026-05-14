package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @PositiveOrZero(message = "Department ID must be zero or greater")
    @Column(name = "department_id", precision = 4, scale = 0)
    long department_id;

    @NotBlank(message = "Department name cannot be empty")
    @Size(max = 30)
    @Column(name = "department_name", nullable = false, length = 30)
    String department_name;

    @PositiveOrZero(message = "Manager ID must be zero or greater")
    @Column(name = "manager_id", precision = 6, scale = 0)
    long manager_id;

    @PositiveOrZero(message = "Location ID must be zero or greater")
    @Column(name = "location_id", precision = 4, scale = 0)
    long location_id;
}
