package com.example.Human_Resource_Managment.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department {

    @Id
    @Column(name = "department_id", precision = 4, scale = 0)
    @NotNull(message = "Department id is required")
    @Min(value = 1, message = "Department id must be positive")
    @Max(value = 9999, message = "Department id cannot exceed 4 digits")
    private Long departmentId;

    @Column(name = "department_name", nullable = false, length = 30)
    @NotBlank(message = "Department name is required")
    @Size(max = 30, message = "Department name cannot exceed 30 characters")
    private String departmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employees manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Locations location;

    @OneToMany(mappedBy = "department")
    private List<Employees> employees;
}