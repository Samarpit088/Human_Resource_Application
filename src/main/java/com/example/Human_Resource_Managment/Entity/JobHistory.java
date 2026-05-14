package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "job_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(JobHistory.JobHistoryId.class)
public class JobHistory {

    @Id
    @Column(name = "employee_id", precision = 6, scale = 0)
    private Integer employeeId;

    @Id
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "job_id", length = 10)
    private String jobId;

    @Column(name = "department_id", precision = 4, scale = 0)
    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Employees employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id", insertable = false, updatable = false)
    @ToString.Exclude
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "department_Id", insertable = false, updatable = false)
    @ToString.Exclude
    private Department department;

    // Composite Primary Key Class
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class JobHistoryId implements Serializable {
        private Integer employeeId;
        private LocalDate startDate;
    }
}
