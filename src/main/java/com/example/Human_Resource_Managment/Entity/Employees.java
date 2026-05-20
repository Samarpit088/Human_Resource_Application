package com.example.Human_Resource_Managment.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employees {

    @Id
    @Column(name = "employee_id")
    @NotNull(message = "Employee ID cannot be null")
    @Digits(integer = 6, fraction = 0,
            message = "Employee ID can have maximum 6 digits")
    private Long employeeId;

    @Column(name = "first_name", length = 20)
    @Size(max = 20,
            message = "First name cannot exceed 20 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 25)
    @NotBlank(message = "Last name is required")
    @Size(max = 25,
            message = "Last name cannot exceed 25 characters")
    private String lastName;

    @Column(nullable = false, unique = true, length = 25)
    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid email format")
    @Size(max = 25,
            message = "Email cannot exceed 25 characters")
    private String email;

    @Column(name = "phone_number", length = 20)
    @Size(max = 20,
            message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Column(name = "hire_date", nullable = false)
    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    @Column(precision = 8, scale = 2)
    @Digits(integer = 6, fraction = 2,
            message = "Salary can have max 6 integer digits and 2 decimal places")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    @Column(name = "commission_pct", precision = 2, scale = 2)
    @Digits(integer = 0, fraction = 2,
            message = "Commission percentage can have maximum 2 decimal places")
    @DecimalMin(value = "0.00",
            message = "Commission percentage cannot be negative")
    @DecimalMax(value = "0.99",
            message = "Commission percentage cannot exceed 0.99")
    private BigDecimal commissionPct;

    /*
        MANY EMPLOYEES -> ONE JOB
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    @NotNull(message = "Job is required")
    private Job job;

    /*
        MANY EMPLOYEES -> ONE DEPARTMENT
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /*
        MANY EMPLOYEES -> ONE MANAGER
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employees manager;

    /*
        ONE EMPLOYEE -> MANY JOB HISTORY RECORDS
     */
    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<JobHistory> jobHistories;
}