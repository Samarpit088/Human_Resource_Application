package com.example.Human_Resource_Managment.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface JobHistoryDetailProjection {
    
    Long getEmployeeId();
    
    String getEmployeeFirstName();
    
    String getEmployeeLastName();
    
    LocalDate getStartDate();
    
    LocalDate getEndDate();
    
    String getJobId();
    
    String getJobTitle();
    
    Long getDepartmentId();
    
    String getDepartmentName();
    
    BigDecimal getSalary();
}
