package com.example.Human_Resource_Managment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobHistoryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String jobId;
    private String jobTitle;
    private Long departmentId;
    private String departmentName;
    private BigDecimal currentSalary;
    private boolean currentlyWorking;
}
