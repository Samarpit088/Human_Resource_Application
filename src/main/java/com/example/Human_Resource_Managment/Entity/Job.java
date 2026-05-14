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
@Table(name = "jobs")
@Data
public class Job {
    @Id
    @NotBlank(message = "Job ID is required")
    @Size(max = 10)
    @Column(name = "job_id",length=10)
    String job_id;

    @NotBlank(message = "Job title cannot be empty")
    @Size(max = 35)
    @Column(name = "job_title", nullable = false,length = 35)
    String job_title;

    @PositiveOrZero(message = "Minimum salary must be zero or greater")
    @Column(name = "min_salary",precision = 6, scale = 0)
    long min_salary;

    @PositiveOrZero(message = "Maximum salary must be zero or greater")
    @Column(name = "max_salary",precision = 6, scale = 0)
    long max_salary;
}
