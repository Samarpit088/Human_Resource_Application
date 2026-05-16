package com.example.Human_Resource_Managment.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface UpdateJobRequestProjection {
    
    Long getEmployeeId();
    
    String getJobId();
    
    Long getDepartmentId();
    
    BigDecimal getSalary();
    
    LocalDate getStartDate();
    
    LocalDate getEndDate();
    
    String getReason();
}
