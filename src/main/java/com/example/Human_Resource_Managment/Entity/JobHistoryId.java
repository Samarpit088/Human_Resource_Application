package com.example.Human_Resource_Managment.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobHistoryId implements Serializable {

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "start_date")
    private LocalDate startDate;
}
