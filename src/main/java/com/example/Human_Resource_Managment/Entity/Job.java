package com.example.Human_Resource_Managment.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {
    @Id
    @NotBlank(message = "Job ID is required")
    @Size(max = 10)
    @Column(name = "job_id",length=10)
    private String jobId;

    @NotBlank(message = "Job title cannot be empty")
    @Size(max = 35)
    @Column(name = "job_title", nullable = false,length = 35)
    private String jobTitle;

    @PositiveOrZero(message = "Minimum salary must be zero or greater")
    @Column(name = "min_salary",precision = 6, scale = 0)
    private BigDecimal minSalary;

    @PositiveOrZero(message = "Maximum salary must be zero or greater")
    @Column(name = "max_salary",precision = 6, scale = 0)
    private BigDecimal maxSalary;
}
