package com.example.Human_Resource_Managment.Projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface UpdateJobResponseProjection {
    
    Long getEmployeeId();
    
    String getEmployeeName();
    
    String getJobId();
    
    String getJobTitle();
    
    Long getDepartmentId();
    
    String getDepartmentName();
    
    BigDecimal getSalary();
    
    LocalDate getStartDate();
    
    LocalDate getEndDate();
    
    String getMessage();
    
    Boolean getSuccess();
}
